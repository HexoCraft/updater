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
import com.github.hexocraft.updater.enumeration.Result;
import com.github.hexocraft.updater.utilities.HttpConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class SpigotChannel implements Channel {

    /**
     * Host to contact
     */
    static String spiget = "http://api.spiget.org";
    static String spigot = "http://www.spigotmc.org";

    /**
     * Repository query
     */
    static String queryResource = "/v2/resources/{{ RESOURCE_ID }}";
    static String queryVersion = "/v2/resources/{{ RESOURCE_ID }}/versions/latest";
    static String queryUpdate = "/v2/resources/{{ RESOURCE_ID }}/updates/latest";

    /**
     * URL to query.
     */
    private URL queryResourceUrl;
    private URL queryVersionUrl;
    private URL queryUpdateUrl;

    /**
     * Ressource ID.
     */
    private String resourceId;


    public SpigotChannel(String resourceId) {
        try {
            this.resourceId = resourceId;
            this.queryResourceUrl = new URL((spiget + queryResource).replace("{{ RESOURCE_ID }}", this.resourceId));
            this.queryVersionUrl = new URL((spiget + queryVersion).replace("{{ RESOURCE_ID }}", this.resourceId));
            this.queryUpdateUrl = new URL((spiget + queryUpdate).replace("{{ RESOURCE_ID }}", this.resourceId));
        }
        catch(Exception ignored) {
        }
    }

    @Override
    public Pair<Result,Update> read() {
        Pair<Result,Update> resource = getResource();
        Result result = resource.getKey();
        Update update = resource.getValue();

        if(result == Result.SUCCESS) {
            Pair<Result,Update> latestVersion = getLatestVersion(update);
            result = latestVersion.getKey();
            update = latestVersion.getValue();
        }

        if(result == Result.SUCCESS) {
            Pair<Result,Update> latestUpdate = getLatestUpdate(update);
            result = latestUpdate.getKey();
            update = latestUpdate.getValue();
        }

        return new Pair<>(result, update);
    }

    private Pair<Result,Update> getResource() {
        try {
            HttpURLConnection connection = HttpConnection.openConnection(queryResourceUrl);
            int status = connection.getResponseCode();

            // The file is present in the repository
            if((status >= 200 && status < 300) || status == 304) {

                // Read response
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));

                // No release found
                if(response.isEmpty()) {
                    return new Pair<>(Result.REPO_NO_RELEASES, null);
                }

                // Convert to json
                JsonObject resource = new Gson().fromJson(response, JsonObject.class);

                // Get version info
                String name = resource.get("name").getAsString();
                String url = resource.get("file").getAsJsonObject().get("url").getAsString();

                // Create new update
                Update update = new Update(name, null, new URL(spigot + "/" + url));

                return new Pair<>(Result.SUCCESS, update);
            }

            return new Pair<>(Result.ERROR, null);
        }
        catch(Exception e) {
            return new Pair<>(Result.ERROR, null);
        }
    }

    private Pair<Result,Update> getLatestVersion(Update update) {
        try {
            HttpURLConnection connection = HttpConnection.openConnection(queryVersionUrl);
            int status = connection.getResponseCode();

            // The file is present in the repository
            if((status >= 200 && status < 300) || status == 304) {

                // Read response
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));

                // No release found
                if(response.isEmpty()) {
                    return new Pair<>(Result.REPO_NO_RELEASES, null);
                }

                // Convert to json
                JsonObject latestVersion = new Gson().fromJson(response, JsonObject.class);

                // Get version info
                String name = latestVersion.get("name").getAsString();

                // Get version from release name
                Version version = Version.parse(name);

                // Create new update
                if(version != null) {
                    update.setVersion(version);
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

    private Pair<Result,Update> getLatestUpdate(Update update) {
        try {
            HttpURLConnection connection = HttpConnection.openConnection(queryUpdateUrl);
            int status = connection.getResponseCode();

            // The file is present in the repository
            if((status >= 200 && status < 300) || status == 304) {

                // Read response
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));

                // No release found
                if(response.isEmpty()) {
                    return new Pair<>(Result.SUCCESS, null);
                }

                // Convert to json
                JsonObject latestUpdate = new Gson().fromJson(response, JsonObject.class);

                // Get update info
                String description = new String(Base64.getDecoder().decode(latestUpdate.get("description").getAsString()));

                // update
                update.setDescription(description);

                return new Pair<>(Result.SUCCESS, update);
            }

            return new Pair<>(Result.ERROR, null);
        }
        catch(Exception e) {
            return new Pair<>(Result.ERROR, null);
        }
    }
}
