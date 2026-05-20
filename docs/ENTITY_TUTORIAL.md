# SkyForge — Tutorial de Entidades Aéreas

## Índice
1. [Anatomía del sistema](#1-anatomía-del-sistema)
2. [Crear una entidad aérea normal](#2-crear-una-entidad-aérea-normal)
3. [Crear una entidad torreta](#3-crear-una-entidad-torreta)
4. [Crear un Boss aéreo](#4-crear-un-boss-aéreo)
5. [Posicionar torretas en el modelo](#5-posicionar-torretas-en-el-modelo)
6. [Asignación automática de huesos](#6-asignación-automática-de-huesos)
7. [Referencia rápida de enums y presets](#7-referencia-rápida-de-enums-y-presets)

---

## 1. Anatomía del sistema

Cada entidad aérea se construye ensamblando 5 sistemas independientes:

| Campo              | Clase                       | Responsabilidad                         |
|--------------------|-----------------------------|-----------------------------------------|
| `targeting`        | `TargetingSystem`           | Detectar y mantener objetivos           |
| `brain`            | `AIStateMachine`            | Patrullar → perseguir → atacar          |
| `combatBehavior`   | `CombatBehavior`            | Posición orbital durante el combate     |
| `movement`         | `FlightMovementController`  | Física de vuelo (velocidad, giro, etc.) |
| `attackController` | `AttackController`          | Lógica de disparo de torretas           |

Las torretas se registran con el patrón builder **antes** de asignar los sistemas:

```java
addTurret(id, perfil, [modo], munición, offsetLocal);
// ...más torretas...
turretInit();  // crea los AimControllers — llamar SIEMPRE al final
```

---

## 2. Crear una entidad aérea normal

### Paso 1 — Clase Java

```java
public class MiHelicoptero extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MiHelicoptero(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas ─────────────────────────────────────────────
        // id  perfil             munición                      offset local (bloques)
        addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(0, 2, 3));
        addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.HE_SHELL,           new Vec3(0, 2, -3));
        // Para modo de apuntado explícito:
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3(-3, 1, 0));
        turretInit();

        // ── Sistemas ─────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new HelicopterCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, FlightConfig.HELICOPTER);
        this.attackController = new HelicopterAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        r.add(new AnimationController<>(this, "rotor", 0,
                state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE, 80.0);
    }
}
```

### Paso 2 — Registrar en ModEntities

```java
public static final DeferredHolder<EntityType<?>, EntityType<MiHelicoptero>> MI_HELICOPTERO =
        ENTITIES.register("mi_helicoptero", () -> EntityType.Builder
                .of(MiHelicoptero::new, MobCategory.MONSTER)
                .sized(6.0f, 3.5f)   // ancho × alto en bloques
                .build("mi_helicoptero"));
```

### Paso 3 — Registrar atributos en ModEvents

```java
event.put(ModEntities.MI_HELICOPTERO.get(), MiHelicoptero.createAttributes().build());
```

### Paso 4 — Registrar renderer en ClientEvents

```java
event.registerEntityRenderer(ModEntities.MI_HELICOPTERO.get(), MiHelicopteroRenderer::new);
```

### Paso 5 — Modelo GeckoLib

Crea `MiHelicopteroModel extends SkyforgeAerialModel<MiHelicoptero>` e implementa los 3 métodos:

```java
@Override public ResourceLocation getModelResource(MiHelicoptero e)     { return MODEL; }
@Override public ResourceLocation getTextureResource(MiHelicoptero e)   { return TEX; }
@Override public ResourceLocation getAnimationResource(MiHelicoptero e) { return ANIM; }
```

### Paso 6 — Renderer GeckoLib

```java
public class MiHelicopteroRenderer extends GeoEntityRenderer<MiHelicoptero> {
    public MiHelicopteroRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MiHelicopteroModel());
    }
}
```

---

## 3. Crear una entidad torreta

Las torretas extienden `AbstractTurretEntity` (estática, hasta 4 torretas, max 4 slots).

```java
public class MiTorreta extends AbstractTurretEntity implements GeoEntity {

    public MiTorreta(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        addTurret(0, AimProfile.TURRET, CBCAmmoType.AP_AUTOCANNON, new Vec3(0, 2, 0));
        turretInit();

        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, ...);
        this.combatBehavior   = new TurretCombatBehavior(this);
        this.movement         = null;  // la torreta no vuela
        this.attackController = new TurretAttackController(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }
}
```

El modelo extiende `SkyforgeTurretModel<MiTorreta>` (igual que `SkyforgeAerialModel` pero con MAX=4).

---

## 4. Crear un Boss aéreo

El boss extiende `AbstractBossAerialEntity` (que a su vez extiende `AbstractAerialEntity`).
Agrega: barra de vida, fases, partes rompibles.

```java
public class MiBoss extends AbstractBossAerialEntity implements GeoEntity {

    public MiBoss(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── 1. Registrar torretas (igual que siempre) ─────────────
        addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(0, 4, 6));
        addTurret(1, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3(-5, 2, 0));
        turretInit();

        // ── 2. Partes rompibles ───────────────────────────────────
        // addBossPart(id, boneName, turretIdVinculado, hp)
        // boneName debe coincidir con el hueso en el geo.json
        addBossPart("canon_mg",  "turret_0", 0, 150f);
        addBossPart("canon_ap",  "turret_1", 1, 100f);
        // Para una parte sin torreta vinculada usa -1:
        // addBossPart("blindaje_frontal", "shield_bone", -1, 500f);

        // ── 3. Inicializar boss (nombre + fases) ──────────────────
        initBoss("Mi Boss Aéreo",
                BossPhase.start(),
                // (threshold, ammoOverride, speedMult, orbitMult, mensaje)
                BossPhase.escalate(0.60f, CBCAmmoType.AP_AUTOCANNON, 1.4f, 0.7f, "§cFase 2!"),
                BossPhase.escalate(0.25f, CBCAmmoType.AP_AUTOCANNON, 1.8f, 0.5f, "§4Fase final!")
        );

        // ── 4. Sistemas ───────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new HelicopterCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, FlightConfig.GUNSHIP);
        this.attackController = new HelicopterAttackController(this);
    }
}
```

### Destruir partes desde código

Para conectar un sistema de daño por hitbox (raycast, hitpart, etc.):

```java
// Cuando un proyectil golpea la parte izquierda del boss:
if (boss instanceof MiBoss b) {
    b.damagePartDirect("canon_ap", 50f);
}
```

Al llegar a 0 HP la parte se marca como destruida, la torreta vinculada se desactiva,
y el bit correspondiente se sincroniza al cliente para que el renderer oculte el hueso.

### Leer estado de partes desde el renderer (cliente)

```java
// En BossGunshipModel.setCustomAnimations():
if (entity.isPartDestroyed("canon_ap")) {
    getBone("turret_1").ifPresent(b -> b.setHidden(true));
}
```

---

## 5. Posicionar torretas en el modelo

### La conversión clave

El `Vec3 localOffset` de `addTurret()` usa el sistema de coordenadas del **entity**:

| Eje | Dirección entity | Dirección Bedrock (geo.json) |
|-----|-----------------|------------------------------|
| X   | derecha (+)     | derecha (+) — **misma señal**  |
| Y   | arriba (+)      | arriba (+) — **misma señal**   |
| Z   | adelante (+)    | adelante (−) — **señal invertida** |

Fórmula para convertir el `pivot` del geo.json al `Vec3` de código:

```
entity_x =  pivot_x / 16
entity_y =  pivot_y / 16
entity_z = -pivot_z / 16    ← ¡Z siempre se invierte!
```

### Ejemplo práctico

Dado un hueso en el geo.json:
```json
{
    "name": "turret_0",
    "pivot": [-24, 32, -64],
    ...
}
```

El Vec3 del código sería:
```java
//           x = -24/16 = -1.5
//           y =  32/16 =  2.0
//           z = -(-64)/16 = 4.0
addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(-1.5, 2.0, 4.0));
```

### Diseñar los barriles

El barrel (cubo del cañón) debe extenderse en **-Z** desde el pivot para que apunte
hacia adelante en su posición base. Cuando el AimController rota el hueso hacia un
objetivo, el cañón girarán alrededor del pivot.

```json
{
    "name": "turret_0",
    "pivot": [0, 32, -64],
    "cubes": [
        // Base de la torreta (centrada en el pivot)
        {"origin": [-12, 24, -76], "size": [24, 16, 24], "uv": [0, 0]},
        // Barrel: se extiende desde pivot_z hacia -Z (muzzle en el extremo más negativo)
        {"origin": [-4, 28, -128], "size": [8, 6, 60], "uv": [0, 0]}
        //                          ^^^^ Z del muzzle: -128 < pivot_z (-64) ✓
    ]
}
```

---

## 6. Asignación automática de huesos

**La asignación de hueso a torreta es completamente automática.**

El renderer base `SkyforgeAerialModel.animateTurrets()` hace esto cada frame:

```java
for (int i = 0; i < MAX_TURRETS; i++) {
    getBone("turret_" + i).ifPresent(bone -> {
        bone.setRotY(Math.toRadians(-entity.getTurretYaw(i)));
        bone.setRotX(Math.toRadians( entity.getTurretPitch(i)));
    });
}
```

**Solo necesitas:**
1. Nombrar el hueso en el geo.json exactamente `turret_0`, `turret_1`, etc.
2. Llamar `addTurret(N, ...)` con el mismo `N`.
3. El sistema hace el resto: el servidor calcula el yaw/pitch relativo, lo sincroniza
   por EntityData, y el cliente rota el hueso automáticamente.

Si un hueso `turret_N` no existe en el modelo, simplemente no se anima — no hay error.
Esto permite tener torretas puramente funcionales sin representación visual (como el
bomb-bay del avión en `turret_2`).

### Resumen visual del flujo

```
Servidor:
  AimController.tick(target) → calcula dirección 3D de apuntado
  syncTurretAngles()         → convierte a yaw/pitch relativo → EntityData

Cliente:
  SkyforgeAerialModel.setCustomAnimations()
    └─ getBone("turret_N").ifPresent(b -> b.setRotY/X(...))
```

---

## 7. Referencia rápida de enums y presets

### AimProfile (perfil de torreta)

| Preset          | canRotate | aimSpeed | tolerance | Uso típico           |
|-----------------|-----------|----------|-----------|----------------------|
| `HELICOPTER`    | true      | 0.15     | 8°        | Torreta de helicóptero |
| `TURRET`        | true      | 0.08     | 6°        | Torreta estática lenta |
| `AIRPLANE`      | false     | —        | 2°        | Cañón fijo de avión    |
| `MACHINE_GUN`   | false     | —        | 6°        | MG fijo (scout)        |

### AimMode (modo de apuntado)

| Modo             | Velocidad de giro | Descripción                         |
|------------------|-------------------|-------------------------------------|
| `FREE_TRACKING`  | aimSpeed × 1.0    | Sigue al objetivo libremente        |
| `LIMITED_TURN`   | aimSpeed × 0.4    | Gira lento (costado de barco/nave)  |
| `FIXED_GUN`      | no gira           | Solo dispara si ya está alineado    |

### CBCAmmoType

| Tipo              | Descripción                            |
|-------------------|----------------------------------------|
| `MACHINE_GUN_BULLET` | Bala rápida, daño bajo, alto RoF    |
| `SOLID_SHOT`      | Proyectil sólido pesado                |
| `HE_SHELL`        | Explosivo de área                      |
| `AP_SHELL`        | Perforante estándar                    |
| `AP_SHOT`         | Perforante ligero                      |
| `AP_AUTOCANNON`   | Autocañón rápido (boss fase avanzada)  |

### FlightConfig presets

```java
FlightConfig.HELICOPTER  // VTOL compacto, 0.35 vel
FlightConfig.GUNSHIP     // VTOL pesado, 0.42 vel
FlightConfig.PLANE       // Ala fija, 0.65 vel, requiere movimiento frontal
FlightConfig.SCOUT       // Caza ágil, 0.95 vel
FlightConfig.AIRSHIP     // Dirigible lento, 0.12 vel

// Custom:
FlightConfig.builder()
    .maxSpeed(0.50f)
    .acceleration(0.04f)
    .turnRate(3.0f)
    .canRotateInPlace(true)
    .minTerrainClearance(20f)
    .build()
```

### BossPhase factory

```java
BossPhase.start()          // fase inicial (threshold=1.0, sin cambios)
BossPhase.escalate(
    float hpThreshold,     // fracción de HP en la que activa (ej: 0.65 = 65%)
    CBCAmmoType ammo,      // reemplaza el ammo de todas las torretas activas
    float speedMult,       // multiplica FlightConfig.maxSpeed
    float orbitMult,       // multiplica el radio orbital del combatBehavior
    String message         // mensaje en chat (null = sin mensaje)
)
```
