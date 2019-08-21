/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package transjawi;

// [START example]
import com.google.appengine.api.utils.SystemProperty;

import java.util.*;
import java.io.IOException;
import java.util.Properties;

import javax.json.*;
import javax.json.stream.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "AppEngine", value = "/ajax")
public class AppEngine extends HttpServlet {
    public Translator translator;

    public AppEngine() throws IOException, Lexicon.ParseError {
        Trie<java.util.List<Entry>> trie = Lexicon.load();
        translator = new Translator(trie);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
        /*
        Translator translator;

        try {
            Trie<java.util.List<Entry>> trie = Lexicon.load();
            translator = new Translator(trie);
        }
        catch (Lexicon.ParseError e) {
            response.setContentType("text/plain");
            response.getWriter().println("Hello App Engine - Standard using");
            return;
        }*/
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String source = request.getParameter("input");
        java.util.List<Token> input = translator.scan(source);
        java.util.List<Token> output = new ArrayList<Token>();
        String translation = translator.processTokens(source, input,
            output);
        JsonGenerator json = Json.createGenerator(response.getWriter());
        json.writeStartObject();
        json.writeStartArray("input");
        serialiseTokens(input, source, json);
        json.writeEnd();
        json.writeStartArray("output");
        serialiseTokens(output, translation, json);
        json.writeEnd();
        json.writeEnd();
        json.flush();
    }

    void serialiseTokens(java.util.List<Token> tokens, String source,
            JsonGenerator json) {
        int i = 0;
        for (Token token : tokens) {
            json.writeStartArray();
            json.write(token.getClass().getSimpleName());
            json.write(source.substring(i, i + token.length()));
            json.writeEnd();
            i += token.length();
        }
    }
}
// [END example]
