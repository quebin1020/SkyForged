package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.combat.AimController;
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

public abstract class AbstractAerialEntity extends Mob implements CombatPlatform {

    protected FlightMovementController movement;
    protected AIStateMachine brain;
    protected TargetingSystem targeting;
    protected CombatBehavior combatBehavior;
    protected AttackController attackController;
    protected AimController aimController;

    protected AbstractAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.aimController = new AimController(this);
    }
    @Override
    public AimController getAimController() {

        return aimController;
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
    public Vec3 getAimOrigin() {

        return this.position().add(
                0,
                2,
                0
        );
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

        if(level().isClientSide())
            return;

        if(targeting != null)
            targeting.tick();

        if(brain != null)
            brain.tick();

        if(aimController != null)
            aimController.tick();

        if(attackController != null)
            attackController.tick();

        if(movement != null)
            movement.tick();

        this.move(
                MoverType.SELF,
                this.getDeltaMovement()
        );
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
