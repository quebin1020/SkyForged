package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.PlaneCombatBehavior;
import com.skyforge.attack.PlaneAttackController;
import com.skyforge.attack.TurretAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.movement.AirplaneMovement;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class PlaneEntity extends AbstractAerialEntity {

    @Override
    public void tick() {

        super.tick();
        if (level().isClientSide()) return;
        if (brain != null && tickCount % 3 == 0) {

            if (brain.getPatrolTarget() != null) {

                DebugRender.drawLine(
                        level(),
                        this.position(),
                        brain.getPatrolTarget()
                );
            }
        }
    }

    public PlaneEntity(EntityType<? extends Mob> type, Level level) {

        super(type, level);
        this.setNoGravity(true);
        initTurrets();
        for (AimController turret : turrets.values()) {
            turret.setMode(AimController.AimMode.FIXED_GUN);
        }

        FlightConfig config = new FlightConfig(
                0.65f,  // rápido
                0.06f,  // respuesta alta
                2.5f,   // poca inercia
                0.05f,  // más corrección
                0.92f,  // menos estabilidad
                true,
                true
        );

        this.targeting = new TargetingSystem(this);

        this.brain = new AIStateMachine(
                this,
                new PatrolNavigator(this, PatrolPresets.plane())
        );

        this.combatBehavior = new PlaneCombatBehavior(this);

        this.movement = new AirplaneMovement(this, config);

        this.attackController = new PlaneAttackController(this);
    }

    public static AttributeSupplier.Builder createAttributes() {

        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.FLYING_SPEED, 0.45)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 80.0);
    }
}