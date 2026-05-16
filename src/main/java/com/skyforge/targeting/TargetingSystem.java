package com.skyforge.targeting;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TargetingSystem {

    protected final AbstractAerialEntity entity;

    protected LivingEntity currentTarget;

    public TargetingSystem(AbstractAerialEntity entity) {

        this.entity = entity;
    }
    public void tick(){
        validateTarget();

        if(currentTarget == null) {

            findTarget();
        }
    }

    protected void findTarget() {

        List<Player> nearbyPlayers =
                entity.level().getEntitiesOfClass(
                        Player.class,
                        entity.getBoundingBox().inflate(100)
                );

        if(nearbyPlayers.isEmpty()) return;

        currentTarget = nearbyPlayers.getFirst();
    }

    protected void validateTarget() {

        if(currentTarget == null) return;

        if(!currentTarget.isAlive()) {

            clearTarget();

            return;
        }

        if(currentTarget.isRemoved()) {

            clearTarget();

            return;
        }

        double distance =
                entity.position()
                        .distanceTo(
                                currentTarget.position()
                        );

        if(distance > 300) {

            clearTarget();
        }
    }

    public LivingEntity getTarget() {

        return currentTarget;
    }

    public void setTarget(LivingEntity target) {

        this.currentTarget = target;
    }

    public void clearTarget() {

        this.currentTarget = null;
    }
}
