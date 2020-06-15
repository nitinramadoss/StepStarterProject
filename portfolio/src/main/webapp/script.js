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

/*global var which holds the number of positive (index 0), neutral (index 1), and negative (index 2) comments */
var commentEmotions = new Array(0,0,0);

async function getPhrase(){
    let max = document.getElementById("max-comments").value;
    let response = await fetch('/load-data?numCommentsDisplay=' + max);
    let list = await response.json(); //list of entities from datastore
    loadCommentChart(); //load chart with entities from datastore
    let commentHistory = document.getElementById("section"); //UI for displaying comments

    commentHistory.innerHTML = '';

    for (const element of list) {
        let commentSection = document.createElement("DIV");
        commentSection.setAttribute("id", "dynamic-history");
        let addName = document.createTextNode(element.name + ": ");
        let addMessage = document.createTextNode(element.message);
        let addScore = document.createTextNode("(" + element.score + ")");
        commentSection.appendChild(addName);  
        commentSection.appendChild(addMessage);  
        commentSection.appendChild(addScore);
        document.getElementById("section").appendChild(commentSection);
    }  
  
}

async function removePhrase(){
    let response = await fetch('/delete-data',  {method:'post'});
    getPhrase();
}

async function loadCommentChart(){
    let commentMap = new Map();
    let response = await fetch('/load-data');
    let list = await response.json();
    let stats = new google.visualization.DataTable();
    
    stats.addColumn('string', 'Name');
    stats.addColumn('number', 'Total Comments');

    countComments(commentMap, list);

    for (let [name, numComments] of commentMap) {      
        stats.addRows([
            [name, numComments],
        ]);      
    }

    // Instantiate and draw the chart.
    let chart = new google.visualization.BarChart(document.getElementById('commentBarChart'));
    chart.draw(stats, null);

    loadSentimentChart(); //load second chart after first one is loaded
}

function loadSentimentChart(){
    let stats = new google.visualization.DataTable();
    
    stats.addColumn('string', 'Sentiment');
    stats.addColumn('number', 'Percentage');
    stats.addRows([
        ['Positive', commentEmotions[0]],
        ['Neutral', commentEmotions[1]],
        ['Negative', commentEmotions[2]],
    ]);

    // Instantiate and draw the chart.
    let chart = new google.visualization.PieChart(document.getElementById('sentimentPieChart'));
    chart.draw(stats, null);
}

function countComments(map, list){  
    for (const element of list) {
        let name = element.name;

        if (map.has(name)) {
            map.set(name, map.get(name) + 1); //add one to current value
        } else {
            map.set(name, 1); //first occurence of that comment
        }

        countSentiment(element); //checks if the comment is positive, neutral, or negative
    }
}

function countSentiment(element){
    if (element.score > 0) {
        commentEmotions[0] += 1;
    } else if(element.score == 0) {
        commentEmotions[1] += 1;
    } else {
        commentEmotions[2] += 1;
    }
}

  


