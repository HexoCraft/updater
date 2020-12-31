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

import com.github.hexocraft.updater.Version;
import com.github.hexocraft.updater.enumeration.Release;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

  @Test
  public void VersionParse() {
    Version version = Version.parse("1.2.3");

    assertEquals(version.getMajor(), 1);
    assertEquals(version.getMinor(), 2);
    assertEquals(version.getPatch(), 3);
  }

  @Test
  public void VersionIsSemver() {
    assertTrue(Version.isSemver("1.2.3"));
    assertTrue(Version.isSemver("1.2.3-alpha.something+meta-data"));

    assertTrue(Version.isSemver("SomeString-1.2.3"));
    assertTrue(Version.isSemver("SomeString-1.2.3-alpha.something+meta-data"));

    assertTrue(Version.isSemver("SomeString 1.2.3"));
    assertTrue(Version.isSemver("SomeString 1.2.3-alpha.something+meta-data"));

    assertTrue(Version.isSemver("SomeString-1.2"));
    assertTrue(Version.isSemver("SomeString 1.2"));

    assertFalse(Version.isSemver("1"));
  }

  @Test
  public void VersionComparison() {
    assertTrue(Objects.requireNonNull(Version.parse("1.2.3")).equals(new Version(1, 2, 3)));
    assertTrue(Objects.requireNonNull(Version.parse("1.2.3")).isLower(Objects.requireNonNull(Version.parse("1.2.4"))));
    assertTrue(Objects.requireNonNull(Version.parse("1.2.3")).isGreater(Objects.requireNonNull(Version.parse("1.2.2"))));
  }

  @Test
  public void VersionReleaseTest() {
    assertSame(Objects.requireNonNull(Version.parse("1.2.3")).getRelease(), Release.RELEASE);
    assertSame(Objects.requireNonNull(Version.parse("1.2.3-SNAPSHOT")).getRelease(), Release.PRE_RELEASE);
    assertSame(Objects.requireNonNull(Version.parse("1.2.3-DEV")).getRelease(), Release.PRE_RELEASE);
  }
}