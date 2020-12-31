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

import com.github.hexocraft.updater.channels.BukkitChannel;
import com.github.hexocraft.updater.enumeration.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertSame;

class UpdaterTest {

    @Test
    public void BukkitChannel() {

        // Function called when updater begin
        Runnable start = this::onStart;

        // Function called when updater finish its work
        BiConsumer<Result,Update> finish = this::onFinish;

        // Create an updater
        Updater updater = new Updater(
                new Version(1, 0, 0)
                , new BukkitChannel("255160"))
            .setDownload(true)
            .setOutput(Paths.get("target", "updater"))
            .setDelay(0)
            .setPeriod(TimeUnit.SECONDS.toMillis(30))
            .onStart(start)
            .onFinish(finish)
            .run();
    }

    private void onStart() {
        System.out.println("Updater is starting");
    }

    private void onFinish(Result result, Update update) {
        System.out.println("Updater is ending");

        assertSame(result, Result.SUCCESS);
    }

}