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

import com.github.hexocraft.updater.channels.Channel;
import com.github.hexocraft.updater.enumeration.Result;
import com.github.hexocraft.updater.utilities.Downloader;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class Updater<U extends Updater<?>> {

    /**
     * Current version.
     */
    private final Version current;

    /**
     * Channel used to find update.
     */
    private final Channel channel;

    /**
     * Download update if available
     */
    private boolean download = true;

    /**
     * Folder where the update will be downloaded.
     */
    private Path output;

    /**
     * Initial delay before stating.
     * Default to 5 seconds.
     */
    private long delay = TimeUnit.SECONDS.toMillis(5);

    /**
     * Period before each tries to find an update.
     * Default to 1 hour.
     */
    private long period = TimeUnit.HOURS.toMillis(1);

    /**
     * onStart will be called at the beginning of the process.
     */
    private Runnable onStart = null;

    /**
     * onFinish will be called at the end of the process.
     */
    private BiConsumer<Result,Update> onFinish = null;

    /**
     * Query result.
     */
    private Result result = Result.NO_UPDATE;

    /**
     * <strong>Latest</strong> update if found.
     */
    private Update update;


    public Updater(Version current, Channel channel) {
        this(current, null, channel);
    }

    public Updater(Version current, Path output, Channel channel) {
        this.current = Objects.requireNonNull(current, "Current version cannot be null");
        this.output = output;
        this.channel = Objects.requireNonNull(channel, "Updater channel cannot be null");
    }

    /**
     * Enable downloading update.
     *
     * @param download true to enable download
     * @return current instance
     */
    public U setDownload(boolean download) {
        this.download = download;
        return (U) this;
    }

    /**
     * Define folder where the update will be downloaded.
     *
     * @param output folder where the update will be downloaded
     * @return current instance
     */
    public U setOutput(Path output) {
        this.output = output;
        return (U) this;
    }

    /**
     * Initial delay before stating.
     *
     * @param delay delay in milliseconds
     * @return current instance
     */
    public U setDelay(long delay) {
        this.delay = delay;
        return (U) this;
    }

    /**
     * Period before each tries to find an update
     *
     * @param period period in milliseconds
     * @return current instance
     */
    public U setPeriod(long period) {
        this.period = period;
        return (U) this;
    }

    /**
     * @param onStart Consumer function which will be called at the beginning of the process
     * @return current instance
     */
    public U onStart(Runnable onStart) {
        this.onStart = onStart;
        return (U) this;
    }

    /**
     * @param onFinish Consumer function which will be called at the end of the process
     * @return current instance
     */
    public U onFinish(BiConsumer<Result,Update> onFinish) {
        this.onFinish = onFinish;
        return (U) this;
    }

    public Result getResult() {
        return result;
    }

    public Update getUpdate() {
        return update;
    }

    /**
     * Schedule updater
     *
     * @return current instance
     */
    public U run() {

        // Call the timer after the defined delay
        if(delay > 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    findUpdate();
                }
            }, delay);
        }

        // Call the timer task periodically
        if(period > 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    findUpdate();
                }
            }, period, period);
        }

        // No timer defined
        if(delay == 0)
            findUpdate();

        return (U) this;
    }

    private void findUpdate() {

        // Updater starting
        if(onStart != null) {
            onStart.run();
        }

        // Find update using defined channel
        Pair<Result,Update> read = channel.read();
        result = read.getKey();
        update = read.getValue();

        // On success
        if(result.equals(Result.SUCCESS)) {
            // Current version is the latest version
            if(! update.version().isGreater(current)) {
                result = Result.NO_UPDATE;
            }
            else if(update.downloadUrl() == null || update.downloadUrl().toString().isEmpty()) {
                result = Result.UPDATE_AVAILABLE;
            }
            // Update found, trying to download it
            else if(output!= null && download) {
                try {
                    Downloader.download(update, output);
                }
                catch(IOException e) {
                    result = Result.UPDATE_AVAILABLE;
                }
            }
        }

        // Updater ending
        if(onFinish != null) {
            onFinish.accept(result, update);
        }

    }

}
