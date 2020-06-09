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
 
package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/** Servlet that adds comments to datastore */
@WebServlet("/data") 
public class DataServlet extends HttpServlet 
{ 
<<<<<<< HEAD:portfolio/src/main/java/com/google/sps/servlets/DataServlet.java
=======
  private List<String> comments;

  @Override
  public void init() {
    comments = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

>>>>>>> master:portfolio/src/main/java/com/google/DataServlet.java
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String name = request.getParameter("user-name");
    String message = request.getParameter("user-comment");
    long timestamp = System.currentTimeMillis();

    if(name.length() != 0 && message.length() != 0){
        Entity taskEntity = new Entity("Comment");
        taskEntity.setProperty("name", name);
        taskEntity.setProperty("message", message);
        taskEntity.setProperty("timestamp", timestamp);
        datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html#comments-panel");
  }
}
