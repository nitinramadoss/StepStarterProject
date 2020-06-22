// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> availableTimesForMandatory = new ArrayList<TimeRange>();
    List<TimeRange> availableTimesForOptional = new ArrayList<TimeRange>();
    List<TimeRange> unavailableTimesForMandatory = new ArrayList<TimeRange>();
    List<TimeRange> unavailableTimesForOptional = new ArrayList<TimeRange>();
    
    for (Event e : events) {
        if (!mandatoryAttendeesFree(e, request)) {
            unavailableTimesForMandatory.add(e.getWhen());
        }

        if (!optionalAttendeesFree(e, request)) {
            unavailableTimesForOptional.add(e.getWhen());
        }
    }

    Collections.sort(unavailableTimesForMandatory, TimeRange.ORDER_BY_START);
    Collections.sort(unavailableTimesForOptional, TimeRange.ORDER_BY_START);

    if (unavailableTimesForMandatory.size() > 0) { // Do this only for mandatory attendees since only one optional attendee exists
        combineOverlaps(unavailableTimesForMandatory, unavailableTimesForMandatory.get(0), 0);
    }

    findAvailableRanges(unavailableTimesForMandatory, availableTimesForMandatory, request);
    findAvailableRanges(unavailableTimesForOptional, availableTimesForOptional, request);

    List<TimeRange> finalAvailableTimes = new ArrayList<TimeRange>();

    if (request.getOptionalAttendees().size() == 0) {
        finalAvailableTimes = availableTimesForMandatory;
    } else if (request.getAttendees().size() == 0) {
        finalAvailableTimes = availableTimesForOptional; 
    } else if (availableTimesForMandatory.size() == 0) {
        finalAvailableTimes = availableTimesForOptional;
    } else if (availableTimesForOptional.size() == 0) { 
        finalAvailableTimes = availableTimesForMandatory;
    } else {
        for (TimeRange m : availableTimesForMandatory) {
            for (TimeRange o : availableTimesForOptional) {
                TimeRange encompassed = null;
    
                if (m.start() >= o.start() && m.end() <= o.end()) {
                    encompassed = m;
                } else if (o.start() >= m.start() && o.end() <= m.end()) {
                    encompassed = o;
                }

                if (encompassed != null) {
                    finalAvailableTimes.add(encompassed);
                }               
            }   
        }

        if (finalAvailableTimes.size() == 0) {
            finalAvailableTimes = availableTimesForMandatory;
        }
    }

    return finalAvailableTimes;   
  }
 
  public boolean mandatoryAttendeesFree(Event e, MeetingRequest request){
    return Collections.disjoint(e.getAttendees(), request.getAttendees());
  }

  public boolean optionalAttendeesFree(Event e, MeetingRequest request){
    return Collections.disjoint(e.getAttendees(), request.getOptionalAttendees());
  }

  public void addAvailableRanges(List<TimeRange> availableTimes, TimeRange range, MeetingRequest request){
    if (range.start() < range.end() && range.duration() >= request.getDuration()) {
        availableTimes.add(range);
    }
  }

  public void combineOverlaps(List<TimeRange> unavailableTimes, TimeRange previous, int index){
    if (index > unavailableTimes.size()-1) {
        return;
    }

    TimeRange current = unavailableTimes.get(index);

    if (current.overlaps(previous)) {
        TimeRange earlierRange = (current.start() < previous.start()) ? current : previous;
        TimeRange laterRange = (current.end() > previous.end()) ? current : previous;
        TimeRange combinedRange = TimeRange.fromStartEnd(earlierRange.start(), laterRange.end(), false);
        int initialIndex = unavailableTimes.indexOf(previous);

        unavailableTimes.remove(previous);
        unavailableTimes.remove(current);
        unavailableTimes.add(initialIndex, combinedRange);

        combineOverlaps(unavailableTimes, combinedRange, initialIndex + 1);
    }

    combineOverlaps(unavailableTimes, current, index + 1);
  }

  public void findAvailableRanges(List<TimeRange> unavailableTimes, List<TimeRange> availableTimes, MeetingRequest request){
        int start = 0;
        for (TimeRange range : unavailableTimes) {
            TimeRange availableRange = TimeRange.fromStartEnd(start, range.start(), false);
            
            addAvailableRanges(availableTimes, availableRange, request);
            
            start = range.end();
        }

    TimeRange lastAvailableRange = TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY + 1, false);

    addAvailableRanges(availableTimes, lastAvailableRange, request);
  }
}