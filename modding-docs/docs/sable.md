# Sable — Documentación de Referencia para Addons

**Repo:** https://github.com/ryanhcode/sable  
**Commit analizado:** `60b31d2` (rama `main`)  
**Loaders:** NeoForge 1.21.1, Fabric 1.21.1 (multi-módulo)  
**Licencia:** PolyForm Shield License 1.0.0  
> Lectura y creación de addons: OK. Fork competitivo o redistribución como producto: NO.

**Discord:** https://discord.gg/pnkzu2dtVA (mismo servidor que Create Aeronautics)  
**Companion opcional:** https://github.com/ryanhcode/sable-companion  
**Wiki oficial:** https://github.com/ryanhcode/sable/wiki

---

## 1. Overview — ¿Qué es un Sub-Level?

Un **sub-level** es una cuadrícula de chunks de Minecraft que existe en una posición y orientación dinámica dentro del nivel principal. Contiene bloques, block entities y entidades normales de Minecraft, pero se mueve y rota como un cuerpo rígido con física real.

### Diferencia con una Contraption de Create

| Aspecto | Create Contraption | Sable Sub-Level |
|---|---|---|
| Motor físico | Teleportación a posición fija por tick | Rapier (rigid body, sub-steps) |
| Bloques | Movidos como "datos" a entidad | Viven en chunks reales (LevelPlot) |
| Colisiones | AABB aproximada | Voxel colliders por bloque |
| Entidades | Pasajeros simples | Física real (sticking, collision callbacks) |
| Orientación | Solo eje horizontal | 6 DOF completos |

### Ciclo de vida

```
Bloques en ServerLevel
        │
        ▼  SubLevelAssemblyHelper.assembleBlocks() o /sable assemble ...
   ServerSubLevel (creado en SubLevelContainer)
        │   ← sable$tick() cada game tick
        │   ← sable$physicsTick() cada physics sub-step
        │   ← fuerzas aplicadas via QueuedForceGroup
        │
        ▼  markRemoved() o /sable remove
   Bloques regresados al ServerLevel
```

- **Creación:** vía `SubLevelAssemblyHelper.assembleBlocks()` (API) o comandos `/sable assemble`.
- **Tick físico:** múltiples sub-steps por game tick (configurable). Las fuerzas se acumulan por sub-step.
- **Destrucción:** `ServerSubLevel.markRemoved()` → `SubLevelContainer.removeSubLevel()` → bloques devueltos al nivel.
- **Splitting:** Sable puede dividir automáticamente un sub-level en partes si detecta separación (heatmap configurable).

---

## 2. Estructura de Paquetes

```
dev.ryanhcode.sable/
├── Sable.java                      # Punto de entrada, constante HELPER (ActiveSableCompanion)
├── SableConfig.java                # Configuración server-side
├── SableClientConfig.java          # Configuración client-side
├── SableCommonEvents.java          # Manejo de cambios de bloque y sincronización
├── ActiveSableCompanion.java       # API principal para terceros
├── api/                            # ← PAQUETE PÚBLICO PRINCIPAL
│   ├── SubLevelHelper.java         # Transformaciones de entidades/coordenadas
│   ├── SubLevelAssemblyHelper.java # Ensamblado/desarmado de sub-levels
│   ├── block/                      # Interfaces para bloques y block entities
│   │   ├── BlockEntitySubLevelActor.java
│   │   ├── BlockEntitySubLevelReactionWheel.java
│   │   ├── BlockSubLevelAssemblyListener.java
│   │   ├── BlockSubLevelCollisionShape.java
│   │   ├── BlockSubLevelCustomCenterOfMass.java
│   │   ├── BlockSubLevelDynamicCollider.java
│   │   ├── BlockSubLevelLiftProvider.java
│   │   ├── BlockWithSubLevelCollisionCallback.java
│   │   └── propeller/
│   │       ├── BlockEntityPropeller.java
│   │       └── BlockEntitySubLevelPropellerActor.java
│   ├── command/                    # Helpers para comandos con sub-levels
│   │   ├── SableCommandHelper.java
│   │   └── SubLevelArgumentType.java
│   ├── entity/
│   │   └── EntitySubLevelUtil.java
│   ├── event/                      # Interfaces de eventos (platform-agnostic)
│   │   ├── SablePrePhysicsTickEvent.java
│   │   ├── SablePostPhysicsTickEvent.java
│   │   └── SableSubLevelContainerReadyEvent.java
│   ├── math/
│   │   ├── LevelReusedVectors.java
│   │   └── OrientedBoundingBox3d.java
│   ├── particle/
│   │   └── ParticleSubLevelKickable.java
│   ├── physics/
│   │   ├── PhysicsPipeline.java    # Interfaz del motor de física
│   │   ├── PhysicsPipelineBody.java
│   │   ├── callback/
│   │   │   └── BlockSubLevelCollisionCallback.java
│   │   ├── collider/
│   │   ├── constraint/             # Tipos de restricciones físicas
│   │   │   ├── ConstraintJointAxis.java
│   │   │   ├── PhysicsConstraintConfiguration.java
│   │   │   ├── PhysicsConstraintHandle.java
│   │   │   ├── fixed/   (FixedConstraintConfiguration + Handle)
│   │   │   ├── free/    (FreeConstraintConfiguration + Handle)
│   │   │   ├── generic/ (GenericConstraintConfiguration + Handle)
│   │   │   └── rotary/  (RotaryConstraintConfiguration + Handle)
│   │   ├── force/
│   │   │   ├── ForceGroup.java
│   │   │   ├── ForceGroups.java    # GRAVITY, DRAG, LEVITATION, BALLOON_LIFT, PROPULSION, LIFT, MAGNETIC_FORCE
│   │   │   ├── ForceTotal.java
│   │   │   └── QueuedForceGroup.java
│   │   ├── handle/
│   │   │   └── RigidBodyHandle.java
│   │   ├── mass/
│   │   │   ├── MassData.java
│   │   │   ├── MassTracker.java
│   │   │   └── MergedMassTracker.java
│   │   └── object/
│   │       ├── ArbitraryPhysicsObject.java
│   │       ├── box/  (BoxPhysicsObject, BoxHandle)
│   │       └── rope/ (RopePhysicsObject, RopeHandle)
│   ├── schematic/
│   │   └── SubLevelSchematicSerializationContext.java
│   └── sublevel/
│       ├── ClientSubLevelContainer.java
│       ├── KinematicContraption.java   # Interfaz para contraptions kinéticas (Create)
│       ├── ServerSubLevelContainer.java
│       ├── SubLevelContainer.java      # Holder de todos los sub-levels en un Level
│       ├── SubLevelObserver.java
│       ├── SubLevelTicketLoadingSystem.java
│       ├── SubLevelTrackingPlugin.java
│       └── ticket/
├── command/                        # Implementaciones de comandos /sable
├── sublevel/
│   ├── SubLevel.java               # Base abstracta
│   ├── ServerSubLevel.java         # Server-side con física
│   └── ClientSubLevel.java         # Client-side con rendering
├── physics/                        # Implementación interna (Rapier via JNI)
├── network/                        # TCP + UDP pipeline
├── index/
│   ├── SableTags.java
│   └── SableAttributes.java
├── platform/                       # Abstracción NeoForge/Fabric
├── mixin/                          # Mixins internos (no tocar)
└── mixinterface/                   # Interfaces inyectadas por mixin
```

> **Nota:** Los paquetes `mixin/`, `mixinterface/`, `physics/impl/`, `sublevel/plot/`, `sublevel/storage/` son internos. La API pública está en `api/`, `sublevel/SubLevel.java`, `sublevel/ServerSubLevel.java`, `index/SableTags.java`, y `ActiveSableCompanion.java` (accesible vía `Sable.HELPER`).

---

## 3. API Pública — Clases Principales

### `Sable.HELPER` — `ActiveSableCompanion`

El punto de entrada estático principal. Accede a sub-levels, transforma coordenadas y obtiene velocidades.

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/ActiveSableCompanion.java`

```java
// Consultar qué sub-level contiene una posición/entidad
@Nullable SubLevel getContaining(Level level, Vec3i pos)
@Nullable SubLevel getContaining(Level level, Position pos)
@Nullable SubLevel getContaining(Level level, Vector3dc pos)
@Nullable SubLevel getContaining(Entity entity)
@Nullable SubLevel getContaining(BlockEntity blockEntity)
Iterable<SubLevel> getAllIntersecting(Level level, BoundingBox3dc bounds)

// Client-side
@Nullable ClientSubLevel getContainingClient(ChunkPos pos)
@Nullable ClientSubLevel getContainingClient(Position pos)
@Nullable ClientSubLevel getContainingClient(Vector3dc pos)

// Transformar coordenadas (world → sub-level local y viceversa)
Vector3d projectOutOfSubLevel(Level level, Vector3dc pos, Vector3d dest)
Vec3 projectOutOfSubLevel(Level level, Vec3 pos)

// Velocidad en un punto del mundo (incluyendo sub-levels)
Vector3d getVelocity(Level level, Vector3dc pos, Vector3d dest)
Vector3d getVelocity(Level level, SubLevelAccess subLevel, Vector3dc pos, Vector3d dest)
Vec3 getVelocity(Level level, Vec3 pos)
Vector3d getVelocityRelativeToAir(Level level, Vector3dc pos, Vector3d dest)

// Entidades: tracking
@Nullable SubLevel getTrackingSubLevel(Entity entity)
@Nullable SubLevel getLastTrackingSubLevel(Entity entity)
@Nullable SubLevel getTrackingOrVehicleSubLevel(Entity entity)
@Nullable SubLevel getVehicleSubLevel(Entity entity)

// Interpolación client-side
@NotNull Vec3 getEyePositionInterpolated(Entity entity, float partialTicks)

// Registro de wind providers (buoyancy/drag external)
void registerWindProvider(BiFunction<Vector3dc, Level, Vector3dc> function)

// Búsqueda que incluye sub-levels y nivel principal
<T, S extends SubLevelAccess> T runIncludingSubLevels(
    Level level, Position origin, boolean checkOrigin,
    @Nullable S subLevel, BiFunction<S, BlockPos, T> converter)

// Distancias teniendo en cuenta sub-levels
double distanceSquaredWithSubLevels(Level level, Vector3dc a, Vector3dc b)
double rectilinearDistanceWithSubLevels(Level level, Vector3dc a, Vector3dc b)
```

---

### `SubLevel` (base abstracta)

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/sublevel/SubLevel.java`

```java
Pose3d logicalPose()           // posición + orientación actual
Pose3dc lastPose()             // pose del tick anterior
BoundingBox3dc boundingBox()   // AABB global
UUID getUniqueId()
String getName()
void setName(String name)
Level getLevel()
LevelPlot getPlot()            // cuadrícula de chunks
boolean isRemoved()
void markRemoved()
```

---

### `ServerSubLevel`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/sublevel/ServerSubLevel.java`

```java
// Velocidades actuales
Vector3d getLatestLinearVelocity()    // [m/s]
Vector3d getLatestAngularVelocity()   // [rad/s]

// Masa e inercia
MassData getMassTracker()
MassTracker getSelfMassTracker()

// Fuerzas (el mecanismo principal para propulsión)
QueuedForceGroup getOrCreateQueuedForceGroup(ForceGroup group)

// Networking
Collection<UUID> getTrackingPlayers()
VeilPacketManager.PacketSink playerSink()

// User data (NBT arbitrario para addons)
void setUserDataTag(CompoundTag tag)
CompoundTag getUserDataTag()

// Debug
void enableIndividualQueuedForcesTracking(boolean enable)
boolean isTrackingIndividualQueuedForces()

// Splitting
void setSplitFrom(ServerSubLevel containing, Pose3d originalPose)
UUID getSplitFromSubLevel()
```

---

### `SubLevelAssemblyHelper`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/SubLevelAssemblyHelper.java`

```java
// Crear un sub-level a partir de un conjunto de bloques
static ServerSubLevel assembleBlocks(
    ServerLevel level,
    BlockPos anchor,
    Iterable<BlockPos> blocks,
    BoundingBox3ic bounds)

// Recoger bloques conectados desde un origen (flood-fill con predicate opcional)
static GatherResult gatherConnectedBlocks(
    BlockPos origin,
    ServerLevel level,
    int maxBlocks,
    @Nullable FrontierPredicate predicate)

// Tipos de retorno
record GatherResult(
    Set<BlockPos> blocks,
    int checkedBlocks,
    BoundingBox3i boundingBox,
    State state)   // SUCCESS | TOO_MANY_BLOCKS | NO_BLOCKS

@FunctionalInterface
interface FrontierPredicate {
    boolean isValidConnection(
        BlockPos originPos, BlockState originState,
        BlockPos pos, BlockState state,
        @Nullable Direction direction)
}
```

---

### `SubLevelContainer`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/sublevel/SubLevelContainer.java`

```java
// Obtener el container de un Level
static @Nullable SubLevelContainer getContainer(Level level)
static @Nullable ServerSubLevelContainer getContainer(ServerLevel level)
static @Nullable ClientSubLevelContainer getContainer(ClientLevel level)

// Consultas
@Nullable SubLevel getSubLevel(UUID uuid)
List<? extends SubLevel> getAllSubLevels()
Iterable<SubLevel> queryIntersecting(BoundingBox3dc bounds)
int getLoadedCount()

// Observadores
void addObserver(SubLevelObserver observer)
```

---

### `PhysicsPipeline`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/physics/PhysicsPipeline.java`

La interfaz del motor físico (implementada por Rapier). Los addons no instancian esto directamente, pero pueden obtenerlo desde `SubLevelPhysicsSystem` en los eventos pre/post tick.

```java
// Añadir constraintsentre dos sub-levels (o entre sub-level y mundo)
<T extends PhysicsConstraintHandle> T addConstraint(
    @Nullable ServerSubLevel sublevelA,
    @Nullable ServerSubLevel sublevelB,
    PhysicsConstraintConfiguration<T> configuration)

// Mover un body
void teleport(PhysicsPipelineBody body, Vector3dc position, Quaterniondc orientation)

// Aplicar impulso
void applyImpulse(PhysicsPipelineBody body, Vector3dc position, Vector3dc force)
void applyLinearAndAngularImpulse(
    PhysicsPipelineBody body, Vector3dc force, Vector3dc torque, boolean wakeUp)

// Velocidades
Vector3d getLinearVelocity(PhysicsPipelineBody body, Vector3d dest)   // [m/s]
Vector3d getAngularVelocity(PhysicsPipelineBody body, Vector3d dest)  // [rad/s]

// Añadir físicos arbitrarios
BoxHandle addBox(BoxPhysicsObject boxPhysicsObject)
RopeHandle addRope(RopePhysicsObject rope)
```

---

## 4. Propiedades Físicas de Bloques

Sable asigna propiedades físicas a los bloques a través de **JSONs de datapack**. No hay anotaciones de código ni DataComponents especiales en el bloque mismo — todo es data-driven.

### Propiedades disponibles

| Propiedad | Tipo | Default | Descripción |
|---|---|---|---|
| `sable:mass` | `double` | `1.0` | Masa en kpg |
| `sable:inertia` | `[double, double, double]` | `[1/6, 1/6, 1/6]` | Multiplicador de inercia por eje (×masa) |
| `sable:volume` | `double` | `1.0` | Volumen en m³ (buoyancy) |
| `sable:restitution` | `double` | `0.0` | Elasticidad (bounce) 0-1 |
| `sable:friction` | `double` | `1.0` | Multiplicador de fricción |
| `sable:fragile` | `boolean` | `false` | Rompe al impacto |
| `sable:floating_material` | `ResourceLocation` | `null` | Material flotante (buoyancy especial) |
| `sable:floating_scale` | `double` | `1.0` | Escala del material flotante |

### Formato JSON

```json
// data/mymod/physics_block_properties/my_block.json
{
    "selector": "mymod:my_block",
    "priority": 1000,
    "properties": {
        "sable:mass": 2.0,
        "sable:friction": 0.5
    },
    "overrides": {
        "powered=true": {
            "sable:mass": 1.0
        }
    }
}
```

- `selector`: ID de bloque (`mymod:block`) o tag (`#mymod:tag`).
- `priority`: orden de aplicación, ascendente (default 1000). Útil para sobreescribir defaults de Sable.
- `overrides`: condiciones de BlockState → propiedades alternativas.

**Fuente del schema:** `common/src/main/java/dev/ryanhcode/sable/physics/config/block_properties/PhysicsBlockPropertiesDefinition.java`  
**Wiki oficial:** https://github.com/ryanhcode/sable/wiki/Block-Physics-Properties

### Tags de bloques predefinidos

Sable incluye su propio datapack con definiciones para blocks vanilla. Para tu mod, la forma más simple es añadir tus bloques a estos tags en lugar de crear JSONs propios:

| Tag | Efecto |
|---|---|
| `#sable:normal` | mass=1.0 (default) |
| `#sable:light` | mass=0.5 |
| `#sable:super_light` | mass=0.25 |
| `#sable:heavy` | mass=2.0 |
| `#sable:super_heavy` | mass=4.0 |
| `#sable:weightless` | mass=0.0 |
| `#sable:half_volume` | volume=0.5 |
| `#sable:quarter_volume` | volume=0.25 |
| `#sable:slippery` | friction=0.0 |
| `#sable:bouncy` | restitution=0.5 |
| `#sable:fragile` | fragile=true |

**Fuente:** `common/src/main/resources/data/sable/physics_block_properties/*.json`  
**Fuente de tags:** `common/src/main/resources/data/sable/tags/block/`

### Floating materials

Para bloques con buoyancy especial (tipo "end stone flotante"), se registran floating materials:

```json
// data/mymod/floating_materials/my_material.json
{
    "prevent_self_lift": true,
    "scale_friction_with_gravity": true,
    "lift_strength": 2,
    "transition_speed": 2,
    "slow_vertical_friction": 0.4,
    "fast_vertical_friction": 0.02,
    "slow_horizontal_friction": 0.3,
    "fast_horizontal_friction": 0.01
}
```

Y se referencia desde el block property JSON con `"sable:floating_material": "mymod:my_material"`.  
**Ejemplo de Sable:** `common/src/main/resources/data/sable/floating_materials/end_stone.json`

---

## 5. Interfaces de Bloques y Block Entities

Implementar estas interfaces en tus bloques/block-entities es la forma principal de interactuar con sub-levels desde tu addon.

### `BlockEntitySubLevelActor`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/BlockEntitySubLevelActor.java`

Implementar en un `BlockEntity` para recibir ticks cuando esté en un sub-level.

```java
public interface BlockEntitySubLevelActor {
    // Tick de juego (una vez por game tick)
    default void sable$tick(ServerSubLevel subLevel) {}

    // Tick físico (puede ocurrir múltiples veces por game tick)
    default void sable$physicsTick(
        ServerSubLevel subLevel,
        RigidBodyHandle handle,
        double timeStep) {}

    // Sub-levels de los que depende para cargarse junto
    @Nullable
    default Iterable<SubLevel> sable$getLoadingDependencies() {
        return sable$getConnectionDependencies();
    }

    // Sub-levels con los que está "conectado" lógicamente
    @Nullable
    default Iterable<SubLevel> sable$getConnectionDependencies() {
        return null;
    }
}
```

### `BlockEntitySubLevelPropellerActor`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/propeller/BlockEntitySubLevelPropellerActor.java`

Extiende `BlockEntitySubLevelActor`. Implementar junto con `BlockEntityPropeller` para crear un propulsor que aplica fuerza al sub-level en `physicsTick`.

```java
public interface BlockEntitySubLevelPropellerActor extends BlockEntitySubLevelActor {
    BlockEntityPropeller getPropeller();

    // Implementación default: aplica la fuerza al ForceGroups.PROPULSION
    @Override
    default void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        // Calcula thrust y llama a applyForces(...)
    }

    default void applyForces(ServerSubLevel subLevel, Vec3 thrustDirection, double timeStep) {
        // Aplica impulso en el grupo PROPULSION
        QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION.get());
        forceGroup.applyAndRecordPointForce(position, thrustVector);
    }
}
```

### `BlockEntityPropeller`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/propeller/BlockEntityPropeller.java`

```java
public interface BlockEntityPropeller {
    Direction getBlockDirection();
    double getAirflow();          // [m/s]
    double getThrust();           // [pN]
    boolean isActive();

    // Calculados automáticamente:
    default double getScaledThrust()          // thrust × airflow_scaling × air_pressure
    default double getAirflowScaling()        // escala según velocidad actual del sub-level
    default double getCurrentAirPressure()    // presión de aire de la dimensión

    Level getLevel();
    BlockPos getBlockPos();
}
```

### `BlockEntitySubLevelReactionWheel`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/BlockEntitySubLevelReactionWheel.java`

Implementar para simular efecto giroscópico desde un block entity (ej. flywheel rotando).

```java
public interface BlockEntitySubLevelReactionWheel {
    void sable$getAngularVelocity(Vector3d dest);  // [rad/s] del spinning body
}
```

### `BlockSubLevelLiftProvider`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/BlockSubLevelLiftProvider.java`

Para bloques que generan sustentación aerodinámica (tipo vela/ala).

```java
public interface BlockSubLevelLiftProvider {
    Direction sable$getNormal(BlockState state);

    default float sable$getParallelDragScalar()      // 0.75f
    default float sable$getDirectionlessDragScalar() // 0.0689...
    default float sable$getLiftScalar()              // 0.475f

    // Contribuye lift y drag al sub-level
    default void sable$contributeLiftAndDrag(
        LiftProviderContext ctx,
        ServerSubLevel subLevel,
        Pose3d localPose,
        double timeStep,
        Vector3dc linearVelocity,
        Vector3dc angularVelocity,
        Vector3d linearImpulse,
        Vector3d angularImpulse,
        @Nullable LiftProviderGroup group)

    record LiftProviderContext(BlockPos pos, BlockState state, Direction dir)
    record LiftProviderGroup(Set<BlockPos> positions)
}
```

### `BlockSubLevelAssemblyListener`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/block/BlockSubLevelAssemblyListener.java`

Llamado antes y después de que el bloque se mueva durante ensamblado/desensamblado.

```java
public interface BlockSubLevelAssemblyListener {
    default void beforeMove(ServerLevel level, ServerLevel resultingLevel,
                           BlockState state, BlockPos oldPos, BlockPos newPos) {}
    default void afterMove(ServerLevel level, ServerLevel resultingLevel,
                          BlockState state, BlockPos oldPos, BlockPos newPos) {}
}
```

### `BlockSubLevelCustomCenterOfMass`

```java
public interface BlockSubLevelCustomCenterOfMass {
    Vector3d sable$getCenterOfMass();  // posición relativa al bloque
}
```

### `BlockWithSubLevelCollisionCallback`

```java
public interface BlockWithSubLevelCollisionCallback {
    void sable$collided(ServerSubLevel subLevel, Collider collider);
}
```

---

## 6. Eventos y Hooks (NeoForge)

Todos los eventos se lanzan en el **NeoForge event bus** principal.

**Fuente:** `neoforge/src/main/java/dev/ryanhcode/sable/neoforge/event/`

### `ForgeSablePrePhysicsTickEvent`

```java
public class ForgeSablePrePhysicsTickEvent extends Event {
    SubLevelPhysicsSystem getPhysicsSystem()  // acceso a todos los sub-levels del nivel
    double getTimeStep()                       // [s], típicamente 1.0/20.0/substeps
}
```

Fired antes de cada sub-step de física. Usar aquí para aplicar fuerzas externas.  
> Hay múltiples physics ticks por game tick. La lógica de fuerzas debe ocurrir aquí, no en el game tick.

### `ForgeSablePostPhysicsTickEvent`

```java
public class ForgeSablePostPhysicsTickEvent extends Event {
    SubLevelPhysicsSystem getPhysicsSystem()
    double getTimeStep()
}
```

Fired después de resolver el sub-step.

### `ForgeSableSubLevelContainerReadyEvent`

```java
public class ForgeSableSubLevelContainerReadyEvent extends Event {
    Level getLevel()
    SubLevelContainer getContainer()
}
```

Fired cuando Sable termina de inicializar el container de sub-levels para un Level. Aquí es el momento correcto para registrar `SubLevelObserver`s.

### Registro de eventos (NeoForge)

```java
@Mod.EventBusSubscriber(modid = "mymod")
public class MyEvents {
    @SubscribeEvent
    public static void onPrePhysics(ForgeSablePrePhysicsTickEvent event) {
        double dt = event.getTimeStep();
        // aplicar fuerzas a sub-levels...
    }

    @SubscribeEvent
    public static void onContainerReady(ForgeSableSubLevelContainerReadyEvent event) {
        event.getContainer().addObserver(new MySubLevelObserver());
    }
}
```

### Platform-agnostic (API)

Para código multiloader, los mismos eventos como interfaces funcionales en `api/event/`:

```java
@FunctionalInterface
interface SablePrePhysicsTickEvent {
    void prePhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep);
}

@FunctionalInterface
interface SablePostPhysicsTickEvent {
    void postPhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep);
}

@FunctionalInterface
interface SableSubLevelContainerReadyEvent {
    void onSubLevelContainerReady(Level level, SubLevelContainer container);
}
```

Registrar vía `SableEventPlatform.INSTANCE.onPhysicsTick(...)`.

---

## 7. Network / Packets

Sable utiliza **dos pipelines de red simultáneos**:

- **TCP:** Protocolo de Minecraft normal (reliable) — usado para datos de estado, sincronización inicial.
- **UDP:** Pipeline propio para updates de pose de sub-levels (posición/velocidad) — optimizado para alta frecuencia.

Los paquetes UDP están deshabilitados si el cliente no soporta UDP o si la config `DISABLE_UDP_PIPELINE` está activa. Un addon **no necesita interactuar** con los paquetes directamente — los datos de pose se sincronizan automáticamente.

**Fuentes relevantes:** `common/src/main/java/dev/ryanhcode/sable/network/`

---

## 8. Restricciones Físicas (Constraints)

Sable expone una API de constraints con cuatro tipos. Se crean via `PhysicsPipeline.addConstraint()`.

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/physics/constraint/`

### `RotaryConstraintConfiguration`

Junta rotaria: un grado de libertad angular (eje de rotación). Equivalente a un pin joint o axle.

```java
public record RotaryConstraintConfiguration(
    Vector3dc pos1,     // posición en world space dentro del plot del sub-level A
    Vector3dc pos2,     // posición en world space dentro del plot del sub-level B
    Vector3dc normal1,  // normal del joint en el sub-level A (eje de rotación)
    Vector3dc normal2   // normal del joint en el sub-level B
) implements PhysicsConstraintConfiguration<RotaryConstraintHandle>
```

### `FixedConstraintConfiguration`

Junta fija (welded): sin grados de libertad.

```java
// Fuente: api/physics/constraint/fixed/FixedConstraintConfiguration.java
```

### `FreeConstraintConfiguration`

Sin restricciones (equivalente a no tener junta — útil para "anclar" sin efectos físicos).

### `GenericConstraintConfiguration`

Junta genérica con axes configurables individualmente. Introducida en v1.1.0.

```java
public record GenericConstraintConfiguration(
    Vector3dc pos1,
    Vector3dc pos2,
    Quaterniondc orientation1,
    Quaterniondc orientation2,
    Set<ConstraintJointAxis> lockedAxes    // axes hard-locked por el solver
) implements PhysicsConstraintConfiguration<GenericConstraintHandle>
```

### Comando de debug

```
/sable debug joint add <subLevel1> <subLevel2> rotary <pos1> <pos2> <axis1> <axis2>
```

Crea un rotary joint interactivo entre dos sub-levels.

---

## 9. Tags de Sable (`SableTags`)

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/index/SableTags.java`

```java
// Entity type tags
TagKey<EntityType<?>> RETAIN_IN_SUB_LEVEL      // entidades que permanecen cuando se desensambla
TagKey<EntityType<?>> DESTROY_WITH_SUB_LEVEL   // entidades destruidas cuando se elimina el sub-level
TagKey<EntityType<?>> DESTROY_WHEN_LEAVING_PLOT // entidades destruidas al salir del plot

// Block tags
TagKey<Block> ALWAYS_CHUNK_RENDERING   // siempre renderizado como chunk (no instanciado)
TagKey<Block> BOUNCY                   // aplicar restitución 0.5
TagKey<Block> SILENT_ASSEMBLY_REMOVAL  // borrado sin llamar Clearable al ensamblar

// Item tags
TagKey<Item> PADDLES                   // items que funcionan como remo
```

---

## 10. Compatibilidad con Create

Sable tiene compatibilidad extensiva con Create, implementada vía mixins en el módulo NeoForge.

**Fuente:** `neoforge/src/main/java/dev/ryanhcode/sable/neoforge/mixin/compatibility/create/`

| Área | Archivos | Descripción |
|---|---|---|
| Airflow | `airflow/AirCurrentMixin`, `FanProcessingTypeMixin` | Air current de fans en sub-levels |
| Belt | `belt/Belt*Mixin` | Belts funcionales en sub-levels |
| Basin | `basin_interactions/Basin*Mixin` | Basin recipes en sub-levels |
| Harvester | `harvester_behaviour/`, `harvester_block_entity/` | Mechanical Harvester en sub-levels |
| Contraptions | `contraptions/AbstractContraptionEntityMixin` | Contraptions conviven con sub-levels |
| Blueprint | `blueprint/BlueprintEntityMixin` | Blueprints reconocen sub-levels |
| Blaze Burner | `blaze_burner/BlazeBurnerBlockEntityMixin` | Compatibilidad visual |
| Block Breakers | `block_breakers/BlockBreaking*Mixin` | Saw y drills en sub-levels |

### `KinematicContraption`

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/api/sublevel/KinematicContraption.java`

Interfaz que Sable mixin-inyecta en las contraptions de Create para que el motor físico las trate como cuerpos cinemáticos (colisionan pero no reciben fuerzas).

```java
public interface KinematicContraption {
    void sable$getLocalBounds(BoundingBox3i bounds)
    BlockGetter sable$blockGetter()
    MassTracker sable$getMassTracker()
    Vector3dc sable$getPosition(double partialTick)
    Quaterniond sable$getOrientation(double partialTick)
    Map<BlockPos, BlockSubLevelLiftProvider.LiftProviderContext> sable$liftProviders()
    boolean sable$shouldCollide()
    boolean sable$isValid()
}
```

---

## 11. Configuración (`SableConfig`)

**Fuente:** `common/src/main/java/dev/ryanhcode/sable/SableConfig.java`

Valores configurables en el archivo de configuración del servidor:

| Config | Tipo | Default | Descripción |
|---|---|---|---|
| `SUB_LEVEL_SPLITTING` | boolean | true | Habilitar splitting automático |
| `SUB_LEVEL_SPLITTING_HEATMAP_STEPS_PER_TICK` | int | — | Velocidad del análisis de splitting |
| `SUB_LEVEL_TRACKING_RANGE` | double | 320.0 | Rango de tracking de jugadores |
| `SUB_LEVEL_REMOVE_MIN` | double | -10000.0 | Y mínimo antes de auto-eliminar |
| `SUB_LEVEL_REMOVE_MAX` | double | 100000.0 | Y máximo antes de auto-eliminar |
| `VELOCITY_RETAINED_ON_LOAD` | double | 0.9 | Fracción de velocidad al cargar |
| `SUB_LEVEL_PUNCH_STRENGTH_MULTIPLIER` | double | 2.1 | Multiplicador de golpe |
| `DISABLE_UDP_PIPELINE` | boolean | false | Forzar TCP para todo |
| `ATTEMPT_UDP_NETWORKING` | boolean | true | Intentar UDP |

---

## 12. Comandos `/sable`

Todos requieren nivel de permiso 2 (operador).

```
/sable paused [true|false]              # pausar/reanudar física
/sable engage_gizmo                     # activar editor gizmo (pausa física)
/sable forceload add <sub_level>        # force-load un sub-level
/sable forceload remove <sub_level>     # quitar force-load
/sable info <sub_level>                 # información del sub-level

/sable name set <targets> <name>        # nombre del sub-level
/sable name clear <targets>
/sable name get <target>

/sable teleport <targets> <dest>        # teleportar
/sable teleport <targets> <dest> <angle>

/sable remove <targets>                 # eliminar sub-level

/sable assemble area <from> <to>
/sable assemble connected [origin] [capacity]    # default 256k bloques
/sable assemble sphere <radius> [origin]
/sable assemble cube <range> [origin]

/sable assemble shatter sub_level <sub_level>    # un bloque → un sub-level
/sable assemble shatter area <from> <to>
/sable assemble shatter connected [origin] [capacity]
/sable assemble shatter sphere <radius> [origin]
/sable assemble shatter cube <range> [origin]

/sable debug joint add <sl1> <sl2> rotary <pos1> <pos2> <axis1> <axis2>
```

---

## 13. Cómo Hacer un Addon de Sable

### Dependencia

**Fuente:** https://github.com/ryanhcode/sable/wiki/Home

```groovy
// build.gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
                url = "https://maven.ryanhcode.dev/releases"
                name = "RyanHCode Maven"
            }
        }
        filter {
            includeGroup("dev.ryanhcode.sable")
            includeGroup("dev.ryanhcode.sable-companion")
        }
    }
}

dependencies {
    // NeoForge:
    api("dev.ryanhcode.sable:sable-common-${minecraft_version}:${sable_version}")
    // Fabric:
    // modApi("dev.ryanhcode.sable:sable-fabric-${minecraft_version}:${sable_version}")
}
```

### Caso mínimo: bloque con propiedades físicas

El camino más simple y recomendado: añadir el bloque a un tag existente de Sable.

```json
// data/mymod/tags/blocks/heavy_stuff.json
{
  "replace": false,
  "values": ["mymod:my_heavy_block"]
}
```

```json
// Referencia el tag de Sable:
// data/sable/tags/blocks/heavy.json  ← esto no se puede hacer así
```

Corrección: debes usar tu propio JSON de `physics_block_properties`:

```json
// data/mymod/physics_block_properties/my_heavy_block.json
{
    "selector": "mymod:my_heavy_block",
    "properties": {
        "sable:mass": 2.0,
        "sable:friction": 0.8
    }
}
```

O añadir el bloque al tag de Sable en tu datapack:

```json
// data/sable/tags/blocks/heavy.json (en tu datapack, overrides)
{
  "replace": false,
  "values": ["mymod:my_heavy_block"]
}
```

### Caso avanzado: BlockEntity propulsor

Implementar un block entity que aplica fuerza al sub-level cuando recibe RPM kinética de Create.

```java
// Ejemplo compilable (esquema, no incluye imports completos)
public class MyThrusterBlockEntity
    extends KineticBlockEntity              // hereda RPM de Create
    implements BlockEntitySubLevelPropellerActor, BlockEntityPropeller {

    // Config values
    private static final double THRUST_PER_RPM = 0.5;   // pN por RPM
    private static final double AIRFLOW_PER_RPM = 0.1;  // m/s por RPM

    public MyThrusterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // --- BlockEntityPropeller ---

    @Override
    public Direction getBlockDirection() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public double getThrust() {
        // getSpeed() viene de KineticBlockEntity (RPM)
        return THRUST_PER_RPM * Math.abs(getSpeed());
    }

    @Override
    public double getAirflow() {
        return AIRFLOW_PER_RPM * Math.abs(getSpeed());
    }

    @Override
    public boolean isActive() {
        return Math.abs(getSpeed()) > 0.1f;
    }

    @Override
    public Level getLevel() { return level; }

    @Override
    public BlockPos getBlockPos() { return worldPosition; }

    // --- BlockEntitySubLevelPropellerActor ---

    @Override
    public BlockEntityPropeller getPropeller() { return this; }

    // sable$physicsTick() viene implementado por default en la interfaz
    // y llama applyForces(subLevel, thrustDirection, timeStep)
}
```

Registrar el BlockEntity normalmente con Registrate/NeoForge. Sable detecta automáticamente que implementa `BlockEntitySubLevelActor` y lo invoca cuando está en un sub-level.

### Caso avanzado: escuchar physics tick para fuerza externa

```java
@Mod.EventBusSubscriber(modid = "mymod")
public class MyPhysicsHandler {
    @SubscribeEvent
    public static void onPrePhysics(ForgeSablePrePhysicsTickEvent event) {
        SubLevelPhysicsSystem sys = event.getPhysicsSystem();
        double dt = event.getTimeStep();

        // Iterar sub-levels del level
        SubLevelContainer container = SubLevelContainer.getContainer(/* ServerLevel */);
        if (container == null) return;

        for (SubLevel sl : container.getAllSubLevels()) {
            if (!(sl instanceof ServerSubLevel ssl)) continue;

            // Aplicar fuerza magnética personalizada hacia arriba
            QueuedForceGroup mag = ssl.getOrCreateQueuedForceGroup(
                ForceGroups.MAGNETIC_FORCE.get());

            // punto: centro de masa
            Vector3d com = ssl.getMassTracker().getCenterOfMass(new Vector3d());
            // fuerza: 10N hacia arriba × timestep
            mag.applyAndRecordPointForce(com, new Vector3d(0, 10.0 * dt, 0));
        }
    }
}
```

---

## 14. Evaluación de la API

La API pública de Sable es **sustancial y bien documentada** comparada con otras library mods de física de Minecraft:

✅ **Lo que SÍ existe:**
- JSON datapack para propiedades físicas (sin código)
- Interfaces de block/block-entity para propulsión, lift, colisiones
- Eventos NeoForge pre/post physics tick
- API de constraints (Fixed, Free, Generic, Rotary)
- `SubLevelAssemblyHelper` para crear sub-levels programáticamente
- `ActiveSableCompanion` (acceso vía `Sable.HELPER`) para consultar sub-levels
- `ForceGroups` registrado en registry (extensible: puedes registrar tu propio grupo)

⚠️ **Limitaciones a tener en cuenta:**
- Sable es "incredibly intrusive" (README propio): extensas mixins pueden romper compatibilidad con otros mods
- El pipeline UDP es interno y no expone API
- Los internos de `SubLevelPhysicsSystem` y `RapierPhysicsPipeline` no son API estable
- No hay anotación formal `@ApiStatus.PublicAPI` — la convención es que el paquete `api/` es público
- La API de constraint solo soporta 4 tipos (no hay slider, spring, o motor por ahora)

---

## Commits Exactos Analizados

| Repo | Commit | URL |
|---|---|---|
| Sable | `60b31d2` | https://github.com/ryanhcode/sable/commit/60b31d2 |
| Create Aeronautics | `87a83ba` | https://github.com/Creators-of-Aeronautics/Simulated-Project/commit/87a83ba |
| Create Propulsion Simulated | `5ba6aa7` | https://github.com/KyivSec/create_propulsion_simulated/commit/5ba6aa7 |
