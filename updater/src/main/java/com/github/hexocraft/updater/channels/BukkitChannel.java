/**
 *    Copyright 2015-2020 hexosse <hexosse@gmail.com>
 *
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.hexocraft.updater.channels;

import com.github.hexocraft.updater.Update;
import com.github.hexocraft.updater.Version;
import com.github.hexocraft.updater.enumeration.Release;
import com.github.hexocraft.updater.enumeration.Result;
import com.github.hexocraft.updater.utilities.HttpConnection;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class BukkitChannel implements Channel {

    /**
     * Host to contact
     */
    static String host = "https://api.curseforge.com";

    /**
     * Repository query
     */
    static String query = "/servermods/files?projectIds={{ PROJECT_ID }}";

    /**
     * URL to query.
     */
    private URL url;

    /**
     * API Key.
     */
    private String apiKey;

    /**
     * Ressource ID.
     */
    private String projectId;


    public BukkitChannel(String projectId) {
        this(null, projectId);
    }

    public BukkitChannel(String apiKey, String projectId) {
        try {
            this.apiKey = apiKey;
            this.projectId = projectId;
            this.url = new URL((host + query).replace("{{ PROJECT_ID }}", this.projectId));
        }
        catch(Exception ignored) {
        }
    }

    @Override
    public Pair<Result,Update> read() {
        try {
            HttpURLConnection connection = HttpConnection.Connection(url);
            if(apiKey != null) connection.addRequestProperty("X-API-Key", apiKey);
            connection = HttpConnection.Open(connection);
            int status = connection.getResponseCode();

            // The file is present in the repository
            if((status >= 200 && status < 300) || status == 304) {

                // Read response
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));

                // Convert to json
                JsonArray releases = new Gson().fromJson(response, JsonArray.class);

                // No release found
                if(response.isEmpty() || releases.size() == 0) {
                    return new Pair<>(Result.REPO_NO_RELEASES, null);
                }

                // Get the latest release
                JsonObject release = releases.get(releases.size() - 1).getAsJsonObject();
                String name = release.get("name").getAsString();
                String downloadUrl = release.get("downloadUrl").getAsString();
                String fileName = release.get("fileName").getAsString();
                Release type = release.get("releaseType").getAsString().equals("release") ? Release.RELEASE : Release.PRE_RELEASE;

                // Get version from release name
                Version version = Version.parse(fileName.replaceFirst("[.][^.]+$", "") + (type == Release.PRE_RELEASE ? "-pre-release" : ""));

                // Create new update
                if(version != null) {
                    Update update = new Update(name, version, new URL(downloadUrl));
                    return new Pair<>(Result.SUCCESS, update);
                }

                return new Pair<>(Result.ERROR, null);
            }

            return new Pair<>(Result.ERROR, null);
        }
        catch(Exception e) {
            return new Pair<>(Result.ERROR, null);
        }
    }
}
