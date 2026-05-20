SKYFORGE STRUCTURE PIECES — NBT Build Guide
============================================

This directory holds the .nbt structure files referenced by the template pools.
They must be built in-game and saved with the Structure Block tool.

Expected piece files (one per template pool entry):
  skyforge_outpost_tower.nbt      — central command tower (start piece, has jigsaw connectors)
  skyforge_outpost_barracks.nbt   — side barracks with pillagerspawn points
  skyforge_outpost_watchtower.nbt — corner watchtower
  skyforge_outpost_hangar.nbt     — landing pad / hangar (optional, spawns aerial entities)

HOW TO BUILD A PIECE:
1. Build the structure in Creative mode.
2. Place a Structure Block (type: SAVE) at one corner.
3. Set the size to cover the build.
4. Set the save name to:   skyforge:<piece_name>
   Example:   skyforge:skyforge_outpost_tower
5. Click SAVE.  The .nbt file appears at:
   world/generated/skyforge/structures/<piece_name>.nbt
6. Copy it here:
   src/main/resources/data/skyforge/structures/<piece_name>.nbt

JIGSAW CONNECTORS:
- Use Jigsaw blocks (obtainable with /give @p minecraft:jigsaw_block) as connection points.
- Set the pool on the START piece connector to:  skyforge:skyforge/outpost_pieces
- Set the target name matching the target pool entries so pieces snap together.

MOB SPAWNERS:
- Place spawner blocks inside pieces for persistent mob presence.
- Or add a SpawnOverride in the structure JSON if you want vanilla category spawning.

ENTITY SPAWNS (aerial):
- The hangar piece can include a command block that summons the dropship on structure load,
  or use a SpawnData-equipped spawner pointing to skyforge:dropship / heavy_dropship.
