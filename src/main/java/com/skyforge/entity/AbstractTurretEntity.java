package com.skyforge.entity;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base para torretas estáticas. Mismo sistema builder que AbstractAerialEntity.
 *
 * Ejemplo en subclase:
 *   addTurret(0, AimProfile.TURRET, CBCAmmoType.SOLID_SHOT, new Vec3(0, 1.5, 0));
 *   addTurret(1, AimProfile.TURRET, CBCAmmoType.HE_SHELL,   new Vec3(0, 1.5, 0));
 *   turretInit();
 *
 * Soporta hasta MAX_TURRETS = 4 torretas sincronizadas.
 */
public abstract class AbstractTurretEntity extends Mob implements CombatPlatform {

    public static final int MAX_TURRETS = 4;

    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Float>[] TURRET_YAW =
            new EntityDataAccessor[MAX_TURRETS];
    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Float>[] TURRET_PITCH =
            new EntityDataAccessor[MAX_TURRETS];

    static {
        for (int i = 0; i < MAX_TURRETS; i++) {
            TURRET_YAW[i]   = SynchedEntityData.defineId(AbstractTurretEntity.class, EntityDataSerializers.FLOAT);
            TURRET_PITCH[i] = SynchedEntityData.defineId(AbstractTurretEntity.class, EntityDataSerializers.FLOAT);
        }
    }

    protected final Map<Integer, AimController> turrets = new HashMap<>();
    protected TargetingSystem targeting;
    protected AttackController attackController;

    private final List<TurretDef> pendingTurrets = new ArrayList<>();
    private final Map<Integer, Vec3> turretLocalOffsets = new HashMap<>();

    protected AbstractTurretEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        for (int i = 0; i < MAX_TURRETS; i++) {
            builder.define(TURRET_YAW[i],   0f);
            builder.define(TURRET_PITCH[i], 0f);
        }
    }

    // ── Builder de torretas ───────────────────────────────────────────────────

    protected void addTurret(int id, AimProfile profile, CBCAmmoType ammo, Vec3 localOffset) {
        pendingTurrets.add(new TurretDef(id, profile, profile.defaultAimMode(), ammo, localOffset));
    }

    protected void addTurret(int id, AimProfile profile, AimController.AimMode mode, CBCAmmoType ammo, Vec3 localOffset) {
        pendingTurrets.add(new TurretDef(id, profile, mode, ammo, localOffset));
    }

    protected void turretInit() {
        for (TurretDef def : pendingTurrets) {
            AimController ac = new AimController(this, def.id(), def.profile(), def.ammoType(), def.aimMode());
            turrets.put(def.id(), ac);
            turretLocalOffsets.put(def.id(), def.localOffset());
        }
        pendingTurrets.clear();
    }

    // ── Ángulos sincronizados al cliente ─────────────────────────────────────

    public float getTurretYaw(int id) {
        if (id < 0 || id >= MAX_TURRETS) return 0f;
        return entityData.get(TURRET_YAW[id]);
    }

    public float getTurretPitch(int id) {
        if (id < 0 || id >= MAX_TURRETS) return 0f;
        return entityData.get(TURRET_PITCH[id]);
    }

    // ── Posición de torreta ───────────────────────────────────────────────────

    @Override
    public Vec3 getTurretOrigin(int id) {
        Vec3 offset = turretLocalOffsets.get(id);
        if (offset == null) return position();

        double yawRad = Math.toRadians(this.getYRot());
        double cosY   = Math.cos(yawRad);
        double sinY   = Math.sin(yawRad);

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

        if (targeting != null) targeting.tick();

        LivingEntity target = getCombatTarget();
        if (target != null) {
            for (AimController turret : turrets.values()) {
                turret.tick(target);
            }
            syncTurretAngles();
        }

        if (attackController != null) attackController.tick();
    }

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

    @Override public Vec3 getCombatPosition()        { return position(); }
    @Override public Vec3 getCombatVelocity()        { return Vec3.ZERO; }
    @Override public Level getCombatLevel()          { return level(); }
    @Override public RandomSource getCombatRandom()  { return getRandom(); }
    @Override public AimController getAimController(int id) { return turrets.get(id); }
    @Override public int getTurretCount()            { return turrets.size(); }

    @Override
    public LivingEntity getCombatTarget() {
        return targeting == null ? null : targeting.getTarget();
    }

    @Override public Entity getCombatOwner()         { return this; }
    @Override public void spawnCombatEntity(Entity e){ level().addFreshEntity(e); }

    // ── Getters ───────────────────────────────────────────────────────────────

    public TargetingSystem getTargetingSystem()  { return targeting; }
    public AttackController getAttackController(){ return attackController; }
}
