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
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUpdater extends Updater<BungeeUpdater> {

    public BungeeUpdater(Plugin plugin, Channel channel) {
        super(Version.parse(plugin.getDescription().getVersion()), plugin.getDataFolder().getParentFile().toPath(), channel);
    }
}
