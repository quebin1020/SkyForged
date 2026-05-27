# Create Aeronautics — Documentación de Referencia

**Repo oficial:** https://github.com/Creators-of-Aeronautics/Simulated-Project  
**Commit analizado:** `87a83ba` (rama `main`)  
**Organización / Autor:** Creators-of-Aeronautics (eriksonn + equipo)  
**Módulo Java:** `dev.eriksonn.aeronautics` (mod id: `aeronautics`)  
**Módulo Offroad:** `dev.ryanhcode.offroad` (incluido en el mismo repo)  
**Loader:** NeoForge 1.21.1  
**Dependencias:** Sable (https://github.com/ryanhcode/sable), Create  
**Licencia:** ver LICENSE.md en el repo

> **Nota de verificación:** El repo se llamaba erróneamente "EriksoModding" en la info previa. La organización real es **Creators-of-Aeronautics** y el paquete Java raíz es `dev.eriksonn.aeronautics`. El repo `Modders-of-Create/Create-Aeronautics` (2022, Fabric 1.18) es completamente diferente y abandonado.

---

## 1. Overview

Create Aeronautics es un mod de contenido que construye sobre Sable + Create para añadir **aeronaves con física real**. A diferencia de las contraptions normales de Create (que se mueven de forma discreta), las estructuras de Aeronautics son **sub-levels de Sable** con física de cuerpo rígido.

El repo contiene dos módulos independientes que se distribuyen juntos:

| Módulo | Mod ID | Descripción |
|---|---|---|
| `aeronautics` | `aeronautics` | Hot air balloons, propellers, levitite |
| `offroad` | `offroad` | Wheels, borehead drills |

**Requisitos:**
- Sable (library mod de física)
- Create (kinetics, contraptions, windmill sails)
- Simulated (base interna del equipo — `dev.simulated_team.simulated`)

---

## 2. Registros

### Bloques (`AeroBlocks`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroBlocks.java`

#### Sistema de globo aerostático

| Constante | Nombre en-game | Clase | Notas |
|---|---|---|---|
| `WHITE_ENVELOPE_BLOCK` | Hot Air Envelope | `EnvelopeBlock` | DyeColor.WHITE |
| `DYED_ENVELOPE_BLOCKS` | [Color] Hot Air Envelope | `EnvelopeBlock` | 16 variantes de color |
| `ENVELOPE_ENCASED_SHAFTS` | [Color] Envelope Encased Shaft | `EnvelopeEncasedShaftBlock` | 16 variantes, encierra un Shaft de Create |
| `HOT_AIR_BURNER` | Hot Air Burner | `HotAirBurnerBlock` | ID: `adjustable_burner` |
| `STEAM_VENT` | Steam Vent | `SteamVentBlock` | Emite gas vapor |

#### Sistema de propulsión

| Constante | Nombre en-game | Clase | Stress Impact |
|---|---|---|---|
| `PROPELLER_BEARING` | Propeller Bearing | `PropellerBearingBlock` | 2.0 SU |
| `GYROSCOPIC_PROPELLER_BEARING` | Gyroscopic Propeller Bearing | `GyroscopicPropellerBearingBlock` | 2.0 SU |
| `SMART_PROPELLER` | Smart Propeller | `SmartPropellerBlock` | 4.0 SU |
| `ANDESITE_PROPELLER` | Andesite Propeller | `AndesitePropellerBlock` | 4.0 SU |
| `WOODEN_PROPELLER` | Wooden Propeller | `WoodenPropellerBlock` | 4.0 SU |

#### Otros

| Constante | Nombre en-game | Clase | Notas |
|---|---|---|---|
| `MOUNTED_POTATO_CANNON` | Mounted Potato Cannon | `MountedPotatoCannonBlock` | 2.0 SU |
| `LEVITITE` | Levitite | `Block` | Luz 10, strength 7/20, no loot table propio |
| `PEARLESCENT_LEVITITE` | Pearlescent Levitite | `Block` | Variante perlada |

### Items (`AeroItems`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroItems.java`

| Constante | Nombre | Clase | Notas |
|---|---|---|---|
| `AVIATORS_GOGGLES` | Aviator's Goggles | `AviatorsGogglesItem` | Armor head, freeze immune |
| `MUSIC_DISC_CLOUD_SKIPPER` | Music Disc | `Item` | Rarity.RARE, stacksTo(1), jukebox |
| `ENDSTONE_POWDER` | End Stone Powder | `Item` | Componente `Levitating.END_STONE` |

### Block Entities (`AeroBlockEntityTypes`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroBlockEntityTypes.java`

| Constante | ID | Clase | Blocks válidos |
|---|---|---|---|
| `STEAM_VENT` | `steam_vent` | `SteamVentBlockEntity` | `STEAM_VENT` |
| `HOT_AIR_BURNER` | `adjustable_burner` | `HotAirBurnerBlockEntity` | `HOT_AIR_BURNER` |
| `PROPELLER_BEARING` | `propeller_bearing` | `PropellerBearingBlockEntity` | `PROPELLER_BEARING` |
| `GYROSCOPIC_PROPELLER_BEARING` | `gyroscopic_propeller_bearing` | `GyroscopicPropellerBearingBlockEntity` | `GYROSCOPIC_PROPELLER_BEARING` |
| `ENVELOPE_ENCASED_SHAFT` | `envelope_encased_shaft` | `KineticBlockEntity` | Todos los `ENVELOPE_ENCASED_SHAFTS` |
| `ANDESITE_PROPELLER` | `andesite_propeller` | `AndesitePropellerBlockEntity` | `ANDESITE_PROPELLER` |
| `WOODEN_PROPELLER` | `wooden_propeller` | `WoodenPropellerBlockEntity` | `WOODEN_PROPELLER` |
| `SMART_PROPELLER` | `smart_propeller` | `SmartPropellerBlockEntity` | `SMART_PROPELLER` |
| `MOUNTED_POTATO_CANNON` | `mounted_potato_cannon` | `MountedPotatoCannonBlockEntity` | `MOUNTED_POTATO_CANNON` |

### Registries custom (`AeroRegistries`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroRegistries.java`

| Registry key | Tipo de entrada | Uso |
|---|---|---|
| `aeronautics:lifting_gas_type` | `LiftingGasType` | Tipos de gas para globos |
| `aeronautics:levitite_crystal_propagation_context` | `CrystalPropagationContext` | Comportamiento de cristalización de levitite |

---

## 3. Sistema de Propulsión

### 3.1 Propeller Bearing (`PropellerBearingBlockEntity`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/content/blocks/propeller/bearing/propeller_bearing/PropellerBearingBlockEntity.java`

El Propeller Bearing es el propulsor principal de aeronaves. Funciona como un `MechanicalBearingBlockEntity` de Create que, en lugar de solo rotar bloques, también convierte esa rotación en **fuerza física** sobre el sub-level.

**Herencia:**
```
MechanicalBearingBlockEntity (Create)
    └── PropellerBearingBlockEntity
            implements BlockEntitySubLevelPropellerActor  (Sable)
            implements BlockEntityPropeller               (Sable)
            implements IHaveGoggleInformation             (Create)
```

**Cálculo de fuerza:**
```java
double getThrust() {
    return Math.pow(totalSailPower, 1.5f) * getDirectionIndependentSpeed() * getConfigThrust();
}

double getAirflow() {
    return Math.sqrt(totalSailPower) * getDirectionIndependentSpeed() * getConfigAirflowMult();
}
```

Donde `totalSailPower` = suma de los windmill sails de Create en la contraption. Las velas cuadradas tienen más sail power que las velas angulares.

El thrust final aplicado al sub-level se escala por:
- Presión de aire de la dimensión (`DimensionPhysicsData.getAirPressure()`)
- Velocidad actual del sub-level en dirección de empuje (eficiencia cae cuando ya se mueve en esa dirección)

**ThrustDirection (ScrollOptionBehaviour):**
- `RIGHT_HANDED` — Pull en giro horario
- `LEFT_HANDED` — Push en giro horario

**Configuración (AeroConfig.server().physics):**
- `propellerBearingThrust` — multiplicador de thrust base
- `propellerBearingAirflowMult` — multiplicador de airflow

La fuerza se aplica al `ForceGroups.PROPULSION` de Sable en cada physics sub-step via `BlockEntitySubLevelPropellerActor.applyForces()`.

---

### 3.2 Small Propellers (Andesite, Wooden, Smart)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/content/blocks/propeller/small/BasePropellerBlockEntity.java`

Los propellers pequeños son `KineticBlockEntity` de Create que también implementan `BlockEntitySubLevelPropellerActor` y `BlockEntityPropeller`.

**Herencia:**
```
KineticBlockEntity (Create)
    └── BasePropellerBlockEntity
            implements BlockEntitySubLevelPropellerActor  (Sable)
            implements BlockEntityPropeller               (Sable)
            ├── AndesitePropellerBlockEntity
            ├── WoodenPropellerBlockEntity
            └── SmartPropellerBlockEntity
```

**Cálculo de thrust/airflow:**
```java
double getThrust() {
    return getConfigThrust() * getDirectionIndependentSpeed();
}

double getAirflow() {
    return getConfigAirflow() * getDirectionIndependentSpeed();
}

boolean isActive() {
    return Math.abs(rotationSpeed) > 0.01f;
}
```

`getDirectionIndependentSpeed()` = `axis_direction_step × rotationSpeed × (10/3) × (reversed ? -1 : 1)`

Los propellers pequeños también empujan entidades en el mundo via `PropellerActorBehaviour.pushEntities()` y emiten partículas de airflow.

**Diferencias entre tipos:**
| Propeller | Config thrust | Config airflow | Notas |
|---|---|---|---|
| Wooden | Bajo | Bajo | Más fácil de craftear |
| Andesite | Medio | Medio | Intercambiable con Wooden (shapeless) |
| Smart | Alto | Alto | Requiere Gyro Mechanism del mod Simulated |

---

### 3.3 Gyroscopic Propeller Bearing

`GyroscopicPropellerBearingBlockEntity` — similar al Propeller Bearing pero implementa `BlockEntitySubLevelReactionWheel` de Sable además de la propulsión. Proporciona **estabilización giroscópica** que resiste cambios de orientación.

**Requiere:** Gyro Mechanism (item del módulo Simulated).

---

### 3.4 `PropellerActorBehaviour`

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/content/blocks/propeller/behaviour/PropellerActorBehaviour.java`

Helper de Create (`BlockEntityBehaviour`) que maneja:
- **Entity pushing**: aplica velocidad a entidades en el cono de airflow
- **Particle spawning**: partículas visuales de airflow (configurable con capas concéntricas)
- **Goggle display**: muestra thrust/airflow al jugador

```java
// Configuración de capas del propeller
behaviour.addSimpleLayer(offset, radius);  // anillo de partículas
behaviour.setParticleAmountUpdater(() -> 0.12 * Math.abs(rotationSpeed));
behaviour.setParticleCountProperties(maxPerTick, smoothingFrames);
```

---

## 4. Sistema de Buoyancy

### 4.1 Hot Air Balloons

El sistema de globo aerostático funciona en capas:

```
EnvelopeBlocks (16+ variantes de color)
     │ definen el volumen del globo (BalloonLayerGraph)
     ▼
HotAirBurnerBlock
     │ emite LiftingGasType → llena el Balloon
     ▼
ServerBalloon (ServerSubLevel)
     │ acumula gas → aplica fuerza ForceGroups.BALLOON_LIFT
```

**`EnvelopeBlock`** — El bloque de tela del globo. Taggeado como `#aeronautics:airtight`, `#aeronautics:envelope`. Flammable (30/60). El sistema detecta la forma del globo mediante `BalloonLayerGraph` que escanea bloques conectados airtight.

**`HotAirBurnerBlockEntity`** (`adjustable_burner`):
- Extiende `SmartBlockEntity` (Create)
- Implementa `BlockEntityLiftingGasProvider`
- Redstone 0-15 controla el output de gas
- ScrollValueBehaviour (UI de Create) ajusta la cantidad máxima de hot air
- Emite `LiftingGasType` al `Balloon` conectado arriba

**`SteamVentBlockEntity`** — Alternativa al burner, emite gas tipo `SteamLiftingGas` (diferente curva de lift).

**`ServerBalloon`** — En cada physics tick, el `BalloonMap.physicsTick()` calcula el lift a aplicar según:
- Volumen de gas actual vs volumen total del globo
- `LiftingGasType.getLiftStrength()`
- Factor de responsiveness (convergencia suave hacia el objetivo)

La fuerza se aplica via `ForceGroups.BALLOON_LIFT` al sub-level.

---

### 4.2 Levitite (Floating Rocks)

Levitite es el "magic floating rock" del mod. Es un bloque normal de Minecraft que flota porque Sable lee sus propiedades de un JSON de `physics_block_properties` y/o `floating_materials`.

**Blocks:**
- `LEVITITE` — Cristal verde, light level 10
- `PEARLESCENT_LEVITITE` — Variante perlada/rosada

Ambos tienen `SableTags.ALWAYS_CHUNK_RENDERING` (no instanciados, siempre chunk rendering) y properties de DataComponent `AeroDataComponents.LEVITATING`.

**Sistema de cristalización:** Levitite puede *crecer* desde bloques de barro, arcilla, etc. cuando están cerca de un catalizador (campfire, magma block, blaze burner). Esto permite "world generation" de levitite flotante.

Controlado por:
- `LevititeCrystallizerManager` — tickea la cristalización por Level
- `CrystalPropagationContext` — registry extensible (ver sección API)
- Tags: `#aeronautics:levitite_catalyzer`, `#aeronautics:levitite_breakable`, `#aeronautics:levitite_soul_catalyzer`

---

### 4.3 `LiftingGasType` — Interfaz API

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/content/blocks/hot_air/lifting_gas/LiftingGasType.java`

Esta interfaz es extensible para addons.

```java
public interface LiftingGasType {
    Component getName();
    double getFillingTime();              // ticks para llenar el globo completamente
    double getEmptyingTime();             // ticks para vaciarlo
    double getLiftStrength();             // fuerza de lift (N por m³ de gas)
    double getResponsivenessAdjustmentFactor();  // velocidad de convergencia
    double getResponsivenessAdjustmentRange();   // rango de convergencia ajustada
}
```

**Implementaciones registradas:**

| ID | Clase | Notas |
|---|---|---|
| `aeronautics:default` | `DefaultLiftingGas` | Gas caliente estándar; fill time=180t; lift de config |
| `aeronautics:steam` | `SteamLiftingGas` | Steam del SteamVent; curva diferente |

**Fuente registry:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroLiftingGasTypes.java`

---

## 5. Tags

### Block Tags (`AeroTags.BlockTags`)

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroTags.java`

| Tag | Namespace | Descripción |
|---|---|---|
| `aeronautics:airtight` | Contiene `#aeronautics:envelope` + `#minecraft:wool` | Bloques que forman volumen airtight del globo |
| `aeronautics:envelope` | Todos los EnvelopeBlocks | Bloques de tela del globo |
| `aeronautics:levitite` | Levitite + Pearlescent Levitite | Bloques de levitite |
| `aeronautics:levitite_breakable` | Clay, Mud, Packed Mud, Coarse Dirt | Pueden ser reemplazados por levitite al cristalizar |
| `aeronautics:levitite_catalyzer` | Campfire, Magma, Torch, Blaze Burner, Fire | Inician cristalización normal |
| `aeronautics:levitite_adjacent_catalyzer` | Netherrack, coal blocks | Aceleran cristalización cuando son adyacentes |
| `aeronautics:levitite_soul_catalyzer` | Soul Campfire, Soul Torch, Soul Fire | Inician cristalización de soul variant |
| `aeronautics:levitite_adjacent_soul_catalyzer` | Soul fire base blocks | Aceleran soul crystallization |

### Item Tags (`AeroTags.ItemTags`)

| Tag | Descripción |
|---|---|
| `aeronautics:envelope` | Todos los items de envelope |
| `aeronautics:shaftless_envelope` | Envelopes sin shaft |
| `aeronautics:levitite` | Items de levitite |
| `aeronautics:levitite_catalyzer` | Flint and Steel, Fire Charge, Torch, Campfire |
| `aeronautics:levitite_soul_catalyzer` | Soul Torch, Soul Campfire |
| `aeronautics:levitite_catalyzer_no_consume` | Catalizadores reutilizables |
| `aeronautics:burner_fire` | Coal Block (combustible para el burner) |
| `aeronautics:converts_to_cloud_skipper` | Items que convierten al music disc |

---

## 6. DataComponents

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroDataComponents.java`

### `aeronautics:levitating` → `Levitating`

Aplicado a items que "flotan" (levitite, endstone powder, music disc):

```java
public record Levitating(float dragFraction, Optional<ParticleOptions> particle) {
    static final Levitating DEFAULT            = new Levitating(0.93f, Optional.empty());
    static final Levitating END_STONE          = new Levitating(0.85f, Optional.empty());
    static final Levitating LEVITITE           = new Levitating(0.93f, Optional.of(/* green sparkle */));
    static final Levitating PEARLESCENT_LEVITITE = new Levitating(0.93f, Optional.of(/* pink sparkle */));
}
```

`dragFraction` < 1.0 → el item pierde menos velocidad vertical por tick → flota.

### `aeronautics:converter` → `Converter`

Datos de conversión (codec-based, para el sistema del music disc convertible).

---

## 7. Integración con Sable

Create Aeronautics integra con Sable de **tres maneras**:

### 7.1 Via `SableEventPlatform` (eventos de física)

Registrado en `Aeronautics.listenCommonEvents()`:

```java
SableEventPlatform.INSTANCE.onPhysicsTick(AeronauticsCommonEvents::physicsTick);
SableEventPlatform.INSTANCE.onSubLevelContainerReady(AeronauticsCommonEvents::onSubLevelContainerReady);
```

**`AeronauticsCommonEvents.physicsTick`:** llama a `BalloonMap.physicsTick(physicsSystem, timeStep)` → aplica fuerza `BALLOON_LIFT` a todos los globos activos cada sub-step.

**`AeronauticsCommonEvents.onSubLevelContainerReady`:** registra `BalloonMap.BalloonSubLevelObserver` para rastrear cuando se crean/eliminan sub-levels (globos).

### 7.2 Via Mixin en `SableCommonEvents`

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/mixin/sable_hooks/SableCommonEventsMixin.java`

```java
@Mixin(SableCommonEvents.class)
public class SableCommonEventsMixin {
    @Inject(method = "handleBlockChange", at = @At("HEAD"))
    private static void onBlockChange(ServerLevel level, LevelChunk chunk,
                                      int x, int y, int z,
                                      BlockState oldState, BlockState newState,
                                      CallbackInfo ci) {
        AeronauticsCommonEvents.onBlockModifiedEvent(level, new BlockPos(x, y, z), oldState, newState);
    }
}
```

Cuando un bloque cambia, el globo recalcula su forma/volumen. Esto permite que destruir un envelope block cambie la física del globo inmediatamente.

### 7.3 Via interfaces de Sable en BlockEntities

Todos los propellers implementan `BlockEntitySubLevelPropellerActor` + `BlockEntityPropeller`. Sable los detecta automáticamente y los invoca en cada physics sub-step cuando están en un sub-level.

### 7.4 Conversión Kinetics → Física

```
Create Kinetics (RPM)
        │
        ▼  KineticBlockEntity.getSpeed()
BasePropellerBlockEntity.getThrust()   = config_thrust × speed_factor
        │
        ▼  BlockEntitySubLevelPropellerActor.sable$physicsTick()
ForceGroups.PROPULSION.applyAndRecordPointForce(position, force × dt)
        │
        ▼  Rapier physics engine
SubLevel.velocity += impulse / mass
```

Aeronautics **no extiende** `KineticBlockEntity` a nivel de sistema físico — usa el RPM solo para calcular la magnitud de la fuerza, que luego aplica al sub-level via la API de Sable.

---

## 8. Módulo Offroad (Wheels)

**Fuente:** `offroad/common/src/main/java/dev/ryanhcode/offroad/`

El módulo Offroad es mantenido por `ryanhcode` (mismo autor de Sable) y añade:

| Block | Clase | Descripción |
|---|---|---|
| Wheel Mount | `WheelMountBlock` | Montura para ruedas, aceita un TireItem |
| Rock Cutting Wheel | `RockCuttingWheelBlock` | Rueda de corte para terreno |
| Borehead Bearing | `BoreheadBearingBlock` | Bearing para drill heads |

Items: `TireItem` (varios tipos), materiales tipo Rubber via `TireLike` component.

Entidades: `BoreheadContraptionEntity` (contraption de Create que perfora el terreno).

El Wheel Mount aplica fuerzas al sub-level de manera similar a los propellers — usando `BlockEntitySubLevelActor.sable$physicsTick()` para calcular fricción y tracción con el suelo.

---

## 9. API Pública para Addons

La API de addons de Create Aeronautics es **limitada pero funcional**.

### 9.1 `CustomSituationalMusic`

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/api/CustomSituationalMusic.java`

```java
public record CustomSituationalMusic(Music music, Condition condition) {
    @FunctionalInterface
    interface Condition {
        boolean test(ClientLevel level, LocalPlayer player);
    }
}
```

Registrar en el registry `AeroClientRegistries.CUSTOM_SITUATIONAL_MUSIC` para que tu música se reproduzca cuando la condición sea verdadera (ej. cuando el jugador está a bordo de un globo).

### 9.2 `LiftingGasType` (registry extensible)

Como se describió en §4.3, puedes registrar tu propio tipo de gas en `aeronautics:lifting_gas_type`. Un addon que quisiera añadir un gas criogénico que baja en lugar de subir implementaría `LiftingGasType` con `getLiftStrength()` negativo.

### 9.3 `CrystalPropagationContext` (registry extensible)

Registrar en `aeronautics:levitite_crystal_propagation_context` para definir nuevos comportamientos de cristalización de levitite (ej. que tu mod añada un catalizador especial que crea levitite de un color diferente).

**Fuente:** `aeronautics/common/src/main/java/dev/eriksonn/aeronautics/index/AeroLevititeBlendPropagationContexts.java`

### 9.4 Lo que NO existe como API

Los siguientes elementos fueron buscados en el código y **no encontrados** como API pública:

- ❌ Interfaz para añadir nuevos tipos de propulsor (no hay un registro de `PropulsorType`)
- ❌ Hook para añadir propiedades de buoyancy al sub-level desde fuera (se usa la API de Sable directamente)
- ❌ Clase abstracta pública para crear un "helm" o "control interface" propio

---

## 10. Caso de Estudio: Create Propulsion Simulated

**Repo:** https://github.com/KyivSec/create_propulsion_simulated  
**Commit analizado:** `5ba6aa7`  
**Mod ID:** `createpropulsionsimulated`

Este addon añade **thrusters basados en combustible y energía** para sub-levels de Sable, sin depender de los propellers de Aeronautics.

### Bloques registrados

**Fuente:** `src/main/java/dev/createpropulsionsimulated/content/thruster/`

| Block | ID | Tipo | Combustible |
|---|---|---|---|
| Thruster | `thruster` | `ThrusterBlock` | Fluido (diesel, fuel oil, etc.) |
| Ion Thruster | `ion_thruster` | `IonThrusterBlock` | Forge Energy (FE) |
| Creative Thruster | `creative_thruster` | `CreativeThrusterBlock` | Nada (modo creativo) |
| Tilt Adapter | `tilt_adapter` | `TiltAdapterBlock` | — |
| Copycat Wing (4/8/12) | `copycat_wing*` | `CopycatWingBlock` | — (visual) |

Items adicionales: `PINE_RESIN` (combustible, burn time 1200t), `TURPENTINE_BUCKET` (fluido).

### Cómo extiende Sable directamente

`ThrusterBlockEntity` implementa `BlockEntitySubLevelActor` de Sable **directamente** (sin pasar por la capa de Aeronautics):

```java
public class ThrusterBlockEntity extends SmartBlockEntity
    implements BlockEntitySubLevelActor, IHaveGoggleInformation {

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        // 1. Calcular thrust basado en fuel y throttle (redstone 0-15)
        double throttle = getThrottle();              // redstone 0.0 – 1.0
        double thrustN = currentThrust * throttle;

        // 2. Aplicar impulso via SimulatedThrustAdapter
        //    → llama a subLevel.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION)
        //    → applyAndRecordPointForce(blockCenter, direction × thrustN × dt)
        SimulatedThrustAdapter.applyImpulseAtPoint(subLevel, this, thrustN, timeStep);

        // 3. Consumir combustible proporcional al thrust
        consumeFuel(thrustN, timeStep);
    }
}
```

**Punto de aplicación:** centro del bloque + offset configurable hacia la dirección de facing.  
**ForceGroup:** `ForceGroups.PROPULSION` (el mismo que los propellers de Aeronautics).  
**Throttle:** señal de redstone (0-15) / 15.0.

### Sistema de combustible

`ThrusterFuelRegistry` — lista de fluidos aceptados como combustible. Valida vía `isFuel(FluidStack)`. Soporta fluidos de: Create: TFMG, Diesel Engines, Immersive Engineering, Mekanism, Northstar, Stellaris.

`SmartFluidTankBehaviour` (Create) — tank integrado al block entity con capacidad configurable (`ThrusterConfig.FUEL_TANK_CAPACITY_MB`).

### Cómo NO extiende Aeronautics

Importante: Create Propulsion Simulated **no depende de Create Aeronautics**. Depende solo de:
- Sable (para `BlockEntitySubLevelActor`, `QueuedForceGroup`, `ForceGroups`)
- Create (para `SmartBlockEntity`, `SmartFluidTankBehaviour`, `ScrollValueBehaviour`)

Esto demuestra que puedes crear propulsores para sub-levels de Sable sin necesitar Aeronautics como dependencia.

---

## 11. Integración con Create — Resumen Técnico

| Característica de Create | Cómo la usa Aeronautics |
|---|---|
| `KineticBlockEntity` | Herencia: todos los propellers son kinetic |
| `getSpeed()` (RPM) | Convierte a thrust y airflow con factores de config |
| `WindmillSail` | `PropellerBearingBlockEntity.findSails()` escanea la contraption |
| `MechanicalBearingBlockEntity` | Herencia del Propeller Bearing |
| `ScrollValueBehaviour` | Ajuste de gas del burner, dirección del propeller |
| `SmartBlockEntity` | Herencia del HotAirBurnerBlockEntity |
| `ContraptionEntity` | Sable las trata como `KinematicContraption` (no physics objects) |

---

## 12. Lo que No se Encontró

Los siguientes elementos fueron buscados y **no encontrados** en el código analizado:

- **Helm / Steering Wheel / Control Interface:** No hay en el módulo `aeronautics`. No se encontró una clase de helm para controlar la nave con input del jugador (WASD). Es posible que exista en el módulo Simulated (base interna del equipo) o que se planee para el futuro.

- **Nav Table:** Mencionado en commits recientes (`Fix nav table dropping air item`, commit `87a83ba`) — existe pero no fue encontrado en el módulo `aeronautics` analizado. Podría estar en el submódulo Simulated.

- **Tipos de entidades (`AeroEntityTypes`):** Referenciado en `Aeronautics.java` pero el archivo no fue leído. Incluye `GustEntity` (partícula de ráfaga de aire) y posiblemente otros.

- **Recetas custom (recipe types):** No se encontraron recipe types personalizados. Las recetas usan `ShapedRecipeBuilder`/`ShapelessRecipeBuilder` estándar de Create.

- **Config completo (`AeroConfig`):** Solo se encontró la interfaz; la implementación está en el módulo Simulated (service-loaded). Los parámetros exactos de thrust, airflow, y hot air strength son configurables pero sus defaults no fueron verificados.

---

## Commits Exactos Analizados

| Repo | Commit | URL |
|---|---|---|
| Create Aeronautics (Simulated-Project) | `87a83ba` | https://github.com/Creators-of-Aeronautics/Simulated-Project/commit/87a83ba |
| Sable (dependencia) | `60b31d2` | https://github.com/ryanhcode/sable/commit/60b31d2 |
| Create Propulsion Simulated (caso de estudio) | `5ba6aa7` | https://github.com/KyivSec/create_propulsion_simulated/commit/5ba6aa7 |
