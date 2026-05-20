package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.CombatBehavior;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.movement.FlightMovementController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAerialEntity extends Mob implements CombatPlatform {

    // Ángulos del cañón 0 sincronizados al cliente para que el renderer los lea
    private static final EntityDataAccessor<Float> TURRET_YAW_0 =
            SynchedEntityData.defineId(AbstractAerialEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TURRET_PITCH_0 =
            SynchedEntityData.defineId(AbstractAerialEntity.class, EntityDataSerializers.FLOAT);

    protected final Map<Integer, AimController> turrets = new HashMap<>();
    protected FlightMovementController movement;
    protected AIStateMachine brain;
    protected TargetingSystem targeting;
    protected CombatBehavior combatBehavior;
    protected AttackController attackController;

    protected AbstractAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TURRET_YAW_0,   0f);
        builder.define(TURRET_PITCH_0, 0f);
    }

    // ── Turret angle getters (leídos por el renderer cliente) ─────────────────

    public float getTurretYaw(int id) {
        if (id == 0) return entityData.get(TURRET_YAW_0);
        return 0f;
    }

    public float getTurretPitch(int id) {
        if (id == 0) return entityData.get(TURRET_PITCH_0);
        return 0f;
    }

    // ── Default turret init ───────────────────────────────────────────────────

    protected void initTurrets() {
        turrets.put(0, new AimController(this, 0, AimProfile.HELICOPTER));
        turrets.put(1, new AimController(this, 1, AimProfile.HELICOPTER));
    }

    @Override public AimController getAimController(int id) { return turrets.get(id); }
    @Override public int getTurretCount()                    { return turrets.size(); }
    @Override public boolean removeWhenFarAway(double d)     { return false; }
    @Override public boolean requiresCustomPersistence()     { return true; }

    public Vec3 getAimDirection(int turretId) {
        AimController t = turrets.get(turretId);
        return t == null ? new Vec3(0, 0, 1) : t.getAimDirection();
    }

    @Override public LivingEntity getCombatTarget()          { return targeting == null ? null : targeting.getTarget(); }
    @Override public Entity getCombatOwner()                 { return this; }
    @Override public void spawnCombatEntity(Entity e)        { level().addFreshEntity(e); }
    @Override public Vec3 getCombatPosition()                { return position(); }
    @Override public Vec3 getCombatVelocity()                { return getDeltaMovement(); }
    @Override public Level getCombatLevel()                  { return level(); }
    @Override public RandomSource getCombatRandom()          { return getRandom(); }

    @Override
    public Vec3 getTurretOrigin(int id) {
        return switch (id) {
            case 0 -> position().add(0, 1.5,  0.5);
            case 1 -> position().add(0, 1.5, -0.5);
            default -> position();
        };
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
            // Sincronizar ángulos del cañón 0 al cliente
            syncTurretAngles();
        }

        if (attackController != null) attackController.tick();
        if (movement != null)         movement.tick();

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /**
     * Convierte la dirección de apuntado del AimController 0
     * a yaw/pitch relativo al cuerpo del entity y los envía al cliente.
     *
     * Yaw   — rotación horizontal relativa al facing del entity (°)
     * Pitch — elevación: positivo = abajo, negativo = arriba (convenio Minecraft)
     */
    private void syncTurretAngles() {
        AimController turret0 = turrets.get(0);
        if (turret0 == null) return;

        Vec3 aimDir = turret0.getAimDirection();
        if (aimDir.lengthSqr() < 0.001) return;

        // Yaw mundial del vector de apuntado
        float worldYaw   = (float) Math.toDegrees(Math.atan2(-aimDir.x, aimDir.z));
        float relYaw     = Mth.wrapDegrees(worldYaw - this.getYRot());

        // Pitch: positivo = abajo en Minecraft
        double horizontal = Math.sqrt(aimDir.x * aimDir.x + aimDir.z * aimDir.z);
        float pitch       = (float) Math.toDegrees(Math.atan2(-aimDir.y, horizontal));

        entityData.set(TURRET_YAW_0,   relYaw);
        entityData.set(TURRET_PITCH_0, pitch);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public CombatBehavior getCombatBehavior()        { return combatBehavior; }
    public FlightMovementController getMovementController() { return movement; }
    public TargetingSystem getTargetingSystem()      { return targeting; }
    public AttackController getAttackController()    { return attackController; }
}
