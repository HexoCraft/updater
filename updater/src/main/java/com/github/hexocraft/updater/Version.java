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

import com.github.hexocraft.updater.enumeration.Release;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple semver like major.minor.patch storage system.
 */
public class Version {

    /**
     * major.
     */
    private final int major;

    /**
     * minor.
     */
    private final int minor;

    /**
     * patch.
     */
    private final int patch;

    /**
     * pre-release.
     */
    private final String preRelease;

    /**
     * build.
     */
    private final String build;

    /**
     * Pattern used to match semantic versioning compliant strings.
     * <p>
     * Major: matcher.group(1) Minor: matcher.group(2) Patch: matcher.group(3)
     */
    protected static Pattern semver = Pattern.compile("^(?:\\D*)(0|[1-9]\\d*)\\.(0|[1-9]\\d*)?(?:\\.(0|[1-9]\\d*))?(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$", Pattern.CASE_INSENSITIVE);

    /**
     * Create a new instance of the {@link Version} class.
     *
     * @param major      semver major
     * @param minor      semver minor
     * @param patch      semver patch
     * @param preRelease semver pre-release
     * @param build      semver build
     */
    public Version(int major, int minor, int patch, String preRelease, String build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.build = build;
    }

    /**
     * Create a new instance of the {@link Version} class.
     *
     * @param major semver major
     * @param minor semver minor
     * @param patch semver patch
     */
    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null, null);
    }

    /**
     * Quick method for parsing update strings and matching them using the {@link
     * Pattern} in {@link Version}
     *
     * @param version semver string to parse
     * @return {@link Version} if valid semver string else null
     */
    public static Version parse(String version) {
        Matcher matcher = Version.semver.matcher(version);

        if(matcher.matches() && matcher.groupCount() >= 2) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
            String preRelease = matcher.group(4);
            String build = matcher.group(5);
            return new Version(major, minor, patch, preRelease, build);
        }

        return null;
    }

    /**
     * Test if the update string contains a valid semver string
     *
     * @param version update to test
     * @return true if valid
     */
    public static boolean isSemver(String version) {
        return Version.parse(version) != null;
    }

    /**
     * @return semver major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return semver minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @return semver patch
     */
    public int getPatch() {
        return patch;
    }

    /**
     * @return semver pre-release
     */
    public String getPreRelease() {
        return preRelease;
    }

    /**
     * @return semver build
     */
    public String getBuild() {
        return build;
    }

    /**
     * @return {@link Release} type
     */
    public Release getRelease() {
        if(preRelease == null || preRelease.isEmpty()) {
            return Release.RELEASE;
        }
        else {
            return Release.PRE_RELEASE;
        }
    }

    /**
     * @return joined update string.
     */
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    /**
     * Check if this version is equal to the input version.
     *
     * @param version input {@link Version} object
     * @return true if the update is greater than ours
     */
    public boolean equals(Version version) {
        if(version.getMajor() != this.getMajor())
            return false;
        if(version.getMinor() != this.getMinor())
            return false;
        return version.getPatch() == this.getPatch();
    }

    /**
     * Check if this version is lower than the input version.
     *
     * @param version input {@link Version} object
     * @return true if the update is greater than ours
     */
    public boolean isLower(Version version) {
        int result = version.getMajor() - this.getMajor();
        if(result == 0) {
            result = version.getMinor() - this.getMinor();
            if(result == 0) {
                result = version.getPatch() - this.getPatch();
            }
        }
        return result > 0;
    }

    /**
     * Check if this version is greater than the input version.
     *
     * @param version input {@link Version} object
     * @return true if the update is lower than ours
     */
    public boolean isGreater(Version version) {
        return version.isLower(this);
    }
}
