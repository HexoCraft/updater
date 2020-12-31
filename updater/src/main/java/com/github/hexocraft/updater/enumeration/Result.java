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

package com.github.hexocraft.updater.enumeration;

/**
 * Available responses from the updater while trying to find an update.
 */
public enum Result {

  /**
   * An update has been found
   */
  SUCCESS,

  /**
   * A new version has been found but nothing was downloaded.
   */

  UPDATE_AVAILABLE,

  /**
   * The specified update is already the latest update
   */
  NO_UPDATE,

  /**
   * No releases have been found on the repository.
   */
  REPO_NO_RELEASES,

  /**
   * An error occurred.
   */
  ERROR;

  @Override
  public String toString() {
    return this.name();
  }
}
