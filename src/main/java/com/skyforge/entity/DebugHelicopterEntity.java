package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.attack.MachineGunAttack;
import com.skyforge.config.FlightConfig;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class DebugHelicopterEntity extends AbstractAerialEntity {

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

        this.movement = new HelicopterMovement(this, config);

        this.attack = new MachineGunAttack(this);

        this.targeting = new TargetingSystem(this);

        this.brain = new AIStateMachine(this);
    }

    public static AttributeSupplier.Builder createAttributes() {

        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
}