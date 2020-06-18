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

package com.google.sps.data;
/* This object represents each comment posted to the website {Name: Comment} */
public class Comment
{
    private long id;
    private String name;
    private String message;
    private double score;

    public Comment(long id, String name, String message, double score){
        this.id = id;
        this.name = name;
        this.message = message;
        this.score = score;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getMessage(){
        return message;
    }

    public double getScore(){
        return score;
    }
}
