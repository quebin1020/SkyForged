package com.skyforge.targeting;

import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class TargetingSystem {

    protected final CombatPlatform platform;

    protected LivingEntity currentTarget;

    protected double detectionRange = 100;

    protected double loseTargetRange = 300;

    public TargetingSystem(
            CombatPlatform platform
    ) {

        this.platform = platform;
    }

    public void tick() {

        validateTarget();

        if(currentTarget == null) {

            findTarget();
        }
    }

    protected void findTarget() {

        AABB searchBox =
                new AABB(
                        platform.getCombatPosition(),
                        platform.getCombatPosition()
                ).inflate(
                        detectionRange
                );

        List<Player> nearbyPlayers =
                platform.getCombatLevel()
                        .getEntitiesOfClass(
                                Player.class,
                                searchBox
                        );

        if(nearbyPlayers.isEmpty())
            return;

        Player closest = null;

        double closestDistance =
                Double.MAX_VALUE;

        for(Player player : nearbyPlayers) {

            if(player.isCreative())
                continue;

            if(player.isSpectator())
                continue;

            double distance =
                    platform.getCombatPosition()
                            .distanceTo(
                                    player.position()
                            );

            if(distance < closestDistance) {

                closestDistance =
                        distance;

                closest = player;
            }
        }

        currentTarget = closest;
    }

    protected void validateTarget() {

        if(currentTarget == null)
            return;

        if(!currentTarget.isAlive()) {

            clearTarget();

            return;
        }

        if(currentTarget.isRemoved()) {

            clearTarget();

            return;
        }

        double distance =
                platform.getCombatPosition()
                        .distanceTo(
                                currentTarget.position()
                        );

        if(distance > loseTargetRange) {

            clearTarget();
        }
    }

    public LivingEntity getTarget() {

        return currentTarget;
    }

    public void setTarget(
            LivingEntity target
    ) {

        this.currentTarget = target;
    }

    public void clearTarget() {

        this.currentTarget = null;
    }

    public void setDetectionRange(
            double detectionRange
    ) {

        this.detectionRange =
                detectionRange;
    }

    public void setLoseTargetRange(
            double loseTargetRange
    ) {

        this.loseTargetRange =
                loseTargetRange;
    }
}
