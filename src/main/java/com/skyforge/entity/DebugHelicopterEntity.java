package com.skyforge.entity;

import com.skyforge.SkyforgeMod;
import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.HelicopterAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;

import com.skyforge.util.DebugRender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import static com.skyforge.SkyforgeMod.LOGGER1;

public class DebugHelicopterEntity extends AbstractAerialEntity {


    @Override
    public void tick() {

        super.tick();

        if(tickCount % 5 == 0) {

            if(brain.getPatrolTarget() != null) {

                DebugRender.drawLine(
                        level(),
                        this.position(),
                        brain.getPatrolTarget()
                );
            }
        }
    }

    public DebugHelicopterEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        FlightConfig config = new FlightConfig(
                0.35f,
                0.03f,
                4f,
                0.02f,
                0.98f,
                true,
                false
        );

        this.combatBehavior = new HelicopterCombatBehavior(this);

        this.movement = new HelicopterMovement(this, config);

        this.attackController = new HelicopterAttackController(this);

        this.targeting = new TargetingSystem(this);

        this.brain = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
    }

    public static AttributeSupplier.Builder createAttributes() {

        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
}