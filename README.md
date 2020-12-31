Updater
=============
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.hexocraft/updater?label=stable&color=%23f6cf17)][Maven Central]
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.hexocraft/updater?label=dev&server=https%3A%2F%2Foss.sonatype.org)

Java Updater for bukkit and Bungeecord plugins

## Setup

updater is published on Maven Central 

```maven
<repositories>
    <!-- for stable release -->
    <repository>
        <id>maven-central</id>
        <url>https://repo1.maven.org/maven2/</url>
    </repository>
    <!-- for development builds -->
    <repository>
       <id>sonatype-oss</id>
       <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.hexocraft</groupId>
        <artifactId>updater</artifactId>
    </dependency>
    <!-- For Bukkit plugin -->
    <dependency>
        <groupId>com.github.hexocraft</groupId>
        <artifactId>updater-bukkit</artifactId>
    </dependency>
    <!-- For Bungeecord plugin -->
    <dependency>
        <groupId>com.github.hexocraft</groupId>
        <artifactId>updater-bungee</artifactId>
    </dependency>
</dependencies>
```

## How to use

### Find update on Bukkit:

```java
BukkitUpdater updater = new BukkitUpdater(plugin, new BukkitChannel("project_id")).run();
```

### Find update on Spigot:
(Free plugin only)

```java
BukkitUpdater updater = new BukkitUpdater(plugin, new SpigotChannel("resource_id")).run();
```

### Find update on GitHub:
(Free plugin only)

```java
BukkitUpdater updater = new BukkitUpdater(plugin, new GithubChannel("repository")).run();
```

[Maven Central]: https://search.maven.org/search?q=g:com.github.hexocraft%20AND%20a:updater*