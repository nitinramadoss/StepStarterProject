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
    let response = await fetch('/load-data');
    let list = await response.json();
 
        for(i = 0; i < list.length; i++){
            let commentSection = document.createElement("DIV");
            commentSection.setAttribute("id", "dynamic-history");
            let addName = document.createTextNode(list[i].name + ": ");
            let addMessage = document.createTextNode(list[i].message);
            commentSection.appendChild(addName);  
            commentSection.appendChild(addMessage);  
            document.getElementById("section").appendChild(commentSection);
        }
}
  
         
