# wip charity stream integration

this is a paper plugin for minecraft

the goal is to create a reusable system for running charity livestreams that have in-game rewards for donations

## building

```
./gradlew build
```

if you want to run a server with the plugin on it for testing, do:

```
./gradlew runServer
```

you'll have to manually accept the minecraft EULA by editing `run/eula.txt` the first time.

## main concepts

### team

a group of players

### objective

something you can do to get the team points

### donation effect

something that happens when a donation comes in
