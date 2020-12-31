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

package com.github.hexocraft.updater;

import java.net.URL;

/**
 * Represent an update
 */
public class Update {

    /**
     * Title.
     */
    private String title;

    /**
     * Version.
     */
    private Version version;

    /**
     * Download link.
     */
    private URL downloadUrl;

    /**
     * Changes contained in the update
     */
    private String description;


    public Update(String title, Version version) {
        this(title, version, null, null);
    }

    public Update(String title, Version version, URL downloadUrl) {
        this(title, version, downloadUrl, null);
    }

    public Update(String title, Version version, URL downloadUrl, String description) {
        this.title = title;
        this.version = version;
        this.downloadUrl = downloadUrl;
        this.description = description;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version version() {
        return version;
    }

    public void setDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public URL downloadUrl() {
        return downloadUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * @return joined update string.
     */
    @Override
    public String toString() {
        return title + "-" + version;
    }
}
