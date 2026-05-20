package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.CombatBehavior;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.FlightMovementController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base para todas las entidades aéreas de SkyForge.
 *
 * ── Sistema de torretas (builder) ───────────────────────────────────────────
 * En el constructor de la subclase:
 *
 *   addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(0, 2, 3));
 *   addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.HE_SHELL,           new Vec3(0, 2, -3));
 *   turretInit();   // crea los AimControllers y registra los offsets
 *
 * Los nombres de hueso en el geo.json deben coincidir: "turret_0", "turret_1", ...
 * El renderer los anima automáticamente con getTurretYaw(n) / getTurretPitch(n).
 *
 * ── Sync cliente ────────────────────────────────────────────────────────────
 * Soporta hasta MAX_TURRETS = 8 torretas sincronizadas con EntityData.
 *
 * ── Posición de torreta ──────────────────────────────────────────────────────
 * getTurretOrigin() rota el localOffset por el yaw del entity automáticamente.
 * Ya no es necesario sobreescribirlo en subclases.
 */
public abstract class AbstractAerialEntity extends Mob implements CombatPlatform {

    public static final int MAX_TURRETS = 8;

    // ── EntityData: yaw y pitch por torreta (hasta 8) ─────────────────────────
    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Float>[] TURRET_YAW =
            new EntityDataAccessor[MAX_TURRETS];
    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Float>[] TURRET_PITCH =
            new EntityDataAccessor[MAX_TURRETS];

    static {
        for (int i = 0; i < MAX_TURRETS; i++) {
            TURRET_YAW[i]   = SynchedEntityData.defineId(AbstractAerialEntity.class, EntityDataSerializers.FLOAT);
            TURRET_PITCH[i] = SynchedEntityData.defineId(AbstractAerialEntity.class, EntityDataSerializers.FLOAT);
        }
    }

    // ── Estado de combate ─────────────────────────────────────────────────────
    protected final Map<Integer, AimController> turrets = new HashMap<>();
    protected FlightMovementController movement;
    protected AIStateMachine brain;
    protected TargetingSystem targeting;
    protected CombatBehavior combatBehavior;
    protected AttackController attackController;

    // ── Builder de torretas ───────────────────────────────────────────────────
    private final List<TurretDef> pendingTurrets = new ArrayList<>();
    private final Map<Integer, Vec3> turretLocalOffsets = new HashMap<>();

    protected AbstractAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        for (int i = 0; i < MAX_TURRETS; i++) {
            builder.define(TURRET_YAW[i],   0f);
            builder.define(TURRET_PITCH[i], 0f);
        }
    }

    // ── API de construcción de torretas ───────────────────────────────────────

    /**
     * Registra una torreta. El modo de apuntado se deriva del perfil
     * (canRotate → FREE_TRACKING, !canRotate → FIXED_GUN).
     */
    protected void addTurret(int id, AimProfile profile, CBCAmmoType ammo, Vec3 localOffset) {
        pendingTurrets.add(new TurretDef(id, profile, profile.defaultAimMode(), ammo, localOffset));
    }

    /**
     * Registra una torreta con modo de apuntado explícito (p.ej. LIMITED_TURN).
     */
    protected void addTurret(int id, AimProfile profile, AimController.AimMode mode, CBCAmmoType ammo, Vec3 localOffset) {
        pendingTurrets.add(new TurretDef(id, profile, mode, ammo, localOffset));
    }

    /**
     * Procesa todas las torretas registradas con addTurret() y crea los AimControllers.
     * Llamar al final de la lista de addTurret() en el constructor de la subclase.
     */
    protected void turretInit() {
        for (TurretDef def : pendingTurrets) {
            AimController ac = new AimController(this, def.id(), def.profile(), def.ammoType(), def.aimMode());
            turrets.put(def.id(), ac);
            turretLocalOffsets.put(def.id(), def.localOffset());
        }
        pendingTurrets.clear();
    }

    // ── Ángulos de torreta sincronizados al cliente ───────────────────────────

    public float getTurretYaw(int id) {
        if (id < 0 || id >= MAX_TURRETS) return 0f;
        return entityData.get(TURRET_YAW[id]);
    }

    public float getTurretPitch(int id) {
        if (id < 0 || id >= MAX_TURRETS) return 0f;
        return entityData.get(TURRET_PITCH[id]);
    }

    // ── Posición de origen de cada torreta ────────────────────────────────────

    /**
     * Calcula la posición mundial de la torreta rotando su localOffset por el yaw del entity.
     * Compatible con el sistema de huesos del modelo GeckoLib.
     */
    @Override
    public Vec3 getTurretOrigin(int id) {
        Vec3 offset = turretLocalOffsets.get(id);
        if (offset == null) return position();

        double yawRad = Math.toRadians(this.getYRot());
        double cosY   = Math.cos(yawRad);
        double sinY   = Math.sin(yawRad);

        // Rotar offset local (X=right, Y=up, Z=forward) al espacio mundial
        return position().add(
                offset.x * cosY - offset.z * sinY,
                offset.y,
                offset.x * sinY + offset.z * cosY
        );
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        this.setNoGravity(true);

        if (targeting != null) targeting.tick();
        if (brain != null)     brain.tick();

        LivingEntity target = getCombatTarget();
        if (target != null) {
            for (AimController turret : turrets.values()) {
                turret.tick(target);
            }
            syncTurretAngles();
        }

        if (attackController != null) attackController.tick();

        if (movement != null) {
            movement.tick();
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            // Turrets and static entities: zero velocity so they don't drift
            setDeltaMovement(Vec3.ZERO);
        }
    }

    /**
     * Convierte la dirección de apuntado de cada AimController a yaw/pitch
     * relativo al cuerpo del entity y los envía al cliente.
     *
     * Yaw   — rotación horizontal relativa al facing del entity (°)
     * Pitch — elevación: positivo = abajo (convención Minecraft)
     */
    private void syncTurretAngles() {
        for (Map.Entry<Integer, AimController> entry : turrets.entrySet()) {
            int id = entry.getKey();
            if (id < 0 || id >= MAX_TURRETS) continue;

            AimController turret = entry.getValue();
            Vec3 aimDir = turret.getAimDirection();
            if (aimDir.lengthSqr() < 0.001) continue;

            float worldYaw = (float) Math.toDegrees(Math.atan2(-aimDir.x, aimDir.z));
            float relYaw   = Mth.wrapDegrees(worldYaw - this.getYRot());

            double horizontal = Math.sqrt(aimDir.x * aimDir.x + aimDir.z * aimDir.z);
            float pitch       = (float) Math.toDegrees(Math.atan2(-aimDir.y, horizontal));

            entityData.set(TURRET_YAW[id],   relYaw);
            entityData.set(TURRET_PITCH[id], pitch);
        }
    }

    // ── CombatPlatform ────────────────────────────────────────────────────────

    // ── Damage / death ────────────────────────────────────────────────────────

    /** No knockback — aerial entities hold their flight path when hit. */
    @Override
    public void knockback(double strength, double x, double z) { }

    /** Preserve velocity so a hit doesn't change the flight path. */
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        Vec3 vel = getDeltaMovement();
        super.actuallyHurt(source, amount);
        if (movement != null) setDeltaMovement(vel);
    }

    /** Skip the 20-tick death animation: spawn an explosion and vanish. */
    @Override
    protected void tickDeath() {
        if (this.deathTime == 0 && !level().isClientSide()) {
            level().explode(this, getX(), getY(), getZ(),
                    getDeathExplosionRadius(), Level.ExplosionInteraction.NONE);
        }
        ++this.deathTime;
        if (this.deathTime >= 3) this.discard();
    }

    /** Override in subclasses to change the death-explosion size. */
    protected float getDeathExplosionRadius() { return 3.0f; }

    @Override public AimController getAimController(int id) { return turrets.get(id); }
    @Override public int getTurretCount()                    { return turrets.size(); }
    @Override public boolean removeWhenFarAway(double d)     { return false; }
    @Override public boolean requiresCustomPersistence()     { return true; }

    @Override public LivingEntity getCombatTarget()  { return targeting == null ? null : targeting.getTarget(); }
    @Override public Entity getCombatOwner()         { return this; }
    @Override public void spawnCombatEntity(Entity e){ level().addFreshEntity(e); }
    @Override public Vec3 getCombatPosition()        { return position(); }
    @Override public Vec3 getCombatVelocity()        { return getDeltaMovement(); }
    @Override public Level getCombatLevel()          { return level(); }
    @Override public RandomSource getCombatRandom()  { return getRandom(); }

    public Vec3 getAimDirection(int turretId) {
        AimController t = turrets.get(turretId);
        return t == null ? new Vec3(0, 0, 1) : t.getAimDirection();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public CombatBehavior getCombatBehavior()             { return combatBehavior; }
    public FlightMovementController getMovementController(){ return movement; }
    public TargetingSystem getTargetingSystem()            { return targeting; }
    public AttackController getAttackController()          { return attackController; }
}
