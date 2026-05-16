package com.skyforge.ai;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.phys.Vec3;

import static com.skyforge.SkyforgeMod.LOGGER1;

public class AIStateMachine {

    protected Vec3 patrolTarget;

    protected final AbstractAerialEntity entity;

    protected AIState currentState = AIState.PATROL;

    public AIStateMachine(AbstractAerialEntity entity) {
        this.entity = entity;
    }

    public void tick() {
        if (this.entity.level().isClientSide()) return;
        switch (currentState) {

            case IDLE -> idleTick();
            case PATROL -> patrolTick();
            case CHASE -> chaseTick();
            case ATTACK_RUN -> attackRunTick();
            case EVADE -> evadeTick();
        }
    }

    protected void idleTick() {
    }

    protected void patrolTick() {

        if (patrolTarget == null || entity.position().distanceTo(patrolTarget) < 3) {

            patrolTarget = entity.position().add(
                    entity.getRandom().nextInt(40) - 20,
                    entity.getRandom().nextInt(10) - 5,
                    entity.getRandom().nextInt(40) - 20
            );
            LOGGER1.info("setting new targetpoint");
        }

        entity.getMovementController().setTargetPosition(patrolTarget);

    }

    protected void chaseTick() {
    }

    protected void attackRunTick() {
    }

    protected void evadeTick() {
    }

    public void setState(AIState state) {
        this.currentState = state;
    }

    public AIState getState() {
        return currentState;
    }

    public Vec3 getPatrolTarget(){ return patrolTarget;}
}
