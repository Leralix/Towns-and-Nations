# Towns-and-Nations-Minecraft-plugin

[![CodeFactor](https://www.codefactor.io/repository/github/leralix/towns-and-nations/badge/main)](https://www.codefactor.io/repository/github/leralix/towns-and-nations/overview/main) 
[![Crowdin](https://badges.crowdin.net/town-and-nation/localized.svg)](https://crowdin.com/project/town-and-nation)

[![Bstat servers](https://img.shields.io/bstats/servers/20527)](https://bstats.org/plugin/bukkit/TownsAndNations/20527)
[![Discord link](https://img.shields.io/discord/1144006816548798615?logo=discord&logoColor=ffffff&labelColor=6A7EC2&color=7389D8)](https://discord.gg/Q8gZSFUuzb)



Towns and Nations is a Minecraft plugin designed to provide players with an immersive role-playing experience. It enables the creation of towns, management of land claims, establishment of alliances, and the orchestration of wars and betrayals. Towns and Nations is currently in development, primarily for the Arcadia Minecraft server (French server).

For updates, discussions, or to join our growing community, please visit our Discord server.
https://discord.gg/Q8gZSFUuzb

Spigot page : https://www.spigotmc.org/resources/towns-nations.114019/

Dynmap, Squaremap and Bluemap support : https://github.com/Leralix/Towns-and-Nations-Dynmap

API :

[![api-javadoc](https://javadoc.io/badge2/io.github.leralix/tan-api/javadoc.svg)](https://javadoc.io/doc/io.github.leralix/tan-api)

Since v0.15.0 towns and nations provide an API. This API allows simple checks and interactions with the plugin. 
An installation guide can be found on GitBook: https://arcadia-9.gitbook.io/towns-and-nations/developpers/api

## Building version-specific jars (1.19 / 1.20 / 1.21)

This repository can produce **3 separate plugin jars**, one for each supported Minecraft version.

Build all jars:

```bash
gradle buildAllJars
```

Outputs:

- `TownsAndNations-Plugin-1_19/build/libs/TownsAndNations-*-mc1.19.jar`
- `TownsAndNations-Plugin-1_20/build/libs/TownsAndNations-*-mc1.20.jar`
- `TownsAndNations-Plugin-1_21/build/libs/TownsAndNations-*-mc1.21.jar`


