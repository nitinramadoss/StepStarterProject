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

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> availableTimes = new ArrayList<TimeRange>();
    List<TimeRange> unavailableTimes = new ArrayList<TimeRange>();

    for (Event e : events) {
        if (!attendeesFree(e, request)) {
            unavailableTimes.add(e.getWhen());
        }
    }

    Collections.sort(unavailableTimes, TimeRange.ORDER_BY_START);

    if (unavailableTimes.size() > 0) {
        combineOverlaps(unavailableTimes, unavailableTimes.get(0), 0);
    }

    int start = 0;
    for (TimeRange range : unavailableTimes) {
        TimeRange availableRange = TimeRange.fromStartEnd(start, range.start(), false);

        addAvailableRanges(availableTimes, availableRange, request);
        
        start = range.end();
    }

    TimeRange lastAvailableRange = TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY+1, false);

    addAvailableRanges(availableTimes, lastAvailableRange, request);
    
    return availableTimes;   
  }
 
  public boolean attendeesFree(Event e, MeetingRequest request){
    return Collections.disjoint(e.getAttendees(), request.getAttendees());
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
}