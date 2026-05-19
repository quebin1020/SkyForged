package com.skyforge.ai;

import com.skyforge.ai.combat.CombatBehavior;
import com.skyforge.config.PatrolConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AIStateMachine {

    protected final AbstractAerialEntity entity;

    protected AIState currentState =
            AIState.PATROL;

    protected Vec3 patrolTarget;

    protected PatrolNavigator patrolNavigator;

    protected int stateTicks;

    public AIStateMachine(
            AbstractAerialEntity entity,
            PatrolNavigator patrolNavigator
    ) {

        this.entity = entity;

        this.patrolNavigator =
                patrolNavigator;
    }

    public void tick() {

        updateTransitions();

        stateTicks++;

        switch(currentState) {

            case IDLE -> idleTick();

            case PATROL -> patrolTick();

            case CHASE -> chaseTick();

            case ATTACK -> attackTick();

            case EVADE -> evadeTick();
        }
    }
    public Vec3 getPatrolTarget() {

        return patrolTarget;
    }

    /*
        TRANSITIONS
     */

    protected void updateTransitions() {

        LivingEntity target =
                entity.getCombatTarget();

        LivingEntity target =
                entity.getTargetingSystem()
                        .getTarget();

        if(target == null) {

            setState(AIState.PATROL);

            return;
        }

        double distance =
                entity.getCombatPosition()
                        .distanceTo(
                                target.position()
                        );

        switch(currentState) {

            case PATROL -> {

                setState(
                        AIState.CHASE
                );
            }

            case CHASE -> {

                if(distance < 60) {

                    setState(
                            AIState.ATTACK
                    );
                }
            }

            case ATTACK -> {

                if(distance > 90) {

                    setState(
                            AIState.CHASE
                    );
                }
            }
        }
    }

    /*
        STATES
     */

    protected void idleTick() {
    }

    protected void patrolTick() {

        if(
                patrolTarget == null
                        ||
                        entity.getCombatPosition()
                                .distanceTo(
                                        patrolTarget
                                ) < 5
        ) {

            patrolTarget =
                    patrolNavigator
                            .generatePatrolPoint();
        }

        entity.getMovementController()
                .setTargetPosition(
                        patrolTarget
                );
    }

    protected void chaseTick() {

        LivingEntity target =
                entity.getCombatTarget();

        if(target == null)
            return;

        entity.getMovementController()
                .setTargetPosition(
                        target.position()
                );
    }

    protected void attackTick() {

        LivingEntity target =
                entity.getCombatTarget();

        if(target == null)
            return;

        CombatBehavior behavior =
                entity.getCombatBehavior();

        Vec3 attackPosition =
                behavior.getAttackPosition(
                        target
                );

        entity.getMovementController()
                .setTargetPosition(
                        attackPosition
                );
    }

        if(target == null)
            return;

        Vec3 chasePosition =
                entity.getCombatBehavior()
                        .getAttackPosition(
                                target
                        );

        entity.getMovementController()
                .setTargetPosition(
                        chasePosition
                );
    }

    /*
        HELPERS
     */

    protected void setState(
            AIState state
    ) {

        if(currentState == state)
            return;

        currentState = state;

        stateTicks = 0;
    }

    public AIState getState() {

        return currentState;
    }
}
