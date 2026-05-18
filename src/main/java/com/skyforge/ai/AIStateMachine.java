package com.skyforge.ai;

import com.skyforge.config.PatrolConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import static com.skyforge.SkyforgeMod.LOGGER1;

public class AIStateMachine {

    protected Vec3 patrolTarget;

    protected final AbstractAerialEntity entity;

    protected AIState currentState = AIState.PATROL;

    protected final PatrolConfig patrolConfig;

    protected final PatrolNavigator patrolNavigator;

    protected int stateTicks;

    protected int lastSeenTargetTicks;

    public AIStateMachine(
            AbstractAerialEntity entity,
            PatrolConfig patrolConfig
    ) {

        this.entity = entity;

        this.patrolConfig = patrolConfig;

        this.patrolNavigator =
                new PatrolNavigator(
                        entity,
                        patrolConfig
                );
    }

    public void tick() {
        System.out.println(
                "STATE: " + currentState
        );
        if(entity.level().isClientSide())
            return;

        updateStateTransitions();

        stateTicks++;

        switch(currentState) {

            case IDLE -> idleTick();

            case PATROL -> patrolTick();

            case CHASE -> chaseTick();

            case ATTACK -> attackTick();

            case EVADE -> evadeTick();
        }
    }

    protected void updateStateTransitions() {

        LivingEntity target =
                entity.getTargetingSystem()
                        .getTarget();

        if(target == null) {

            setState(AIState.PATROL);

            return;
        }

        double distance =
                entity.position()
                        .distanceTo(
                                target.position()
                        );

        switch(currentState) {

            case PATROL -> {

                setState(AIState.CHASE);
            }

            case CHASE -> {

                if(distance < 30) {

                    setState(AIState.ATTACK);
                }
            }

            case ATTACK -> {

                if(distance > 50) {

                    setState(AIState.CHASE);
                }

                if(entity.getHealth()
                        < entity.getMaxHealth() * 0.3f) {

                    setState(AIState.EVADE);
                }
            }

            case EVADE -> {

                if(stateTicks > 100) {

                    setState(AIState.PATROL);
                }
            }
        }
    }

    public void setState(
            AIState state
    ) {

        if(this.currentState == state)
            return;

        this.currentState = state;

        this.stateTicks = 0;
    }

    public AIState getState() {

        return currentState;
    }

    protected void idleTick() {
    }

    protected void patrolTick() {

        if(
                patrolTarget == null ||

                        entity.position()
                                .distanceTo(
                                        patrolTarget
                                ) < 5
        ) {

            patrolTarget =
                    patrolNavigator
                            .generatePatrolPoint();

            LOGGER1.info(
                    "setting new patrol point"
            );

            LOGGER1.info(
                    patrolTarget.x
                            + " "
                            + patrolTarget.y
                            + " "
                            + patrolTarget.z
            );
        }

        entity.getMovementController()
                .setTargetPosition(
                        patrolTarget
                );
    }

    protected void chaseTick() {

        LivingEntity target =
                entity.getTargetingSystem()
                        .getTarget();

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

    protected void attackTick() {

        LivingEntity target =
                entity.getTargetingSystem()
                        .getTarget();

        if(target == null)
            return;

        Vec3 attackPosition =
                entity.getCombatBehavior()
                        .getAttackPosition(
                                target
                        );

        entity.getMovementController()
                .setTargetPosition(
                        attackPosition
                );
    }

    protected void evadeTick() {

        LivingEntity target =
                entity.getTargetingSystem()
                        .getTarget();

        if(target == null)
            return;

        Vec3 evadeDirection =
                entity.position()
                        .subtract(
                                target.position()
                        )
                        .normalize();

        Vec3 evadePosition =
                entity.position()
                        .add(
                                evadeDirection.scale(40)
                        )
                        .add(
                                0,
                                15,
                                0
                        );

        entity.getMovementController()
                .setTargetPosition(
                        evadePosition
                );
    }

    public Vec3 getPatrolTarget() {

        return patrolTarget;
    }
}
