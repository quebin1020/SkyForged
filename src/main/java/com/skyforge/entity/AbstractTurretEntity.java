package com.skyforge.entity;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTurretEntity extends Mob implements CombatPlatform {

    float bodyYaw, prevBodyYaw;
    float turretYaw, prevTurretYaw;
    float turretPitch, prevTurretPitch;
    protected final Map<Integer, AimController> turrets = new HashMap<>();

    protected Vec3 aimDirection =
            new Vec3(0, 0, 1);

    protected TargetingSystem targeting;

    protected AttackController attackController;

    protected AbstractTurretEntity(
            EntityType<? extends Mob> type,
            Level level
    ) {

        super(type, level);

        initTurrets();
    }

    @Override
    public void tick() {

        super.tick();

        if (level().isClientSide())
            return;

        if (targeting != null)
            targeting.tick();

        LivingEntity target = getCombatTarget();

        if (target != null) {

            for (AimController turret : turrets.values()) {
                turret.tick(target);
            }
        }

        if (attackController != null)
            attackController.tick();
    }

    @Override
    public Vec3 getCombatPosition() {

        return this.position();
    }

    @Override
    public Vec3 getCombatVelocity() {

        return Vec3.ZERO;
    }
    protected void initTurrets() {

        turrets.put(0, new AimController(this, 0, AimProfile.HELICOPTER));
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
    public LivingEntity getCombatTarget() {

        if(targeting == null)
            return null;

        return targeting.getTarget();
    }

    @Override
    public Entity getCombatOwner() {

        return this;
    }

    @Override
    public void spawnCombatEntity(
            Entity entity
    ) {

        this.level()
                .addFreshEntity(entity);
    }
    public AimController getAimController(int id) {
        return turrets.get(id);
    }

    public TargetingSystem getTargetingSystem() {

        return targeting;
    }

    public AttackController getAttackController() {

        return attackController;
    }
    @Override
    public int getTurretCount() {
        return turrets.size();
    }
}