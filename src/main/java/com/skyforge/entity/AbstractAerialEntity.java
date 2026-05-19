package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.CombatBehavior;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.movement.FlightMovementController;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAerialEntity extends Mob implements CombatPlatform {
    float bodyYaw, prevBodyYaw;
    float turretYaw, prevTurretYaw;
    float turretPitch, prevTurretPitch;

    protected final Map<Integer, AimController> turrets = new HashMap<>();
    protected FlightMovementController movement;
    protected AIStateMachine brain;
    protected TargetingSystem targeting;
    protected CombatBehavior combatBehavior;
    protected AttackController attackController;

    protected AbstractAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        initTurrets();
    }
    protected void initTurrets() {

        turrets.put(0, new AimController(this, 0, AimProfile.HELICOPTER));
        turrets.put(1, new AimController(this, 1, AimProfile.HELICOPTER));

    }
    @Override
    public AimController getAimController(int id) {
        return turrets.get(id);
    }
    @Override
    public int getTurretCount() {
        return turrets.size();
    }
    @Override
    public boolean removeWhenFarAway(
            double distance
    ) {
        return false;
    }
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    public Vec3 getAimDirection(int turretId) {
        AimController turret = turrets.get(turretId);

        if (turret == null)
            return new Vec3(0, 0, 1);

        return turret.getAimDirection();
    }

    @Override
    public LivingEntity getCombatTarget() {

        return this.getTargetingSystem()
                .getTarget();
    }

    @Override
    public Entity getCombatOwner() {

        return this;
    }

    @Override
    public void spawnCombatEntity(Entity entity) {

        this.level()
                .addFreshEntity(entity);
    }

    @Override
    public Vec3 getCombatPosition() {

        return this.position();
    }

    @Override
    public Vec3 getCombatVelocity() {

        return this.getDeltaMovement();
    }

    @Override
    public Vec3 getTurretOrigin(int id) {

        return switch (id) {
            case 0 -> position().add(0, 1.5, 0.5);
            case 1 -> position().add(0, 1.5, -0.5);
            default -> position();
        };
    }

    @Override
    public Level getCombatLevel() {

        return this.level();
    }

    @Override
    public RandomSource getCombatRandom() {

        return this.getRandom();
    }

    @Override
    public void tick() {

        super.tick();

        if (level().isClientSide())
            return;
        this.setNoGravity(true);
        if (targeting != null)
            targeting.tick();

        if (brain != null)
            brain.tick();

        LivingEntity target = getCombatTarget();

        if (target != null) {

            for (AimController turret : turrets.values()) {
                turret.tick(target);
            }
        }

        if (attackController != null)
            attackController.tick();

        if (movement != null)
            movement.tick();

        this.move(MoverType.SELF, this.getDeltaMovement());
    }
    public CombatBehavior getCombatBehavior() {

        return combatBehavior;
    }

    public FlightMovementController getMovementController() {
        return movement;
    }
    public TargetingSystem getTargetingSystem() {
        return targeting;
    }
    public AttackController getAttackController() {

        return attackController;
    }
}
