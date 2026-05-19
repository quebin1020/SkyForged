package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AirshipCombatBehavior;
import com.skyforge.attack.AirshipAttackController;
import com.skyforge.attack.TurretAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.movement.AirshipMovement;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class AirshipEntity extends AbstractAerialEntity {

    @Override
    public void tick() {

        super.tick();
        if (level().isClientSide()) return;
        if (brain != null && tickCount % 10 == 0) {

            if (brain.getPatrolTarget() != null) {

                DebugRender.drawLine(
                        level(),
                        this.position(),
                        brain.getPatrolTarget()
                );
            }
        }
    }

    public AirshipEntity(EntityType<? extends Mob> type, Level level) {

        super(type, level);
        this.setNoGravity(true);
        initTurrets();
        for (AimController turret : turrets.values()) {
            turret.setMode(AimController.AimMode.LIMITED_TURN);
        }

        FlightConfig config = new FlightConfig(
                0.12f,   // velocidad baja
                0.01f,   // poca aceleración
                10f,     // inercia alta
                0.01f,   // corrección mínima
                0.995f,  // muy estable
                false,
                false
        );

        this.targeting = new TargetingSystem(this);

        this.brain = new AIStateMachine(
                this,
                new PatrolNavigator(this, PatrolPresets.boat())
        );

        this.combatBehavior = new AirshipCombatBehavior(this);

        this.movement = new AirshipMovement(this, config);

        this.attackController = new AirshipAttackController(this);
    }

    public static AttributeSupplier.Builder createAttributes() {

        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FLYING_SPEED, 0.15)
                .add(Attributes.MOVEMENT_SPEED, 0.10)
                .add(Attributes.FOLLOW_RANGE, 96.0);
    }
}