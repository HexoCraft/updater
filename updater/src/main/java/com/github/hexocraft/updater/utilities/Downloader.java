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

package com.github.hexocraft.updater.utilities;

import com.github.hexocraft.updater.Update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Download update from url
 */
public class Downloader {

    /**
     * {@link Update} to download.
     */
    private final Update update;

    /**
     * {@link Path} where the {@link Update} will be downloaded.
     */
    private final Path folder;

    /**
     * User agent
     */
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.75 Safari/535.7";

    /**
     * Download update
     *
     * @param update update to download
     * @param folder folder where the update file will be downloaded
     * @return true on successful download
     * @throws IOException If the file cannot be downloaded
     */
    static public boolean download(Update update, Path folder) throws IOException {
        Downloader downloader = new Downloader(update, folder);
        return downloader.download();
    }

    /**
     * This class cannot be instantiate.
     * Use static functions below.
     *
     * @param update update to download
     * @param folder folder where the update file will be downloaded
     */
    public Downloader(Update update, Path folder) {
        Objects.requireNonNull(update, "update cannot be null");
        Objects.requireNonNull(update.downloadUrl(), "update url cannot be null");

        this.update = update;
        this.folder = folder;
    }

    /**
     * Download update
     *
     * @return true on successful download
     * @throws IOException If the file cannot be downloaded
     */
    public boolean download() throws IOException {
        // Make sure output directory exist
        makeDir(folder);

        // Download update from url
        return downloadFile(update.downloadUrl(), folder);
    }

    /**
     * Create the directory if not exist
     *
     * @param dir Directory to create
     */
    static void makeDir(Path dir) throws IOException {
        Objects.requireNonNull(dir, "dir cannot be null.");

        if (Files.exists(dir)) {
            if (!Files.isDirectory(dir)) {
                Files.delete(dir);
            }
        } else {
            Files.createDirectories(dir);
        }
    }

    /**
     * Download file from url
     *
     * @param url    File url
     * @param output output file
     * @return true on successful download
     * @throws IOException If the file cannot be downloaded
     */
    static boolean downloadFile(URL url, Path output) throws IOException {
        //logger.accept("Downloading file: " + url);

        HttpURLConnection connection = HttpConnection.openConnection(url);
        int status = connection.getResponseCode();

        // The file is present in the repository
        if ((status >= 200 && status < 300) || status == 304) {
            String disposition = connection.getHeaderField("Content-Disposition");
            String fileName = "";
            String fileURL = url.toString();

            // extracts file name from header field
            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0)
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
            }

            // extracts file name from URL
            if (fileName.isEmpty())
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);

            // Test if file already exist
            File outputFile = output.resolve(fileName).toFile();
            if(outputFile.exists()) {
                return true;
            }

            // Download file
            ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(output.resolve(fileName).toFile());
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();

            return Files.exists(output.resolve(fileName));
        } else {
            return false;
        }
    }
}
