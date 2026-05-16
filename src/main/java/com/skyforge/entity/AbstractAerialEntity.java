package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.attack.AttackController;
import com.skyforge.movement.FlightMovementController;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;

public abstract class AbstractAerialEntity extends Mob {

    protected FlightMovementController movement;
    protected AttackController attack;
    protected AIStateMachine brain;
    protected TargetingSystem targeting;

    protected AbstractAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()){
            if (targeting != null)
                targeting.tick();

            if (brain != null)
                brain.tick();

            if (movement != null)
                movement.tick();

            if (attack != null)
                attack.tick();

            this.move(MoverType.SELF, this.getDeltaMovement());
        }

    }

    public FlightMovementController getMovementController() {
        return movement;
    }

    public TargetingSystem getTargetingSystem() {
        return targeting;
    }
}
