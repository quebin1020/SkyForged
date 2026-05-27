# Create — Documentación Técnica Exhaustiva

**Mod:** Create  
**Loader:** NeoForge 1.21.1  
**Rama documentada:** `mc1.21.1/dev`  
**Commit:** `802dddddd0f3b1a5eb4ae56272483dee00d17890` (2026-05-19)  
**Repo:** https://github.com/Creators-of-Create/Create  
**Paquete raíz:** `com.simibubi.create`

---

## 1. Estructura de Paquetes

```
com.simibubi.create/
├── api/                          ← API pública para addons
│   ├── behaviour/
│   │   ├── display/              ← DisplaySource, DisplayTarget
│   │   ├── interaction/          ← MovingInteractionBehaviour
│   │   └── movement/             ← MovementBehaviour
│   ├── boiler/
│   ├── connectivity/
│   ├── contraption/
│   │   ├── dispenser/            ← MountedDispenseBehavior
│   │   └── storage/
│   │       ├── fluid/            ← MountedFluidStorageType
│   │       └── item/             ← MountedItemStorageType
│   ├── data/
│   ├── effect/
│   ├── equipment/
│   │   └── goggles/              ← IHaveGoggleInformation
│   ├── event/                    ← BlockEntityBehaviourEvent
│   ├── packager/
│   ├── registrate/
│   ├── registry/
│   ├── schematic/
│   └── stress/                   ← BlockStressValues
├── compat/                       ← Compatibilidad con otros mods
│   ├── computercraft/
│   ├── curios/
│   ├── jei/
│   └── (otros)
├── content/                      ← Contenido del mod
│   ├── contraptions/             ← Pistones, poleas, bearings, trains
│   ├── decoration/               ← Casings, puertas, escaleras, etc.
│   ├── equipment/                ← Armadura, herramientas, goggles
│   ├── fluids/                   ← Tuberías, bombas, tanques
│   ├── kinetics/                 ← Todo el sistema de rotación
│   ├── legacy/                   ← Items legados
│   ├── logistics/                ← Belts, funnels, tunnels, vaults
│   ├── materials/                ← Experience block
│   ├── processing/               ← Basin, blaze burner, sequenced assembly
│   ├── redstone/                 ← Links, nixie tubes, gauges
│   ├── schematics/               ← Schematicannon, schematic table
│   └── trains/                   ← Tracks, bogies, stations, schedules
├── foundation/                   ← Framework interno
│   ├── block/
│   ├── blockEntity/              ← SmartBlockEntity, behaviours
│   ├── data/                     ← CreateRegistrate, builders
│   ├── fluid/
│   ├── gui/
│   ├── item/
│   ├── networking/
│   ├── recipe/
│   ├── render/
│   ├── sound/
│   └── utility/
├── impl/                         ← Implementaciones internas
└── infrastructure/               ← Config, worldgen, commands
```

---

## 2. Registros Principales

### 2.1 AllBlocks

Archivo: `src/main/java/com/simibubi/create/AllBlocks.java`  
Tipo de campo: `BlockEntry<T>` (Registrate wrapper). Todos son `public static final`.

#### Schematics

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `SCHEMATICANNON` | `BlockEntry<SchematicannonBlock>` | `create:schematicannon` |
| `SCHEMATIC_TABLE` | `BlockEntry<SchematicTableBlock>` | `create:schematic_table` |

#### Kinetics — Simple Relays

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `SHAFT` | `BlockEntry<ShaftBlock>` | `create:shaft` |
| `COGWHEEL` | `BlockEntry<CogWheelBlock>` | `create:cogwheel` |
| `LARGE_COGWHEEL` | `BlockEntry<CogWheelBlock>` | `create:large_cogwheel` |
| `ANDESITE_ENCASED_SHAFT` | `BlockEntry<EncasedShaftBlock>` | `create:andesite_encased_shaft` |
| `BRASS_ENCASED_SHAFT` | `BlockEntry<EncasedShaftBlock>` | `create:brass_encased_shaft` |
| `ANDESITE_ENCASED_COGWHEEL` | `BlockEntry<EncasedCogwheelBlock>` | `create:andesite_encased_cogwheel` |
| `BRASS_ENCASED_COGWHEEL` | `BlockEntry<EncasedCogwheelBlock>` | `create:brass_encased_cogwheel` |
| `ANDESITE_ENCASED_LARGE_COGWHEEL` | `BlockEntry<EncasedCogwheelBlock>` | `create:andesite_encased_large_cogwheel` |
| `BRASS_ENCASED_LARGE_COGWHEEL` | `BlockEntry<EncasedCogwheelBlock>` | `create:brass_encased_large_cogwheel` |
| `GEARBOX` | `BlockEntry<GearboxBlock>` | `create:gearbox` |
| `CLUTCH` | `BlockEntry<ClutchBlock>` | `create:clutch` |
| `GEARSHIFT` | `BlockEntry<GearshiftBlock>` | `create:gearshift` |
| `ENCASED_CHAIN_DRIVE` | `BlockEntry<ChainDriveBlock>` | `create:encased_chain_drive` |
| `ADJUSTABLE_CHAIN_GEARSHIFT` | `BlockEntry<ChainGearshiftBlock>` | `create:adjustable_chain_gearshift` |
| `BELT` | `BlockEntry<BeltBlock>` | `create:belt` |
| `CHAIN_CONVEYOR` | `BlockEntry<ChainConveyorBlock>` | `create:chain_conveyor` |

#### Kinetics — Generators

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `CREATIVE_MOTOR` | `BlockEntry<CreativeMotorBlock>` | `create:creative_motor` |
| `WATER_WHEEL` | `BlockEntry<WaterWheelBlock>` | `create:water_wheel` |
| `LARGE_WATER_WHEEL` | `BlockEntry<LargeWaterWheelBlock>` | `create:large_water_wheel` |
| `WATER_WHEEL_STRUCTURAL` | `BlockEntry<WaterWheelStructuralBlock>` | `create:water_wheel_structure` |
| `WINDMILL_BEARING` | `BlockEntry<WindmillBearingBlock>` | `create:windmill_bearing` |
| `HAND_CRANK` | `BlockEntry<HandCrankBlock>` | `create:hand_crank` |
| `STEAM_ENGINE` | `BlockEntry<SteamEngineBlock>` | `create:steam_engine` |
| `POWERED_SHAFT` | `BlockEntry<PoweredShaftBlock>` | `create:powered_shaft` |

#### Kinetics — Machines

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `ENCASED_FAN` | `BlockEntry<EncasedFanBlock>` | `create:encased_fan` |
| `NOZZLE` | `BlockEntry<NozzleBlock>` | `create:nozzle` |
| `TURNTABLE` | `BlockEntry<TurntableBlock>` | `create:turntable` |
| `CUCKOO_CLOCK` | `BlockEntry<CuckooClockBlock>` | `create:cuckoo_clock` |
| `MYSTERIOUS_CUCKOO_CLOCK` | `BlockEntry<CuckooClockBlock>` | `create:mysterious_cuckoo_clock` |
| `MILLSTONE` | `BlockEntry<MillstoneBlock>` | `create:millstone` |
| `CRUSHING_WHEEL` | `BlockEntry<CrushingWheelBlock>` | `create:crushing_wheel` |
| `CRUSHING_WHEEL_CONTROLLER` | `BlockEntry<CrushingWheelControllerBlock>` | `create:crushing_wheel_controller` |
| `MECHANICAL_PRESS` | `BlockEntry<MechanicalPressBlock>` | `create:mechanical_press` |
| `MECHANICAL_MIXER` | `BlockEntry<MechanicalMixerBlock>` | `create:mechanical_mixer` |
| `BASIN` | `BlockEntry<BasinBlock>` | `create:basin` |
| `BLAZE_BURNER` | `BlockEntry<BlazeBurnerBlock>` | `create:blaze_burner` |
| `LIT_BLAZE_BURNER` | `BlockEntry<LitBlazeBurnerBlock>` | `create:lit_blaze_burner` |
| `MECHANICAL_CRAFTER` | `BlockEntry<MechanicalCrafterBlock>` | `create:mechanical_crafter` |
| `SEQUENCED_GEARSHIFT` | `BlockEntry<SequencedGearshiftBlock>` | `create:sequenced_gearshift` |
| `FLYWHEEL` | `BlockEntry<FlywheelBlock>` | `create:flywheel` |
| `ROTATION_SPEED_CONTROLLER` | `BlockEntry<SpeedControllerBlock>` | `create:rotation_speed_controller` |
| `SPEEDOMETER` | `BlockEntry<GaugeBlock>` | `create:speedometer` |
| `STRESSOMETER` | `BlockEntry<GaugeBlock>` | `create:stressometer` |
| `WOODEN_BRACKET` | `BlockEntry<BracketBlock>` | `create:wooden_bracket` |
| `METAL_BRACKET` | `BlockEntry<BracketBlock>` | `create:metal_bracket` |

#### Fluids

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `FLUID_PIPE` | `BlockEntry<FluidPipeBlock>` | `create:fluid_pipe` |
| `ENCASED_FLUID_PIPE` | `BlockEntry<EncasedPipeBlock>` | `create:encased_fluid_pipe` |
| `GLASS_FLUID_PIPE` | `BlockEntry<GlassFluidPipeBlock>` | `create:glass_fluid_pipe` |
| `MECHANICAL_PUMP` | `BlockEntry<PumpBlock>` | `create:mechanical_pump` |
| `SMART_FLUID_PIPE` | `BlockEntry<SmartFluidPipeBlock>` | `create:smart_fluid_pipe` |
| `FLUID_VALVE` | `BlockEntry<FluidValveBlock>` | `create:fluid_valve` |
| `COPPER_VALVE_HANDLE` | `BlockEntry<ValveHandleBlock>` | `create:copper_valve_handle` |
| `DYED_VALVE_HANDLES` | `DyedBlockList<ValveHandleBlock>` | `create:<color>_valve_handle` (×16) |
| `FLUID_TANK` | `BlockEntry<FluidTankBlock>` | `create:fluid_tank` |
| `CREATIVE_FLUID_TANK` | `BlockEntry<FluidTankBlock>` | `create:creative_fluid_tank` |
| `HOSE_PULLEY` | `BlockEntry<HosePulleyBlock>` | `create:hose_pulley` |
| `ITEM_DRAIN` | `BlockEntry<ItemDrainBlock>` | `create:item_drain` |
| `SPOUT` | `BlockEntry<SpoutBlock>` | `create:spout` |
| `PORTABLE_FLUID_INTERFACE` | `BlockEntry<PortableStorageInterfaceBlock>` | `create:portable_fluid_interface` |
| `STEAM_WHISTLE` | `BlockEntry<WhistleBlock>` | `create:steam_whistle` |
| `STEAM_WHISTLE_EXTENSION` | `BlockEntry<WhistleExtenderBlock>` | `create:steam_whistle_extension` |

#### Contraptions

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `MECHANICAL_PISTON` | `BlockEntry<MechanicalPistonBlock>` | `create:mechanical_piston` |
| `STICKY_MECHANICAL_PISTON` | `BlockEntry<MechanicalPistonBlock>` | `create:sticky_mechanical_piston` |
| `PISTON_EXTENSION_POLE` | `BlockEntry<PistonExtensionPoleBlock>` | `create:piston_extension_pole` |
| `MECHANICAL_PISTON_HEAD` | `BlockEntry<MechanicalPistonHeadBlock>` | `create:mechanical_piston_head` |
| `GANTRY_CARRIAGE` | `BlockEntry<GantryCarriageBlock>` | `create:gantry_carriage` |
| `GANTRY_SHAFT` | `BlockEntry<GantryShaftBlock>` | `create:gantry_shaft` |
| `MECHANICAL_BEARING` | `BlockEntry<MechanicalBearingBlock>` | `create:mechanical_bearing` |
| `CLOCKWORK_BEARING` | `BlockEntry<ClockworkBearingBlock>` | `create:clockwork_bearing` |
| `ROPE_PULLEY` | `BlockEntry<PulleyBlock>` | `create:rope_pulley` |
| `ROPE` | `BlockEntry<PulleyBlock.RopeBlock>` | `create:rope` |
| `PULLEY_MAGNET` | `BlockEntry<PulleyBlock.MagnetBlock>` | `create:pulley_magnet` |
| `ELEVATOR_PULLEY` | `BlockEntry<ElevatorPulleyBlock>` | `create:elevator_pulley` |
| `ELEVATOR_CONTACT` | `BlockEntry<ElevatorContactBlock>` | `create:elevator_contact` |
| `CART_ASSEMBLER` | `BlockEntry<CartAssemblerBlock>` | `create:cart_assembler` |
| `MINECART_ANCHOR` | `BlockEntry<CartAssemblerBlock.MinecartAnchorBlock>` | `create:minecart_anchor` |
| `LINEAR_CHASSIS` | `BlockEntry<LinearChassisBlock>` | `create:linear_chassis` |
| `SECONDARY_LINEAR_CHASSIS` | `BlockEntry<LinearChassisBlock>` | `create:secondary_linear_chassis` |
| `RADIAL_CHASSIS` | `BlockEntry<RadialChassisBlock>` | `create:radial_chassis` |
| `STICKER` | `BlockEntry<StickerBlock>` | `create:sticker` |
| `CONTRAPTION_CONTROLS` | `BlockEntry<ContraptionControlsBlock>` | `create:contraption_controls` |
| `SAIL_FRAME` | `BlockEntry<SailBlock>` | `create:sail_frame` |
| `SAIL` | `BlockEntry<SailBlock>` | `create:white_sail` |
| `DYED_SAILS` | `DyedBlockList<SailBlock>` | `create:<color>_sail` (×16) |

#### Contraption Actors (Kinetics montados en contraptions)

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `MECHANICAL_DRILL` | `BlockEntry<DrillBlock>` | `create:mechanical_drill` |
| `MECHANICAL_SAW` | `BlockEntry<SawBlock>` | `create:mechanical_saw` |
| `DEPLOYER` | `BlockEntry<DeployerBlock>` | `create:deployer` |
| `PORTABLE_STORAGE_INTERFACE` | `BlockEntry<PortableStorageInterfaceBlock>` | `create:portable_storage_interface` |
| `REDSTONE_CONTACT` | `BlockEntry<RedstoneContactBlock>` | `create:redstone_contact` |
| `MECHANICAL_HARVESTER` | `BlockEntry<HarvesterBlock>` | `create:mechanical_harvester` |
| `MECHANICAL_PLOUGH` | `BlockEntry<PloughBlock>` | `create:mechanical_plough` |
| `MECHANICAL_ROLLER` | `BlockEntry<RollerBlock>` | `create:mechanical_roller` |

#### Logistics

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `DEPOT` | `BlockEntry<DepotBlock>` | `create:depot` |
| `WEIGHTED_EJECTOR` | `BlockEntry<EjectorBlock>` | `create:weighted_ejector` |
| `CHUTE` | `BlockEntry<ChuteBlock>` | `create:chute` |
| `SMART_CHUTE` | `BlockEntry<SmartChuteBlock>` | `create:smart_chute` |
| `ANDESITE_FUNNEL` | `BlockEntry<AndesiteFunnelBlock>` | `create:andesite_funnel` |
| `ANDESITE_BELT_FUNNEL` | `BlockEntry<BeltFunnelBlock>` | `create:andesite_belt_funnel` |
| `BRASS_FUNNEL` | `BlockEntry<BrassFunnelBlock>` | `create:brass_funnel` |
| `BRASS_BELT_FUNNEL` | `BlockEntry<BeltFunnelBlock>` | `create:brass_belt_funnel` |
| `ANDESITE_TUNNEL` | `BlockEntry<BeltTunnelBlock>` | `create:andesite_tunnel` |
| `BRASS_TUNNEL` | `BlockEntry<BrassTunnelBlock>` | `create:brass_tunnel` |
| `MECHANICAL_ARM` | `BlockEntry<ArmBlock>` | `create:mechanical_arm` |
| `ITEM_VAULT` | `BlockEntry<ItemVaultBlock>` | `create:item_vault` |
| `ITEM_HATCH` | `BlockEntry<ItemHatchBlock>` | `create:item_hatch` |
| `CREATIVE_CRATE` | `BlockEntry<CreativeCrateBlock>` | `create:creative_crate` |
| `PACKAGER` | `BlockEntry<PackagerBlock>` | `create:packager` |
| `REPACKAGER` | `BlockEntry<RepackagerBlock>` | `create:repackager` |
| `PACKAGE_FROGPORT` | `BlockEntry<FrogportBlock>` | `create:package_frogport` |
| `PACKAGE_POSTBOXES` | `DyedBlockList<PostboxBlock>` | `create:<color>_postbox` (×16) |
| `STOCK_LINK` | `BlockEntry<PackagerLinkBlock>` | `create:stock_link` |
| `STOCK_TICKER` | `BlockEntry<StockTickerBlock>` | `create:stock_ticker` |
| `REDSTONE_REQUESTER` | `BlockEntry<RedstoneRequesterBlock>` | `create:redstone_requester` |
| `FACTORY_GAUGE` | `BlockEntry<FactoryPanelBlock>` | `create:factory_gauge` |
| `TABLE_CLOTHS` | `DyedBlockList<TableClothBlock>` | `create:<color>_table_cloth` (×16) |
| `ANDESITE_TABLE_CLOTH` | `BlockEntry<TableClothBlock>` | `create:andesite_table_cloth` |
| `BRASS_TABLE_CLOTH` | `BlockEntry<TableClothBlock>` | `create:brass_table_cloth` |
| `COPPER_TABLE_CLOTH` | `BlockEntry<TableClothBlock>` | `create:copper_table_cloth` |
| `SMART_OBSERVER` | `BlockEntry<SmartObserverBlock>` | `create:content_observer` |
| `THRESHOLD_SWITCH` | `BlockEntry<ThresholdSwitchBlock>` | `create:stockpile_switch` |

#### Trains

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `TRACK` | `BlockEntry<TrackBlock>` | `create:track` |
| `FAKE_TRACK` | `BlockEntry<FakeTrackBlock>` | `create:fake_track` |
| `RAILWAY_CASING` | `BlockEntry<CasingBlock>` | `create:railway_casing` |
| `TRACK_STATION` | `BlockEntry<StationBlock>` | `create:track_station` |
| `TRACK_SIGNAL` | `BlockEntry<SignalBlock>` | `create:track_signal` |
| `TRACK_OBSERVER` | `BlockEntry<TrackObserverBlock>` | `create:track_observer` |
| `SMALL_BOGEY` | `BlockEntry<StandardBogeyBlock>` | `create:small_bogey` |
| `LARGE_BOGEY` | `BlockEntry<StandardBogeyBlock>` | `create:large_bogey` |
| `TRAIN_CONTROLS` | `BlockEntry<ControlsBlock>` | `create:controls` |
| `CONTROLLER_RAIL` | `BlockEntry<ControllerRailBlock>` | `create:controller_rail` |

#### Redstone

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `DISPLAY_LINK` | `BlockEntry<DisplayLinkBlock>` | `create:display_link` |
| `DISPLAY_BOARD` | `BlockEntry<FlapDisplayBlock>` | `create:display_board` |
| `ORANGE_NIXIE_TUBE` | `BlockEntry<NixieTubeBlock>` | `create:nixie_tube` |
| `NIXIE_TUBES` | `DyedBlockList<NixieTubeBlock>` | `create:<color>_nixie_tube` (×15 otros colores) |
| `ROSE_QUARTZ_LAMP` | `BlockEntry<RoseQuartzLampBlock>` | `create:rose_quartz_lamp` |
| `REDSTONE_LINK` | `BlockEntry<RedstoneLinkBlock>` | `create:redstone_link` |
| `ANALOG_LEVER` | `BlockEntry<AnalogLeverBlock>` | `create:analog_lever` |
| `PLACARD` | `BlockEntry<PlacardBlock>` | `create:placard` |
| `PULSE_REPEATER` | `BlockEntry<BrassDiodeBlock>` | `create:pulse_repeater` |
| `PULSE_EXTENDER` | `BlockEntry<BrassDiodeBlock>` | `create:pulse_extender` |
| `PULSE_TIMER` | `BlockEntry<BrassDiodeBlock>` | `create:pulse_timer` |
| `POWERED_LATCH` | `BlockEntry<PoweredLatchBlock>` | `create:powered_latch` |
| `POWERED_TOGGLE_LATCH` | `BlockEntry<ToggleLatchBlock>` | `create:powered_toggle_latch` |
| `LECTERN_CONTROLLER` | `BlockEntry<LecternControllerBlock>` | `create:lectern_controller` |

#### Casings / Decoration

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `ANDESITE_CASING` | `BlockEntry<CasingBlock>` | `create:andesite_casing` |
| `BRASS_CASING` | `BlockEntry<CasingBlock>` | `create:brass_casing` |
| `COPPER_CASING` | `BlockEntry<CasingBlock>` | `create:copper_casing` |
| `SHADOW_STEEL_CASING` | `BlockEntry<CasingBlock>` | `create:shadow_steel_casing` |
| `REFINED_RADIANCE_CASING` | `BlockEntry<CasingBlock>` | `create:refined_radiance_casing` |
| `SEATS` | `DyedBlockList<SeatBlock>` | `create:<color>_seat` (×16) |
| `TOOLBOXES` | `DyedBlockList<ToolboxBlock>` | `create:<color>_toolbox` (×16) |
| `CLIPBOARD` | `BlockEntry<ClipboardBlock>` | `create:clipboard` |
| `COPPER_BACKTANK` | `BlockEntry<BacktankBlock>` | `create:copper_backtank` |
| `NETHERITE_BACKTANK` | `BlockEntry<BacktankBlock>` | `create:netherite_backtank` |
| `PECULIAR_BELL` | `BlockEntry<PeculiarBellBlock>` | `create:peculiar_bell` |
| `HAUNTED_BELL` | `BlockEntry<HauntedBellBlock>` | `create:haunted_bell` |
| `DESK_BELL` | `BlockEntry<DeskBellBlock>` | `create:desk_bell` |
| `ANDESITE_DOOR` | `BlockEntry<SlidingDoorBlock>` | `create:andesite_door` |
| `BRASS_DOOR` | `BlockEntry<SlidingDoorBlock>` | `create:brass_door` |
| `COPPER_DOOR` | `BlockEntry<SlidingDoorBlock>` | `create:copper_door` |
| `TRAIN_DOOR` | `BlockEntry<SlidingDoorBlock>` | `create:train_door` |
| `TRAIN_TRAPDOOR` | `BlockEntry<TrainTrapdoorBlock>` | `create:train_trapdoor` |
| `FRAMED_GLASS_DOOR` | `BlockEntry<SlidingDoorBlock>` | `create:framed_glass_door` |
| `FRAMED_GLASS_TRAPDOOR` | `BlockEntry<TrainTrapdoorBlock>` | `create:framed_glass_trapdoor` |
| `ANDESITE_LADDER` | `BlockEntry<MetalLadderBlock>` | `create:andesite_ladder` |
| `BRASS_LADDER` | `BlockEntry<MetalLadderBlock>` | `create:brass_ladder` |
| `COPPER_LADDER` | `BlockEntry<MetalLadderBlock>` | `create:copper_ladder` |
| `ANDESITE_BARS` | `BlockEntry<IronBarsBlock>` | `create:andesite_bars` |
| `BRASS_BARS` | `BlockEntry<IronBarsBlock>` | `create:brass_bars` |
| `COPPER_BARS` | `BlockEntry<IronBarsBlock>` | `create:copper_bars` |
| `ANDESITE_SCAFFOLD` | `BlockEntry<MetalScaffoldingBlock>` | `create:andesite_scaffolding` |
| `BRASS_SCAFFOLD` | `BlockEntry<MetalScaffoldingBlock>` | `create:brass_scaffolding` |
| `COPPER_SCAFFOLD` | `BlockEntry<MetalScaffoldingBlock>` | `create:copper_scaffolding` |
| `METAL_GIRDER` | `BlockEntry<GirderBlock>` | `create:metal_girder` |
| `METAL_GIRDER_ENCASED_SHAFT` | `BlockEntry<GirderEncasedShaftBlock>` | `create:metal_girder_encased_shaft` |
| `COPYCAT_BASE` | `BlockEntry<Block>` | `create:copycat_base` |
| `COPYCAT_STEP` | `BlockEntry<CopycatStepBlock>` | `create:copycat_step` |
| `COPYCAT_PANEL` | `BlockEntry<CopycatPanelBlock>` | `create:copycat_panel` |
| `COPYCAT_BARS` | `BlockEntry<WrenchableDirectionalBlock>` | `create:copycat_bars` |

#### Materials / Palettes

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `ZINC_ORE` | `BlockEntry<Block>` | `create:zinc_ore` |
| `DEEPSLATE_ZINC_ORE` | `BlockEntry<Block>` | `create:deepslate_zinc_ore` |
| `RAW_ZINC_BLOCK` | `BlockEntry<Block>` | `create:raw_zinc_block` |
| `ZINC_BLOCK` | `BlockEntry<Block>` | `create:zinc_block` |
| `ANDESITE_ALLOY_BLOCK` | `BlockEntry<Block>` | `create:andesite_alloy_block` |
| `INDUSTRIAL_IRON_BLOCK` | `BlockEntry<Block>` | `create:industrial_iron_block` |
| `WEATHERED_IRON_BLOCK` | `BlockEntry<Block>` | `create:weathered_iron_block` |
| `BRASS_BLOCK` | `BlockEntry<Block>` | `create:brass_block` |
| `CARDBOARD_BLOCK` | `BlockEntry<CardboardBlock>` | `create:cardboard_block` |
| `BOUND_CARDBOARD_BLOCK` | `BlockEntry<CardboardBlock>` | `create:bound_cardboard_block` |
| `EXPERIENCE_BLOCK` | `BlockEntry<ExperienceBlock>` | `create:experience_block` |
| `ROSE_QUARTZ_BLOCK` | `BlockEntry<RotatedPillarBlock>` | `create:rose_quartz_block` |
| `ROSE_QUARTZ_TILES` | `BlockEntry<Block>` | `create:rose_quartz_tiles` |
| `SMALL_ROSE_QUARTZ_TILES` | `BlockEntry<Block>` | `create:small_rose_quartz_tiles` |
| `COPPER_SHINGLES` | `CopperBlockSet` | `create:copper_shingles` + variantes oxidadas |
| `COPPER_TILES` | `CopperBlockSet` | `create:copper_tiles` + variantes oxidadas |

---

### 2.2 AllItems

Archivo: `src/main/java/com/simibubi/create/AllItems.java`  
Tipo de campo: `ItemEntry<T>`.

#### Ingredientes básicos

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `WHEAT_FLOUR` | `ItemEntry<Item>` | `create:wheat_flour` |
| `DOUGH` | `ItemEntry<Item>` | `create:dough` |
| `CINDER_FLOUR` | `ItemEntry<Item>` | `create:cinder_flour` |
| `ROSE_QUARTZ` | `ItemEntry<Item>` | `create:rose_quartz` |
| `POLISHED_ROSE_QUARTZ` | `ItemEntry<Item>` | `create:polished_rose_quartz` |
| `POWDERED_OBSIDIAN` | `ItemEntry<Item>` | `create:powdered_obsidian` |
| `STURDY_SHEET` | `ItemEntry<Item>` | `create:sturdy_sheet` |
| `PROPELLER` | `ItemEntry<Item>` | `create:propeller` |
| `WHISK` | `ItemEntry<Item>` | `create:whisk` |
| `BRASS_HAND` | `ItemEntry<Item>` | `create:brass_hand` |
| `CRAFTER_SLOT_COVER` | `ItemEntry<Item>` | `create:crafter_slot_cover` |
| `ELECTRON_TUBE` | `ItemEntry<Item>` | `create:electron_tube` |
| `TRANSMITTER` | `ItemEntry<Item>` | `create:transmitter` |
| `PULP` | `ItemEntry<Item>` | `create:pulp` |
| `CARDBOARD` | `ItemEntry<Item>` | `create:cardboard` |
| `PRECISION_MECHANISM` | `ItemEntry<Item>` | `create:precision_mechanism` |

#### Ingredientes de ensamblado secuenciado

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `INCOMPLETE_PRECISION_MECHANISM` | `ItemEntry<SequencedAssemblyItem>` | `create:incomplete_precision_mechanism` |
| `INCOMPLETE_REINFORCED_SHEET` | `ItemEntry<SequencedAssemblyItem>` | `create:unprocessed_obsidian_sheet` |
| `INCOMPLETE_TRACK` | `ItemEntry<SequencedAssemblyItem>` | `create:incomplete_track` |

#### Comida / consumibles

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `BLAZE_CAKE_BASE` | `ItemEntry<Item>` | `create:blaze_cake_base` |
| `BLAZE_CAKE` | `ItemEntry<Item>` | `create:blaze_cake` |
| `CREATIVE_BLAZE_CAKE` | `ItemEntry<Item>` | `create:creative_blaze_cake` |
| `BAR_OF_CHOCOLATE` | `ItemEntry<Item>` | `create:bar_of_chocolate` |
| `SWEET_ROLL` | `ItemEntry<Item>` | `create:sweet_roll` |
| `CHOCOLATE_BERRIES` | `ItemEntry<Item>` | `create:chocolate_glazed_berries` |
| `HONEYED_APPLE` | `ItemEntry<Item>` | `create:honeyed_apple` |
| `BUILDERS_TEA` | `ItemEntry<BuildersTeaItem>` | `create:builders_tea` |

#### Metales / materiales

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `RAW_ZINC` | `ItemEntry<Item>` | `create:raw_zinc` |
| `ANDESITE_ALLOY` | `ItemEntry<Item>` | `create:andesite_alloy` |
| `ZINC_INGOT` | `ItemEntry<Item>` | `create:zinc_ingot` |
| `BRASS_INGOT` | `ItemEntry<Item>` | `create:brass_ingot` |
| `CHROMATIC_COMPOUND` | `ItemEntry<ChromaticCompoundItem>` | `create:chromatic_compound` |
| `SHADOW_STEEL` | `ItemEntry<ShadowSteelItem>` | `create:shadow_steel` |
| `REFINED_RADIANCE` | `ItemEntry<RefinedRadianceItem>` | `create:refined_radiance` |
| `COPPER_NUGGET` | `ItemEntry<Item>` | `create:copper_nugget` |
| `ZINC_NUGGET` | `ItemEntry<Item>` | `create:zinc_nugget` |
| `BRASS_NUGGET` | `ItemEntry<Item>` | `create:brass_nugget` |
| `EXP_NUGGET` | `ItemEntry<ExperienceNuggetItem>` | `create:experience_nugget` |
| `COPPER_SHEET` | `ItemEntry<Item>` | `create:copper_sheet` |
| `BRASS_SHEET` | `ItemEntry<Item>` | `create:brass_sheet` |
| `IRON_SHEET` | `ItemEntry<Item>` | `create:iron_sheet` |
| `GOLDEN_SHEET` | `ItemEntry<Item>` | `create:golden_sheet` |
| `CRUSHED_IRON` | `ItemEntry<Item>` | `create:crushed_raw_iron` |
| `CRUSHED_GOLD` | `ItemEntry<Item>` | `create:crushed_raw_gold` |
| `CRUSHED_COPPER` | `ItemEntry<Item>` | `create:crushed_raw_copper` |
| `CRUSHED_ZINC` | `ItemEntry<Item>` | `create:crushed_raw_zinc` |
| `CRUSHED_OSMIUM` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_osmium` |
| `CRUSHED_PLATINUM` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_platinum` |
| `CRUSHED_SILVER` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_silver` |
| `CRUSHED_TIN` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_tin` |
| `CRUSHED_LEAD` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_lead` |
| `CRUSHED_QUICKSILVER` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_quicksilver` |
| `CRUSHED_BAUXITE` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_aluminum` |
| `CRUSHED_URANIUM` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_uranium` |
| `CRUSHED_NICKEL` | `ItemEntry<TagDependentIngredientItem>` | `create:crushed_raw_nickel` |

#### Equipo / herramientas

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `BELT_CONNECTOR` | `ItemEntry<BeltConnectorItem>` | `create:belt_connector` |
| `VERTICAL_GEARBOX` | `ItemEntry<VerticalGearboxItem>` | `create:vertical_gearbox` |
| `EMPTY_BLAZE_BURNER` | `ItemEntry<BlazeBurnerBlockItem>` | `create:empty_blaze_burner` |
| `GOGGLES` | `ItemEntry<GogglesItem>` | `create:goggles` |
| `SUPER_GLUE` | `ItemEntry<SuperGlueItem>` | `create:super_glue` |
| `MINECART_COUPLING` | `ItemEntry<MinecartCouplingItem>` | `create:minecart_coupling` |
| `CRAFTING_BLUEPRINT` | `ItemEntry<BlueprintItem>` | `create:crafting_blueprint` |
| `COPPER_BACKTANK` | `ItemEntry<? extends BacktankItem>` | `create:copper_backtank` |
| `NETHERITE_BACKTANK` | `ItemEntry<? extends BacktankItem>` | `create:netherite_backtank` |
| `COPPER_DIVING_HELMET` | `ItemEntry<? extends DivingHelmetItem>` | `create:copper_diving_helmet` |
| `NETHERITE_DIVING_HELMET` | `ItemEntry<? extends DivingHelmetItem>` | `create:netherite_diving_helmet` |
| `COPPER_DIVING_BOOTS` | `ItemEntry<? extends DivingBootsItem>` | `create:copper_diving_boots` |
| `NETHERITE_DIVING_BOOTS` | `ItemEntry<? extends DivingBootsItem>` | `create:netherite_diving_boots` |
| `CARDBOARD_HELMET` | `ItemEntry<? extends BaseArmorItem>` | `create:cardboard_helmet` |
| `CARDBOARD_CHESTPLATE` | `ItemEntry<? extends BaseArmorItem>` | `create:cardboard_chestplate` |
| `CARDBOARD_LEGGINGS` | `ItemEntry<? extends BaseArmorItem>` | `create:cardboard_leggings` |
| `CARDBOARD_BOOTS` | `ItemEntry<? extends BaseArmorItem>` | `create:cardboard_boots` |
| `CARDBOARD_SWORD` | `ItemEntry<CardboardSwordItem>` | `create:cardboard_sword` |
| `SAND_PAPER` | `ItemEntry<SandPaperItem>` | `create:sand_paper` |
| `RED_SAND_PAPER` | `ItemEntry<SandPaperItem>` | `create:red_sand_paper` |
| `WRENCH` | `ItemEntry<WrenchItem>` | `create:wrench` |
| `LINKED_CONTROLLER` | `ItemEntry<LinkedControllerItem>` | `create:linked_controller` |
| `POTATO_CANNON` | `ItemEntry<PotatoCannonItem>` | `create:potato_cannon` |
| `EXTENDO_GRIP` | `ItemEntry<ExtendoGripItem>` | `create:extendo_grip` |
| `WAND_OF_SYMMETRY` | `ItemEntry<SymmetryWandItem>` | `create:wand_of_symmetry` |
| `WORLDSHAPER` | `ItemEntry<WorldshaperItem>` | `create:handheld_worldshaper` |
| `TREE_FERTILIZER` | `ItemEntry<TreeFertilizerItem>` | `create:tree_fertilizer` |

#### Logistics / Filtros

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `FILTER` | `ItemEntry<ListFilterItem>` | `create:filter` |
| `ATTRIBUTE_FILTER` | `ItemEntry<AttributeFilterItem>` | `create:attribute_filter` |
| `PACKAGE_FILTER` | `ItemEntry<PackageFilterItem>` | `create:package_filter` |
| `SCHEDULE` | `ItemEntry<ScheduleItem>` | `create:schedule` |
| `SHOPPING_LIST` | `ItemEntry<ShoppingListItem>` | `create:shopping_list` |

#### Schematics

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `EMPTY_SCHEMATIC` | `ItemEntry<Item>` | `create:empty_schematic` |
| `SCHEMATIC_AND_QUILL` | `ItemEntry<SchematicAndQuillItem>` | `create:schematic_and_quill` |
| `SCHEMATIC` | `ItemEntry<SchematicItem>` | `create:schematic` |

#### Minecart Contraptions

| Campo | Tipo Java | Registry key |
|-------|-----------|--------------|
| `MINECART_CONTRAPTION` | `ItemEntry<MinecartContraptionItem>` | `create:minecart_contraption` |
| `FURNACE_MINECART_CONTRAPTION` | `ItemEntry<MinecartContraptionItem>` | `create:furnace_minecart_contraption` |
| `CHEST_MINECART_CONTRAPTION` | `ItemEntry<MinecartContraptionItem>` | `create:chest_minecart_contraption` |

---

### 2.3 AllBlockEntityTypes

Archivo: `src/main/java/com/simibubi/create/AllBlockEntityTypes.java`  
Tipo de campo: `BlockEntityEntry<T>`.

| Campo | BE Class | Blocks válidos |
|-------|----------|----------------|
| `SCHEMATICANNON` | `SchematicannonBlockEntity` | `SCHEMATICANNON` |
| `SCHEMATIC_TABLE` | `SchematicTableBlockEntity` | `SCHEMATIC_TABLE` |
| `BRACKETED_KINETIC` | `BracketedKineticBlockEntity` | `SHAFT`, `COGWHEEL`, `LARGE_COGWHEEL` |
| `MOTOR` | `CreativeMotorBlockEntity` | `CREATIVE_MOTOR` |
| `GEARBOX` | `GearboxBlockEntity` | `GEARBOX` |
| `ENCASED_SHAFT` | `KineticBlockEntity` | `ANDESITE_ENCASED_SHAFT`, `BRASS_ENCASED_SHAFT`, `ENCASED_CHAIN_DRIVE`, `METAL_GIRDER_ENCASED_SHAFT` |
| `ENCASED_COGWHEEL` | `SimpleKineticBlockEntity` | `ANDESITE_ENCASED_COGWHEEL`, `BRASS_ENCASED_COGWHEEL` |
| `ENCASED_LARGE_COGWHEEL` | `SimpleKineticBlockEntity` | `ANDESITE_ENCASED_LARGE_COGWHEEL`, `BRASS_ENCASED_LARGE_COGWHEEL` |
| `ADJUSTABLE_CHAIN_GEARSHIFT` | `ChainGearshiftBlockEntity` | `ADJUSTABLE_CHAIN_GEARSHIFT` |
| `ENCASED_FAN` | `EncasedFanBlockEntity` | `ENCASED_FAN` |
| `NOZZLE` | `NozzleBlockEntity` | `NOZZLE` |
| `CLUTCH` | `ClutchBlockEntity` | `CLUTCH` |
| `GEARSHIFT` | `GearshiftBlockEntity` | `GEARSHIFT` |
| `TURNTABLE` | `TurntableBlockEntity` | `TURNTABLE` |
| `HAND_CRANK` | `HandCrankBlockEntity` | `HAND_CRANK` |
| `VALVE_HANDLE` | `ValveHandleBlockEntity` | `COPPER_VALVE_HANDLE`, `DYED_VALVE_HANDLES` |
| `CUCKOO_CLOCK` | `CuckooClockBlockEntity` | `CUCKOO_CLOCK`, `MYSTERIOUS_CUCKOO_CLOCK` |
| `GANTRY_SHAFT` | `GantryShaftBlockEntity` | `GANTRY_SHAFT` |
| `GANTRY_PINION` | `GantryCarriageBlockEntity` | `GANTRY_CARRIAGE` |
| `CHAIN_CONVEYOR` | `ChainConveyorBlockEntity` | `CHAIN_CONVEYOR` |
| `MECHANICAL_PUMP` | `PumpBlockEntity` | `MECHANICAL_PUMP` |
| `SMART_FLUID_PIPE` | `SmartFluidPipeBlockEntity` | `SMART_FLUID_PIPE` |
| `FLUID_PIPE` | `FluidPipeBlockEntity` | `FLUID_PIPE` |
| `ENCASED_FLUID_PIPE` | `FluidPipeBlockEntity` | `ENCASED_FLUID_PIPE` |
| `GLASS_FLUID_PIPE` | `StraightPipeBlockEntity` | `GLASS_FLUID_PIPE` |
| `FLUID_VALVE` | `FluidValveBlockEntity` | `FLUID_VALVE` |
| `FLUID_TANK` | `FluidTankBlockEntity` | `FLUID_TANK` |
| `CREATIVE_FLUID_TANK` | `CreativeFluidTankBlockEntity` | `CREATIVE_FLUID_TANK` |
| `HOSE_PULLEY` | `HosePulleyBlockEntity` | `HOSE_PULLEY` |
| `SPOUT` | `SpoutBlockEntity` | `SPOUT` |
| `ITEM_DRAIN` | `ItemDrainBlockEntity` | `ITEM_DRAIN` |
| `BELT` | `BeltBlockEntity` | `BELT` |
| `CHUTE` | `ChuteBlockEntity` | `CHUTE` |
| `SMART_CHUTE` | `SmartChuteBlockEntity` | `SMART_CHUTE` |
| `ANDESITE_TUNNEL` | `BeltTunnelBlockEntity` | `ANDESITE_TUNNEL` |
| `BRASS_TUNNEL` | `BrassTunnelBlockEntity` | `BRASS_TUNNEL` |
| `MECHANICAL_ARM` | `ArmBlockEntity` | `MECHANICAL_ARM` |
| `ITEM_VAULT` | `ItemVaultBlockEntity` | `ITEM_VAULT` |
| `ITEM_HATCH` | `ItemHatchBlockEntity` | `ITEM_HATCH` |
| `PACKAGER` | `PackagerBlockEntity` | `PACKAGER` |
| `REPACKAGER` | `RepackagerBlockEntity` | `REPACKAGER` |
| `PACKAGE_FROGPORT` | `FrogportBlockEntity` | `PACKAGE_FROGPORT` |
| `PACKAGE_POSTBOX` | `PostboxBlockEntity` | `PACKAGE_POSTBOXES` |
| `TABLE_CLOTH` | `TableClothBlockEntity` | `TABLE_CLOTHS`, `ANDESITE_TABLE_CLOTH`, `BRASS_TABLE_CLOTH`, `COPPER_TABLE_CLOTH` |
| `PACKAGER_LINK` | `PackagerLinkBlockEntity` | `STOCK_LINK` |
| `STOCK_TICKER` | `StockTickerBlockEntity` | `STOCK_TICKER` |
| `REDSTONE_REQUESTER` | `RedstoneRequesterBlockEntity` | `REDSTONE_REQUESTER` |
| `MECHANICAL_PISTON` | `MechanicalPistonBlockEntity` | `MECHANICAL_PISTON`, `STICKY_MECHANICAL_PISTON` |
| `WINDMILL_BEARING` | `WindmillBearingBlockEntity` | `WINDMILL_BEARING` |
| `MECHANICAL_BEARING` | `MechanicalBearingBlockEntity` | `MECHANICAL_BEARING` |
| `CLOCKWORK_BEARING` | `ClockworkBearingBlockEntity` | `CLOCKWORK_BEARING` |
| `ROPE_PULLEY` | `PulleyBlockEntity` | `ROPE_PULLEY` |
| `ELEVATOR_PULLEY` | `ElevatorPulleyBlockEntity` | `ELEVATOR_PULLEY` |
| `ELEVATOR_CONTACT` | `ElevatorContactBlockEntity` | `ELEVATOR_CONTACT` |
| `CHASSIS` | `ChassisBlockEntity` | `RADIAL_CHASSIS`, `LINEAR_CHASSIS`, `SECONDARY_LINEAR_CHASSIS` |
| `STICKER` | `StickerBlockEntity` | `STICKER` |
| `CONTRAPTION_CONTROLS` | `ContraptionControlsBlockEntity` | `CONTRAPTION_CONTROLS` |
| `DRILL` | `DrillBlockEntity` | `MECHANICAL_DRILL` |
| `SAW` | `SawBlockEntity` | `MECHANICAL_SAW` |
| `HARVESTER` | `HarvesterBlockEntity` | `MECHANICAL_HARVESTER` |
| `MECHANICAL_ROLLER` | `RollerBlockEntity` | `MECHANICAL_ROLLER` |
| `PORTABLE_STORAGE_INTERFACE` | `PortableItemInterfaceBlockEntity` | `PORTABLE_STORAGE_INTERFACE` |
| `PORTABLE_FLUID_INTERFACE` | `PortableFluidInterfaceBlockEntity` | `PORTABLE_FLUID_INTERFACE` |
| `STEAM_ENGINE` | `SteamEngineBlockEntity` | `STEAM_ENGINE` |
| `STEAM_WHISTLE` | `WhistleBlockEntity` | `STEAM_WHISTLE` |
| `POWERED_SHAFT` | `PoweredShaftBlockEntity` | `POWERED_SHAFT` |
| `FLYWHEEL` | `FlywheelBlockEntity` | `FLYWHEEL` |
| `MILLSTONE` | `MillstoneBlockEntity` | `MILLSTONE` |
| `CRUSHING_WHEEL` | `CrushingWheelBlockEntity` | `CRUSHING_WHEEL` |
| `CRUSHING_WHEEL_CONTROLLER` | `CrushingWheelControllerBlockEntity` | `CRUSHING_WHEEL_CONTROLLER` |
| `WATER_WHEEL` | `WaterWheelBlockEntity` | `WATER_WHEEL` |
| `LARGE_WATER_WHEEL` | `LargeWaterWheelBlockEntity` | `LARGE_WATER_WHEEL` |
| `MECHANICAL_PRESS` | `MechanicalPressBlockEntity` | `MECHANICAL_PRESS` |
| `MECHANICAL_MIXER` | `MechanicalMixerBlockEntity` | `MECHANICAL_MIXER` |
| `DEPLOYER` | `DeployerBlockEntity` | `DEPLOYER` |
| `BASIN` | `BasinBlockEntity` | `BASIN` |
| `HEATER` | `BlazeBurnerBlockEntity` | `BLAZE_BURNER` |
| `MECHANICAL_CRAFTER` | `MechanicalCrafterBlockEntity` | `MECHANICAL_CRAFTER` |
| `SEQUENCED_GEARSHIFT` | `SequencedGearshiftBlockEntity` | `SEQUENCED_GEARSHIFT` |
| `ROTATION_SPEED_CONTROLLER` | `SpeedControllerBlockEntity` | `ROTATION_SPEED_CONTROLLER` |
| `SPEEDOMETER` | `SpeedGaugeBlockEntity` | `SPEEDOMETER` |
| `STRESSOMETER` | `StressGaugeBlockEntity` | `STRESSOMETER` |
| `ANALOG_LEVER` | `AnalogLeverBlockEntity` | `ANALOG_LEVER` |
| `PLACARD` | `PlacardBlockEntity` | `PLACARD` |
| `FACTORY_PANEL` | `FactoryPanelBlockEntity` | `FACTORY_GAUGE` |
| `CART_ASSEMBLER` | `CartAssemblerBlockEntity` | `CART_ASSEMBLER` |
| `REDSTONE_LINK` | `RedstoneLinkBlockEntity` | `REDSTONE_LINK` |
| `NIXIE_TUBE` | `NixieTubeBlockEntity` | `ORANGE_NIXIE_TUBE`, `NIXIE_TUBES` |
| `DISPLAY_LINK` | `DisplayLinkBlockEntity` | `DISPLAY_LINK` |
| `THRESHOLD_SWITCH` | `ThresholdSwitchBlockEntity` | `THRESHOLD_SWITCH` |
| `CREATIVE_CRATE` | `CreativeCrateBlockEntity` | `CREATIVE_CRATE` |
| `DEPOT` | `DepotBlockEntity` | `DEPOT` |
| `WEIGHTED_EJECTOR` | `EjectorBlockEntity` | `WEIGHTED_EJECTOR` |
| `FUNNEL` | `FunnelBlockEntity` | `BRASS_FUNNEL`, `BRASS_BELT_FUNNEL`, `ANDESITE_FUNNEL`, `ANDESITE_BELT_FUNNEL` |
| `SMART_OBSERVER` | `SmartObserverBlockEntity` | `SMART_OBSERVER` |
| `PULSE_EXTENDER` | `PulseExtenderBlockEntity` | `PULSE_EXTENDER` |
| `PULSE_REPEATER` | `PulseRepeaterBlockEntity` | `PULSE_REPEATER` |
| `PULSE_TIMER` | `PulseTimerBlockEntity` | `PULSE_TIMER` |
| `LECTERN_CONTROLLER` | `LecternControllerBlockEntity` | `LECTERN_CONTROLLER` |
| `BACKTANK` | `BacktankBlockEntity` | `COPPER_BACKTANK`, `NETHERITE_BACKTANK` |
| `PECULIAR_BELL` | `PeculiarBellBlockEntity` | `PECULIAR_BELL` |
| `HAUNTED_BELL` | `HauntedBellBlockEntity` | `HAUNTED_BELL` |
| `DESK_BELL` | `DeskBellBlockEntity` | `DESK_BELL` |
| `TOOLBOX` | `ToolboxBlockEntity` | `TOOLBOXES` |
| `TRACK` | `TrackBlockEntity` | todos los `TrackMaterial` blocks |
| `FAKE_TRACK` | `FakeTrackBlockEntity` | `FAKE_TRACK` |
| `BOGEY` | `StandardBogeyBlockEntity` | `SMALL_BOGEY`, `LARGE_BOGEY` |
| `TRACK_STATION` | `StationBlockEntity` | `TRACK_STATION` |
| `SLIDING_DOOR` | `SlidingDoorBlockEntity` | `TRAIN_DOOR`, `FRAMED_GLASS_DOOR`, `ANDESITE_DOOR`, `BRASS_DOOR`, `COPPER_DOOR` |
| `COPYCAT` | `CopycatBlockEntity` | `COPYCAT_PANEL`, `COPYCAT_STEP` |
| `FLAP_DISPLAY` | `FlapDisplayBlockEntity` | `DISPLAY_BOARD` |
| `TRACK_SIGNAL` | `SignalBlockEntity` | `TRACK_SIGNAL` |
| `TRACK_OBSERVER` | `TrackObserverBlockEntity` | `TRACK_OBSERVER` |
| `CLIPBOARD` | `ClipboardBlockEntity` | `CLIPBOARD` |

---

### 2.4 AllRecipeTypes

Archivo: `src/main/java/com/simibubi/create/AllRecipeTypes.java`

Enum que implementa `IRecipeTypeInfo`. Todos los tipos tienen su propio `RecipeType<?>` y `RecipeSerializer<?>` registrado bajo el namespace `create:`.

| Enum entry | Recipe class | Namespace ID |
|------------|-------------|--------------|
| `CONVERSION` | `ConversionRecipe` | `create:conversion` |
| `CRUSHING` | `CrushingRecipe` | `create:crushing` |
| `CUTTING` | `CuttingRecipe` | `create:cutting` |
| `MILLING` | `MillingRecipe` | `create:milling` |
| `BASIN` | `BasinRecipe` | `create:basin` |
| `MIXING` | `MixingRecipe` | `create:mixing` |
| `COMPACTING` | `CompactingRecipe` | `create:compacting` |
| `PRESSING` | `PressingRecipe` | `create:pressing` |
| `SANDPAPER_POLISHING` | `SandPaperPolishingRecipe` | `create:sandpaper_polishing` |
| `SPLASHING` | `SplashingRecipe` | `create:splashing` |
| `HAUNTING` | `HauntingRecipe` | `create:haunting` |
| `DEPLOYING` | `DeployerApplicationRecipe` | `create:deploying` |
| `FILLING` | `FillingRecipe` | `create:filling` |
| `EMPTYING` | `EmptyingRecipe` | `create:emptying` |
| `ITEM_APPLICATION` | `ManualApplicationRecipe` | `create:item_application` |
| `MECHANICAL_CRAFTING` | `MechanicalCraftingRecipe` | `create:mechanical_crafting` |
| `SEQUENCED_ASSEMBLY` | *(multi-step)* | `create:sequenced_assembly` |
| `TOOLBOX_DYEING` | `ToolboxDyeingRecipe` | usa `RecipeType.CRAFTING` |
| `ITEM_COPYING` | `ItemCopyingRecipe` | usa `RecipeType.CRAFTING` |

**API de consulta:**
```java
// Buscar una receta de Create para un inventario dado
Optional<RecipeHolder<R>> result = AllRecipeTypes.CRUSHING.find(inventory, level);
```

---

## 3. Categoría: Kinetics

### 3.1 KineticBlockEntity

Archivo: `src/main/java/com/simibubi/create/content/kinetics/base/KineticBlockEntity.java`

```java
public class KineticBlockEntity extends SmartBlockEntity
        implements IHaveGoggleInformation, IHaveHoveringInformation
```

Clase base de todas las block entities que participan en la red de rotación. Gestiona velocidad, estrés, red cinética y propagación.

#### Campos públicos

```java
public @Nullable Long network;          // ID de la red cinética a la que pertenece
public @Nullable BlockPos source;       // posición del BE que le da velocidad
public boolean networkDirty;            // marca si la red necesita actualización
public boolean updateSpeed;             // si necesita re-propagar velocidad al añadirse
public int preventSpeedUpdate;          // contador para inhibir re-propagación temporal
public SequenceContext sequenceContext; // contexto del SequencedGearshift, si aplica
```

#### Campos protected

```java
protected KineticEffectHandler effects;
protected float speed;
protected float capacity;
protected float stress;
protected boolean overStressed;
protected boolean wasMoved;
protected float lastStressApplied;
protected float lastCapacityProvided;
```

#### Constructor

```java
public KineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state)
```

#### Métodos del ciclo de vida (override de SmartBlockEntity)

```java
@Override
public void initialize()
```
Inicializa la participación en la red cinética. Si ya hay red asignada, añade el BE silenciosamente sin re-propagar.

```java
@Override
public void tick()
```
Tick principal. En servidor: valida conexiones, actualiza red si `networkDirty`, gestiona sonidos de audio en cliente.

```java
@Override
public void remove()
```
Elimina el BE de su red y llama a `detachKinetics()`.

```java
@Override
protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)

@Override
protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket)
```

#### Métodos de velocidad

```java
public float getGeneratedSpeed()
```
Retorna la velocidad que este BE genera. Base devuelve `0`; los generadores (motor, rueda de agua, etc.) hacen override.

```java
public boolean isSource()
```
`true` si `getGeneratedSpeed() != 0`.

```java
public float getSpeed()
```
Velocidad efectiva. Retorna `0` si está sobrecargado (`overStressed`) o si el tiempo está congelado.

```java
public float getTheoreticalSpeed()
```
Velocidad almacenada sin considerar overstress. Equivale al campo `speed`.

```java
public void setSpeed(float speed)
```

```java
public void onSpeedChanged(float previousSpeed)
```
Llamado cuando la velocidad cambia. Gestiona el efecto de parpadeo visual y llama a `setChanged()`.

#### Métodos de red

```java
public boolean hasNetwork()
```

```java
public void setNetwork(@Nullable Long networkIn)
```
Asigna este BE a una red. Si tenía red previa, se elimina de ella.

```java
public KineticNetwork getOrCreateNetwork()
```
Obtiene o crea la `KineticNetwork` de `Create.TORQUE_PROPAGATOR`.

```java
public void updateFromNetwork(float maxStress, float currentStress, int networkSize)
```
Llamado por la red para actualizar los valores de estrés y capacidad.

#### Métodos de source (origen de velocidad)

```java
public boolean hasSource()

public void setSource(BlockPos source)

public void removeSource()

protected void copySequenceContextFrom(KineticBlockEntity sourceBE)
```

#### Métodos de kinetics (attach/detach)

```java
public void attachKinetics()
```
Llama a `RotationPropagator.handleAdded(level, worldPosition, this)`. Se ejecuta cuando `needsSpeedUpdate()` es `true`.

```java
public void detachKinetics()
```
Llama a `RotationPropagator.handleRemoved(level, worldPosition, this)`.

```java
public boolean needsSpeedUpdate()
```
`true` cuando `updateSpeed` está activo.

#### Métodos de estrés

```java
public float calculateStressApplied()
```
Lee `BlockStressValues.getImpact(block)`. Los generadores hacen override para retornar valor negativo (capacidad).

```java
public float calculateAddedStressCapacity()
```
Lee `BlockStressValues.getCapacity(block)`.

```java
public boolean isSpeedRequirementFulfilled()
```
Verifica que `Math.abs(getSpeed()) >= minimumRequiredSpeedLevel.getSpeedValue()`.

```java
public boolean isOverStressed()
```

#### Métodos de propagación personalizada (override para addons)

```java
/**
 * @param target           otro KBE receptor
 * @param stateFrom        estado del emisor
 * @param stateTo          estado del receptor
 * @param diff             diff de posición (to.pos - from.pos)
 * @param connectedViaAxes conectados por IRotate.hasShaftTowards()
 * @param connectedViaCogs conectados por IRotate.hasIntegratedCogwheel()
 * @return factor de velocidad. 0 = sin conexión o reglas estándar
 */
public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom,
        BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs)
```
Override para definir ratios de transmisión personalizados (ej. engranajes con ratio).

```java
/**
 * @param block      el bloque IRotate
 * @param state      estado del bloque
 * @param neighbours lista de posiciones vecinas (añadir más aquí)
 * @return lista modificada de vecinos a considerar
 */
public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours)
```
Permite añadir vecinos adicionales para propagación diagonal (usado por ruedas dentadas pequeñas).

```java
/**
 * @param other      otro KBE
 * @param state      estado de este BE
 * @param otherState estado del otro BE
 * @return true si existe una conexión personalizada que se debe evaluar
 */
public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState)
```

#### Métodos de utilidad estáticos

```java
public static void switchToBlockState(Level world, BlockPos pos, BlockState state)
```
Cambia el estado del bloque sin romper la red cinética si los estados son equivalentes.

```java
public static float convertToDirection(float axisSpeed, Direction d)
```
Convierte velocidad de eje a dirección: negativa si el eje apunta en dirección negativa.

```java
public static float convertToLinear(float speed)
```
`speed / 512f` — convierte RPM a velocidad lineal de bloques/tick.

```java
public static float convertToAngular(float speed)
```
`speed * 360f / 60f / 20f` — convierte RPM a grados/tick.

#### Métodos de audio/cliente

```java
@OnlyIn(Dist.CLIENT)
public void tickAudio()
```
Reproduce sonidos de kinética y engranajes proporcionales a la velocidad.

```java
protected boolean isNoisy()
```
`true` por defecto; los bloques silenciosos pueden hacer override.

```java
public int getRotationAngleOffset(Axis axis)
```
Offset de ángulo para el renderizador; `0` por defecto.

---

### 3.2 KineticBlock

Archivo: `src/main/java/com/simibubi/create/content/kinetics/base/KineticBlock.java`

```java
public abstract class KineticBlock extends Block implements IRotate
```

Clase base de todos los bloques cinéticos. Implementa `IRotate` e integra el ciclo de vida del bloque con su `KineticBlockEntity`.

#### Métodos

```java
@Override
public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
```
Si el bloque cambia de estado pero la conexión cinética es equivalente, establece `preventSpeedUpdate = 2` para evitar re-propagación innecesaria.

```java
@Override
public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving)
```
Delega a `IBE.onRemove()`.

```java
@Override
public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face)
```
`false` por defecto. Override para declarar ejes de shaft.

```java
protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState)
```
`true` si los estados tienen el mismo bloque y el mismo eje de rotación. Previene re-propagación innecesaria.

```java
@Override
public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count)
```
Cuando el vecino cambia forma, limpia la información cinética y fuerza re-propagación.

```java
@Override
public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
```
Registra el avance del jugador y lanza los indicadores de rotación visuales.

```java
public float getParticleTargetRadius()   // default: 0.65f
public float getParticleInitialRadius()  // default: 0.75f
```

---

### 3.3 Jerarquía de bloques cinéticos concretos

#### Simple Relays

```
KineticBlock
├── ShaftBlock          — shaft simple, sin IRotate especial
├── CogWheelBlock       — rueda dentada (small o large según factory method)
│   └── CogWheelBlock.small() / CogWheelBlock.large()
├── EncasedShaftBlock   — shaft encased en casing de andesita/latón
└── EncasedCogwheelBlock — cogwheel encased (large si constructor booleano = true)
```

#### Transmission

```
KineticBlock
├── GearboxBlock            — 4 ejes, transimisión en T/L/cross
├── ClutchBlock             — corta/conecta en función de redstone
├── GearshiftBlock          — invierte dirección con redstone
├── ChainDriveBlock         — cadena, transmite entre ejes paralelos
├── ChainGearshiftBlock     — cadena ajustable por redstone
└── SequencedGearshiftBlock — gearshift con secuencias programables
```

#### Generators

```
KineticBlock
├── CreativeMotorBlock     — genera velocidad configurable ilimitada
├── WaterWheelBlock        — genera hasta 8 RPM según flujo de agua
├── LargeWaterWheelBlock   — genera hasta 4 RPM, más capacidad
├── WindmillBearingBlock   — genera velocidad según número de velas/masa
└── HandCrankBlock         — genera 32 RPM mientras el jugador interactúa
    (SteamEngineBlock — no generator directamente, usa PoweredShaftBlock)
```

#### Machines

```
KineticBlock
├── MillstoneBlock          — muele ítems, stress: 4 SU
├── CrushingWheelBlock      — tritura ítems al pasar entre ruedas, stress: 8 SU
├── MechanicalPressBlock    — presiona ítems sobre basin/depot, stress: 8 SU
├── MechanicalMixerBlock    — mezcla en basin, stress: 4 SU
├── SawBlock                — corta bloques y árboles, stress: 4 SU
├── DrillBlock              — taladra bloques al moverse en contraption, stress: 4 SU
├── EncasedFanBlock         — sopla/aspira entidades y procesa ítems (fan processing), stress: 2 SU
├── MechanicalCrafterBlock  — crafter 9×9, stress: 2 SU
├── SpeedControllerBlock    — controla RPM exactas hacia abajo
├── FlywheelBlock           — almacena energía cinética para el SteamEngine
└── TurntableBlock          — rota bloques sobre él, stress: 4 SU
```

---

### 3.4 GeneratingKineticBlockEntity

Archivo: `src/main/java/com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity.java`

```java
public abstract class GeneratingKineticBlockEntity extends KineticBlockEntity
```

Extiende `KineticBlockEntity` con soporte para fuentes de velocidad. Los generadores hacen override de `getGeneratedSpeed()`.

```java
public boolean reActivateSource;
```
Flag para reactivar la fuente tras un cambio de BlockState.

```java
@Override
public float getGeneratedSpeed()
// debe ser override en subclases para retornar RPM generadas
```

---

### 3.5 Rueda de agua — WaterWheelBlockEntity

Archivo: `src/main/java/com/simibubi/create/content/kinetics/waterwheel/WaterWheelBlockEntity.java`

Extiende `GeneratingKineticBlockEntity`. Escanea bloques de fluido adyacentes para calcular velocidad.

- Capacidad: 32 SU (normal), 128 SU (large)
- Velocidad generada: hasta 8 RPM (normal), 4 RPM (large)

---

### 3.6 Molino de viento — WindmillBearingBlockEntity

Archivo: `src/main/java/com/simibubi/create/content/contraptions/bearing/WindmillBearingBlockEntity.java`

- Capacidad: 512 SU
- Velocidad: proporcional a la masa de velas/bloques en la contraption
- Velocidad máxima: 16 RPM

---

### 3.7 Motor creativo — CreativeMotorBlockEntity

Archivo: `src/main/java/com/simibubi/create/content/kinetics/motor/CreativeMotorBlockEntity.java`

- Capacidad: 16384 SU
- Velocidad máxima configurable: hasta 256 RPM
- Uso: solo para testing/creative

---

## 4. Categoría: SmartBlockEntity y Behaviours

### 4.1 SmartBlockEntity

Archivo: `src/main/java/com/simibubi/create/foundation/blockEntity/SmartBlockEntity.java`

```java
public abstract class SmartBlockEntity extends CachedRenderBBBlockEntity
        implements PartialSafeNBT, IInteractionChecker, SpecialBlockEntityItemRequirement, VirtualBlockEntity
```

Framework base de todos los block entities de Create. Proporciona un sistema de *behaviours* que permite composición de funcionalidad sin herencia múltiple.

#### Campos

```java
private final Map<BehaviourType<?>, BlockEntityBehaviour> behaviours;
private boolean initialized;
private boolean firstNbtRead;
protected int lazyTickRate;     // cada cuántos ticks se llama lazyTick()
protected int lazyTickCounter;
private boolean chunkUnloaded;
private boolean virtualMode;    // para simulación ponder sin mundo real
```

#### Método abstracto obligatorio

```java
public abstract void addBehaviours(List<BlockEntityBehaviour> behaviours);
```
Implementar en cada subclase para registrar los `BlockEntityBehaviour` necesarios.

#### Métodos de ciclo de vida

```java
public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours)
```
Registra behaviours adicionales justo antes de la primera lectura de NBT.

```java
public void initialize()
```
Inicializa todos los behaviours y dispara `lazyTick()`. Se llama una vez al primer tick.

```java
public void tick()
```
Llama `initialize()` si es el primer tick, decrementa `lazyTickCounter`, llama `lazyTick()` si es momento, y llama `tick()` en todos los behaviours.

```java
public void lazyTick()
```
Hook para lógica poco frecuente. Por defecto vacío.

```java
protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket)
protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket)
```
Hooks de serialización. Delegan a la superclase y a todos los behaviours. **Usar estos** en lugar de `saveAdditional`/`loadAdditional`.

```java
public void invalidate()
```
Llamado cuando el bloque es destruido o el chunk descargado. Invalida capabilities.

```java
public void remove()
```
Llamado cuando el bloque es destruido o recogido por una contraption. Desconecta kinetics.

```java
public void destroy()
```
Llamado cuando el bloque es destruido o reemplazado.

#### Gestión de Behaviours

```java
@SuppressWarnings("unchecked")
public <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type)
```
Obtiene un behaviour por tipo. Retorna `null` si no existe.

```java
public void forEachBehaviour(Consumer<BlockEntityBehaviour> action)

public Collection<BlockEntityBehaviour> getAllBehaviours()

public void attachBehaviourLate(BlockEntityBehaviour behaviour)

public void removeBehaviour(BehaviourType<?> type)
```

#### Lazy tick

```java
public void setLazyTickRate(int slowTickRate)
```
Configura el intervalo de `lazyTick()`. Default: 10 ticks.

#### Modo virtual (Ponder)

```java
public void markVirtual()
public boolean isVirtual()
```

#### Utilidades

```java
public boolean isChunkUnloaded()

@Override
public boolean canPlayerUse(Player player)
// el jugador debe estar a ≤8 bloques

public void sendToMenu(RegistryFriendlyByteBuf buffer)

public void refreshBlockState()

public void registerAwardables(List<BlockEntityBehaviour> behaviours, CreateAdvancement... advancements)

public void award(CreateAdvancement advancement)

public void awardIfNear(CreateAdvancement advancement, int range)
```

---

### 4.2 BlockEntityBehaviour (base)

Archivo: `src/main/java/com/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour.java`

```java
public abstract class BlockEntityBehaviour
```

#### API

```java
public SmartBlockEntity blockEntity;

public abstract BehaviourType<?> getType();

public void initialize() {}

public void tick() {}

public void lazyTick() {}

public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}

public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}

public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {}

public void unload() {}

public void destroy() {}

public boolean isSafeNBT() { return false; }

public ItemRequirement getRequiredItems() { return ItemRequirement.NONE; }
```

#### Behaviours principales registrados en el ecosistema Create

| Clase | Propósito |
|-------|-----------|
| `AdvancementBehaviour` | Otorga avances cuando el BE realiza ciertas acciones |
| `FilteringBehaviour` | Filtra ítems con `FilterItem` o `AttributeFilter` |
| `InventoryManagementBehaviour` | Gestión de inventario con slots |
| `SlottedSmartInventory` | Inventario sloteado |
| `BeltProcessingBehaviour` | Procesamiento de ítems en belt |
| `DirectBeltInputBehaviour` | Acepta ítems directamente de belts |
| `TransportedItemStackHandlerBehaviour` | Transporta `TransportedItemStack` |
| `StorageViewBehaviour` | Vista de inventario para goggles |
| `FluidTankBehaviour` | Gestión de tanques de fluido |
| `MountedFluidHandlerBehaviour` | Fluidos en contraptions |
| `LinkBehaviour` | Frecuencias para `RedstoneLinkBlock` |
| `ScrollValueBehaviour` | Valor ajustable con scroll |
| `DisplayLinkBehaviour` | Fuentes/destinos de display link |

---

## 5. Categoría: Contraptions

### 5.1 Contraption (clase base)

Archivo: `src/main/java/com/simibubi/create/content/contraptions/Contraption.java`

```java
public abstract class Contraption
```

2712 líneas. Clase núcleo que representa una estructura de bloques en movimiento. Contiene el mapa de bloques, entidades montadas, actors (movementBehaviours), y toda la lógica de serialización/deserialización.

#### Campos clave

```java
public Map<BlockPos, BlockInfo> blocks;      // bloques dentro de la contraption
public List<MutableObject<Entity>> superGlue; // super glue entre bloques
public Map<BlockPos, BlockEntityInfo> storage; // block entities serializadas
public List<MountedItemStorage> inventories;  // inventarios montados
public List<MountedFluidStorage> fluidStorages; // tanques montados
public Map<BlockPos, MovementContext> actors; // actores (MovementBehaviour)
public AABB bounds;                           // bounding box de la contraption
```

#### Tipos concretos de Contraption

| Clase | Asociada a |
|-------|-----------|
| `PistonContraption` | `MechanicalPistonBlock` |
| `BearingContraption` | `MechanicalBearingBlock`, `WindmillBearingBlock` |
| `PulleyContraption` | `PulleyBlock` |
| `ElevatorContraption` | `ElevatorPulleyBlock` |
| `GantryContraption` | `GantryCarriageBlock` |
| `MountedContraption` | `CartAssemblerBlock` |
| `TrainContraption` | trenes |

---

### 5.2 MovementBehaviour

Archivo: `src/main/java/com/simibubi/create/api/behaviour/movement/MovementBehaviour.java`

```java
public interface MovementBehaviour
```

Interfaz que permite a un bloque ejecutar acciones mientras está montado en una contraption en movimiento.

#### API completa

```java
/**
 * Visita el bloque cuando la contraption pasa por una posición del mundo.
 * Llamado en servidor.
 */
default void visitNewPosition(MovementContext context, BlockPos pos) {}

/**
 * Tick de movimiento. Llamado cada tick mientras la contraption se mueve.
 */
default void tick(MovementContext context) {}

/**
 * La contraption se detuvo. Limpia recursos.
 */
default void stopMoving(MovementContext context) {}

/**
 * Se llama cuando la contraption se desmonta/ensambla.
 * @return true si el actor debe ser destruido al desmontarse
 */
default boolean renderAsNormalBlockEntity() { return false; }

/**
 * ¿Debe este actor recibir eventos de movimiento?
 */
default boolean hasSpecialMovementHandler() { return false; }

/**
 * @return true si este actor debe ser ignorado al calcular el masa de la contraption
 */
default boolean isActive(MovementContext context) { return true; }

/**
 * Registra este MovementBehaviour para un bloque en AllBlocks.
 * Uso: .onRegister(movementBehaviour(new MiMovementBehaviour()))
 */
static Consumer<Block> movementBehaviour(MovementBehaviour behaviour) { ... }
```

#### Implementaciones en Create

| Clase | Bloque |
|-------|--------|
| `DrillMovementBehaviour` | `MECHANICAL_DRILL` — taladra bloques al moverse |
| `SawMovementBehaviour` | `MECHANICAL_SAW` — corta árboles/bloques |
| `HarvesterMovementBehaviour` | `MECHANICAL_HARVESTER` — cosecha cultivos |
| `PloughMovementBehaviour` | `MECHANICAL_PLOUGH` — ara tierra |
| `RollerMovementBehaviour` | `MECHANICAL_ROLLER` — pavimenta bloques |
| `DeployerMovementBehaviour` | `DEPLOYER` — usa ítems sobre bloques |
| `FunnelMovementBehaviour` | `ANDESITE_FUNNEL`/`BRASS_FUNNEL` |
| `BasinMovementBehaviour` | `BASIN` |
| `FluidTankMovementBehavior` | `FLUID_TANK` |
| `PortableStorageInterfaceMovement` | `PORTABLE_STORAGE_INTERFACE` |
| `BlazeBurnerMovementBehaviour` | `BLAZE_BURNER` |
| `SeatMovementBehaviour` | `SEAT` |
| `ControlsMovementBehaviour` | `TRAIN_CONTROLS` |
| `ContraptionControlsMovement` | `CONTRAPTION_CONTROLS` |
| `StabilizedBearingMovementBehaviour` | `MECHANICAL_BEARING` |
| `BellMovementBehaviour` | `PECULIAR_BELL`, `DESK_BELL` |
| `HauntedBellMovementBehaviour` | `HAUNTED_BELL` |
| `ContactMovementBehaviour` | `REDSTONE_CONTACT` |

---

### 5.3 MovementContext

Archivo: `src/main/java/com/simibubi/create/content/contraptions/behaviour/MovementContext.java`

```java
public class MovementContext
```

Objeto de contexto pasado a `MovementBehaviour` con toda la información del estado actual del actor dentro de la contraption.

#### Campos

```java
public BlockState state;              // estado del bloque actor
public CompoundTag blockEntityData;   // NBT del BE serializado
public Vec3 motion;                   // velocidad actual de la contraption
public Vec3 relativeMotion;           // movimiento relativo del actor
public Vec3 position;                 // posición actual en el mundo
public float rotation;                // ángulo de rotación (bearing/turntable)
public Level world;
public Contraption contraption;
public Object data;                   // datos custom del actor (persistente entre ticks)
public boolean stall;                 // true = pedir a la contraption que se detenga
public boolean attackedEntities;      // si el actor ya atacó entidades este tick
public boolean isStalled;             // si la contraption está actualmente detenida
```

---

### 5.4 Trenes

#### Arquitectura

```
GlobalRailwayManager          — singleton server-side, gestiona todas las vías
├── RailwaySavedData          — serialización al disco
├── TrackGraph                — grafo de vías (nodos = intersecciones)
│   ├── TrackNode             — nodo de via
│   └── TrackEdge             — edge con NavigableMap de EdgePoints
├── EdgePointType<T>          — tipos: STATION, SIGNAL, OBSERVER
└── Train                     — entidad lógica de tren
    ├── TravellingPoint       — posición del tren en el grafo
    ├── Carriage[]            — vagones del tren
    │   ├── BogeyInstance     — bogey delantero/trasero
    │   └── ContraptionEntity — entidad física del vagón
    └── ScheduleRuntime       — ejecuta el Schedule del tren
```

#### ScheduleRuntime

Archivo: `src/main/java/com/simibubi/create/content/trains/schedule/ScheduleRuntime.java`

Gestiona la ejecución del `Schedule` de un tren. Evalúa condiciones de espera y ejecuta instrucciones de destino.

```java
public class ScheduleRuntime
```

Campos relevantes:
```java
public Schedule schedule;
public boolean isAutoSchedule;
public int currentEntry;
public List<Integer> conditionProgress;
public Train train;
```

#### Schedule

Archivo: `src/main/java/com/simibubi/create/content/trains/schedule/Schedule.java`

```java
public class Schedule
```

```java
public List<ScheduleEntry> entries;  // lista de instrucciones + condiciones
public boolean cyclic;               // si el schedule es cíclico
```

`ScheduleEntry` contiene:
- `ScheduleInstruction instruction` — qué hacer (destino, espera, etc.)
- `List<List<ScheduleWaitCondition>> conditions` — condiciones AND dentro de OR

---

## 6. Categoría: Fluids

### 6.1 Tuberías

- **`FluidPipeBlock`** / **`FluidPipeBlockEntity`** — tubería básica de cobre. Conecta automáticamente con vecinos compatibles.
- **`EncasedPipeBlock`** / `FluidPipeBlockEntity` — misma lógica, pero encased en casing de cobre.
- **`GlassFluidPipeBlock`** / **`StraightPipeBlockEntity`** — tubería de cristal; visual transparency.
- **`SmartFluidPipeBlock`** / **`SmartFluidPipeBlockEntity`** — filtra fluidos según frecuencia redstone.

### 6.2 Bomba

**`PumpBlock`** / **`PumpBlockEntity`**

- Empuja fluido en la dirección que apunta
- Estrés: 4 SU
- La dirección se invierte si se activa con redstone

### 6.3 Tanque

**`FluidTankBlock`** / **`FluidTankBlockEntity`**

- Conecta verticalmente en multi-bloque
- `FLUID_TANK`: capacidad configurable (default: 8 buckets por bloque)
- `CREATIVE_FLUID_TANK`: capacidad infinita

### 6.4 Spout y Drain

- **`SpoutBlock`** / **`SpoutBlockEntity`** — llena contenedores de ítem con fluidos del tanque de abajo
- **`ItemDrainBlock`** / **`ItemDrainBlockEntity`** — vacía contenedores de ítem al tanque de abajo

### 6.5 Hose Pulley

**`HosePulleyBlock`** / **`HosePulleyBlockEntity`**

- Sube/baja un "chorrito" de fluido desde el mundo a un tanque o viceversa
- Estrés: 4 SU

### 6.6 Válvula

**`FluidValveBlock`** / **`FluidValveBlockEntity`**

- Corta el flujo cuando se activa con redstone (o al contrario, configurable)
- `ValveHandleBlock` — variante que usa el jugador para abrir/cerrar manualmente

---

## 7. Categoría: Logistics

### 7.1 Belt

**`BeltBlock`** / **`BeltBlockEntity`**  
Transporta ítems en una dirección. La velocidad de transporte es proporcional a la velocidad cinética.

**Propiedades:**
- Un belt se extiende entre dos shafts en el mismo eje
- Los ítems sobre el belt son representados como `TransportedItemStack`
- Stress: sin impacto propio (consume del shaft que lo mueve)

### 7.2 Funnels

| Bloque | Clase | Función |
|--------|-------|---------|
| `ANDESITE_FUNNEL` | `AndesiteFunnelBlock` | Extrae o inserta ítems (1 stack a la vez) |
| `BRASS_FUNNEL` | `BrassFunnelBlock` | Con filtrado y velocidad mayor |
| `ANDESITE_BELT_FUNNEL` | `BeltFunnelBlock` | Versión para montaje en belt |
| `BRASS_BELT_FUNNEL` | `BeltFunnelBlock` | Versión brass para belt |

### 7.3 Tunnels

| Bloque | Clase | Función |
|--------|-------|---------|
| `ANDESITE_TUNNEL` | `BeltTunnelBlock` | Cubre el belt; puede redistribuir ítems |
| `BRASS_TUNNEL` | `BrassTunnelBlock` | Con filtrado y splitting configurable |

### 7.4 Chutes

| Bloque | Clase | Función |
|--------|-------|---------|
| `CHUTE` | `ChuteBlock` | Cae ítems verticalmente |
| `SMART_CHUTE` | `SmartChuteBlock` | Con filtrado redstone |

### 7.5 Vault y Depot

- **`ITEM_VAULT`** — almacén grande de ítems (combina hasta 4 en multi-bloque horizontal)
- **`DEPOT`** — plataforma para depositar/recoger ítems manualmente o con belt/funnel
- **`WEIGHTED_EJECTOR`** — lanza ítems en arco a un destino configurado

### 7.6 Mechanical Arm

**`ArmBlock`** / **`ArmBlockEntity`**  
Transporta ítems entre múltiples fuentes y destinos configurados con clic-derecho.

- Estrés: 2 SU
- Rango configurable
- Soporta filtrado en cada punto de recogida/entrega

### 7.7 Packager System (nuevo en 1.21)

| Bloque | Función |
|--------|---------|
| `PACKAGER` | Empaqueta ítems en paquetes según dirección |
| `REPACKAGER` | Desempaqueta y re-empaqueta |
| `PACKAGE_FROGPORT` | Envía/recibe paquetes a través de la red logística |
| `PACKAGE_POSTBOXES` | Buzones de recepción de paquetes (×16 colores) |
| `STOCK_LINK` | Enlaza frogports/packagers para logística logística |
| `STOCK_TICKER` | Muestra estado de stock en la red |

---

## 8. Categoría: Schematics

### 8.1 Schematic Table

**`SchematicTableBlock`** / **`SchematicTableBlockEntity`**  
Escribe un esquemático del mundo a un ítem `SCHEMATIC` usando `SCHEMATIC_AND_QUILL`.

### 8.2 Schematicannon

**`SchematicannonBlock`** / **`SchematicannonBlockEntity`**  
Construye estructuras automáticamente a partir de ítems `SCHEMATIC`.

- Inventario de materiales
- Configuración de tolerancia de reemplazo
- Puede filtrar bloques a construir

---

## 9. Categoría: Equipment

### 9.1 Engineer's Goggles

**`GogglesItem`** (`create:goggles`)

- Al usarlos se activa `IHaveGoggleInformation.addToGoggleTooltip()` en el BE que el jugador mira
- También activa `IHaveHoveringInformation.addToTooltip()` al pasar el cursor
- Compatibilidad con Curios/slot de cabeza

### 9.2 Wrench

**`WrenchItem`** (`create:wrench`)

- Clic derecho sobre un bloque: rota/configura
- Clic derecho + Shift: desmonta bloques especiales
- Tag: `neoforge:tools/wrench`

### 9.3 Wand of Symmetry

**`SymmetryWandItem`** (`create:wand_of_symmetry`)

- Crea un plano de simetría en el punto de interacción
- Soporta simetría X, Z y plano horizontal

### 9.4 Toolbox

**`ToolboxBlock`** / **`ToolboxBlockEntity`** (`create:<color>_toolbox`)

- Almacenamiento de ítems accesible desde lejos
- Compatible con contraptions (montado storage)
- 16 colores disponibles

### 9.5 Super Glue

**`SuperGlueItem`** (`create:super_glue`)

- Pega bloques a contraptions para que se muevan con ellas

### 9.6 Sand Paper

**`SandPaperItem`** (`create:sand_paper`, `create:red_sand_paper`)

- Pulir ítems manualmente (clic sostenido sobre inventario)
- Genera recetas de tipo `SANDPAPER_POLISHING`

---

## 10. API para Addons

### 10.1 Registrar un MovementBehaviour

```java
// En AllBlocks o en el registro de tu addon:
REGISTRATE.block("mi_bloque", MiBloque::new)
    .onRegister(MovementBehaviour.movementBehaviour(new MiMovementBehaviour()))
    .register();

// La clase:
public class MiMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        // context.world, context.position, context.motion, etc.
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        // llamado cada vez que la contraption entra a un nuevo bloque de mundo
    }
}
```

### 10.2 Registrar estrés/capacidad

```java
// En tu evento de registro:
BlockStressValues.map(MiBloque.class, 8.0); // 8 SU de impacto

// O con capacidad (generador):
BlockStressValues.setCapacity(MiBloque.class, 256.0);
BlockStressValues.setGeneratorSpeed(MiBloque.class, 64, true); // 64 RPM, variable
```

### 10.3 Registrar una InteractionBehaviour

```java
REGISTRATE.block("mi_bloque", MiBloque::new)
    .onRegister(MovingInteractionBehaviour.interactionBehaviour(new MiInteraction()))
    .register();

public class MiInteraction implements MovingInteractionBehaviour {
    @Override
    public InteractionResult onInteract(MovementContext ctx, Player player,
            InteractionHand hand, BlockHitResult hit) {
        // lógica de interacción mientras el bloque está en movimiento
        return InteractionResult.SUCCESS;
    }
}
```

### 10.4 KineticBlockEntity personalizado con ratio de transmisión

```java
public class MiKineticBE extends KineticBlockEntity {

    public MiKineticBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target,
            BlockState stateFrom, BlockState stateTo,
            BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        // ratio 2:1 hacia target
        if (target instanceof MiOtroBE)
            return 2.0f;
        return 0; // no hay conexión o usa reglas estándar
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // añadir behaviours de Create
        behaviours.add(new FilteringBehaviour(this, new FilteringHandler()));
    }
}
```

### 10.5 Añadir un tipo de receta de procesamiento

```java
// 1. Crea la clase de receta extendiendo StandardProcessingRecipe
public class MiReceta extends StandardProcessingRecipe<SimpleProcessingInput> {
    public MiReceta(ProcessingRecipeParams params) { super(params); }

    @Override
    protected int getMaxInputCount() { return 1; }
    @Override
    protected int getMaxOutputCount() { return 6; }
}

// 2. Regístrala en tu enum de recipe types equivalente al AllRecipeTypes de Create
// 3. Haz que tu máquina busque recetas con:
Optional<RecipeHolder<MiReceta>> recipe =
    MiRecipeTypes.MI_TIPO.find(inventario, nivel);
```

---

## 11. JSON Recipe Types — Schemas

### 11.1 StandardProcessingRecipe (formato base compartido)

Los tipos `crushing`, `cutting`, `milling`, `basin`, `mixing`, `compacting`, `pressing`, `sandpaper_polishing`, `splashing`, `haunting`, `deploying` comparten este esquema:

```json
{
  "type": "create:<tipo>",
  "ingredients": [
    { "item": "minecraft:wheat" },
    { "tag": "forge:dusts/redstone", "amount": 2 }
  ],
  "results": [
    { "item": "create:wheat_flour", "count": 2 },
    { "item": "minecraft:wheat_seeds", "chance": 0.5 }
  ],
  "processingTime": 200,
  "heatRequirement": "none"   // "none" | "heated" | "superheated"
}
```

### 11.2 Crushing (`create:crushing`)

```json
{
  "type": "create:crushing",
  "ingredients": [{ "item": "minecraft:gravel" }],
  "results": [
    { "item": "minecraft:flint",           "count": 1 },
    { "item": "create:powdered_obsidian",  "chance": 0.25 }
  ],
  "processingTime": 400
}
```

### 11.3 Milling (`create:milling`)

```json
{
  "type": "create:milling",
  "ingredients": [{ "item": "minecraft:wheat" }],
  "results": [{ "item": "create:wheat_flour" }],
  "processingTime": 200
}
```

### 11.4 Mixing (`create:mixing`)

```json
{
  "type": "create:mixing",
  "ingredients": [
    { "item": "minecraft:copper_ingot", "count": 4 },
    { "item": "create:zinc_ingot",      "count": 4 }
  ],
  "results": [{ "item": "create:brass_ingot", "count": 4 }],
  "heatRequirement": "heated",
  "processingTime": 120
}
```

### 11.5 Compacting (`create:compacting`)

Igual a mixing pero orientado a condensar (ej. lingotes → bloque).

### 11.6 Pressing (`create:pressing`)

```json
{
  "type": "create:pressing",
  "ingredients": [{ "item": "minecraft:iron_ingot" }],
  "results": [{ "item": "create:iron_sheet" }],
  "processingTime": 200
}
```

### 11.7 Filling / Emptying

```json
{
  "type": "create:filling",
  "ingredients": [{ "item": "minecraft:glass_bottle" }],
  "fluidIngredients": [{ "fluidTag": "minecraft:water", "amount": 333 }],
  "results": [{ "item": "minecraft:potion", "nbt": "{ ... }" }]
}
```

```json
{
  "type": "create:emptying",
  "ingredients": [{ "item": "minecraft:lava_bucket" }],
  "results": [
    { "item": "minecraft:bucket" },
    { "fluid": "minecraft:lava", "amount": 1000 }
  ]
}
```

### 11.8 Haunting / Splashing (Fan Processing)

```json
{
  "type": "create:haunting",
  "ingredients": [{ "item": "minecraft:dirt" }],
  "results": [{ "item": "minecraft:soul_soil" }]
}
```

```json
{
  "type": "create:splashing",
  "ingredients": [{ "item": "minecraft:gravel" }],
  "results": [
    { "item": "minecraft:sand", "count": 1 },
    { "item": "minecraft:clay_ball", "chance": 0.15 }
  ]
}
```

### 11.9 Mechanical Crafting (`create:mechanical_crafting`)

```json
{
  "type": "create:mechanical_crafting",
  "pattern": [
    "PPP",
    "PCP",
    "PPP"
  ],
  "key": {
    "P": { "item": "minecraft:iron_ingot" },
    "C": { "item": "minecraft:compass" }
  },
  "result": { "item": "create:precision_mechanism" }
}
```

Soporta grids hasta 9×9.

### 11.10 Sequenced Assembly (`create:sequenced_assembly`)

```json
{
  "type": "create:sequenced_assembly",
  "ingredient": { "item": "create:incomplete_precision_mechanism" },
  "transitionalItem": { "item": "create:incomplete_precision_mechanism" },
  "sequence": [
    {
      "type": "create:deploying",
      "ingredients": [
        { "item": "create:incomplete_precision_mechanism" },
        { "item": "minecraft:gold_nugget" }
      ],
      "results": [{ "item": "create:incomplete_precision_mechanism" }]
    },
    {
      "type": "create:pressing",
      "ingredients": [{ "item": "create:incomplete_precision_mechanism" }],
      "results": [{ "item": "create:incomplete_precision_mechanism" }]
    }
  ],
  "results": [
    { "item": "create:precision_mechanism",   "chance": 1.0 },
    { "item": "create:precision_mechanism",   "chance": 0.5 }
  ],
  "loops": 4
}
```

---

## 12. Eventos Custom de Create

### `BlockEntityBehaviourEvent`

Archivo: `src/main/java/com/simibubi/create/api/event/BlockEntityBehaviourEvent.java`

```java
public class BlockEntityBehaviourEvent extends Event
```

Disparado en `NeoForge.EVENT_BUS` cuando un `SmartBlockEntity` se inicializa por primera vez. Permite que otros mods añadan `BlockEntityBehaviour` a BEs de Create.

```java
@SubscribeEvent
public static void onBehaviourInit(BlockEntityBehaviourEvent event) {
    if (event.blockEntity instanceof KineticBlockEntity kbe) {
        event.behaviours.put(MiBehaviour.TYPE, new MiBehaviour(kbe));
    }
}
```

### `KineticsChangeEvent` (ComputerCraft)

Archivo: `src/main/java/com/simibubi/create/compat/computercraft/events/KineticsChangeEvent.java`

Disparado cuando la velocidad/estrés de un `KineticBlockEntity` cambia. Integración con CC:Tweaked.

---

*Documento generado leyendo el código fuente del commit `802dddddd0f3b1a5eb4ae56272483dee00d17890`.*  
*Rama: `mc1.21.1/dev` — Fecha: 2026-05-19.*
