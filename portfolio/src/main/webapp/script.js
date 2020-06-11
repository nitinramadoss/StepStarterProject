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

async function getPhrase(){
    let max = document.getElementById("max-comments").value;
    let response = await fetch('/load-data?numCommentsDisplay=' + max);
    let list = await response.json(); //list of entities from datastore
    let commentHistory = document.getElementById("section"); //UI for displaying comments

    commentHistory.innerHTML = '';

    for(const element of list){
        let commentSection = document.createElement("DIV");
        commentSection.setAttribute("id", "dynamic-history");
        let addName = document.createTextNode(element.name + ": ");
        let addMessage = document.createTextNode(element.message);
        commentSection.appendChild(addName);  
        commentSection.appendChild(addMessage);  
        document.getElementById("section").appendChild(commentSection);
    }    
}

async function removePhrase(){
    let response = await fetch('/delete-data',  {method:'post'});
    getPhrase();
}

function loadChart(){
    let stats = new google.visualization.DataTable();
      stats.addColumn('string', 'Action');
      stats.addColumn('number', 'Percentage');
      stats.addRows([
        ['Programming', 0.65],
        ['Biking', 0.10],
        ['Gaming', 0.25]
      ]);

      // Instantiate and draw the chart.
      var chart = new google.visualization.PieChart(document.getElementById('activityPieChart'));
      chart.draw(stats, null);
}
  
  

