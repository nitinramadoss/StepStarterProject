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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
/** Servlet that adds comments to datastore */
@WebServlet("/data") 
public class DataServlet extends HttpServlet 
{ 
  private LanguageServiceClient languageService;
  
  public void init() throws ServletException {
    try {
        languageService = LanguageServiceClient.create();
    } catch (Exception e) {
        throw new ServletException("Servlet exception thrown", e);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String name = request.getParameter("user-name");
    String message = request.getParameter("user-comment");
    long timestamp = System.currentTimeMillis();

    if (name.length() != 0 && message.length() != 0) {
        Entity taskEntity = new Entity("Comment");

        taskEntity.setProperty("name", name);
        taskEntity.setProperty("message", message);
        taskEntity.setProperty("score", getSentimentScore(message));
        taskEntity.setProperty("timestamp", timestamp);
        datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html#comments-panel");
  }

  public double getSentimentScore(String message) throws IOException {
    Document doc = Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    double score = sentiment.getScore();

    return score;
  }
  
  public void destroy(){
    languageService.close();
  }
}
