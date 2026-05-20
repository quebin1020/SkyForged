package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.ScoutCombatBehavior;
import com.skyforge.attack.ScoutAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.movement.AirplaneMovement;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * Scout — aeronave de reconocimiento y ataque ligero.
 *
 * Perfil:
 *  - La más rápida de la flota (maxSpeed 0.95, turnRate 9)
 *  - 1 cañón fijo frontal con metralladora ligera (machine_gun_bullet)
 *  - Pasadas rasantes, baja altitud
 *  - Poca HP — muere rápido si la pillan
 *
 * Diferencias vs PlaneEntity:
 *  - Mucho más ágil (turnRate 9 vs 4)
 *  - Metralladora en ráfaga vs HE shell puntual
 *  - Radio de patrulla más corto
 *  - ScoutCombatBehavior → pasadas rasantes
 */
public class ScoutEntity extends AbstractAerialEntity {

    public ScoutEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        initTurrets();
        // Un solo cañón fijo frontal con perfil de metralladora
        AimController gun = turrets.get(0);
        if (gun != null) {
            gun.setMode(AimController.AimMode.FIXED_GUN);
        }

        FlightConfig config = new FlightConfig(
                0.95f,  // maxSpeed — el más rápido
                0.09f,  // acceleration — respuesta alta
                9f,     // turnRate — curvas cerradas
                0.06f,  // climbRate
                0.93f,  // drag
                false,  // no puede rotar en el lugar
                true    // requiere movimiento hacia adelante
        );
        config.minTerrainClearance = 9f; // scout vuela bajo pero no contra el piso

        this.targeting       = new TargetingSystem(this);
        this.brain           = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.scout()));
        this.combatBehavior  = new ScoutCombatBehavior(this);
        this.movement        = new AirplaneMovement(this, config);
        this.attackController = new ScoutAttackController(this);
    }

    @Override
    protected void initTurrets() {
        // Solo 1 torreta — cañón fijo frontal
        turrets.put(0, new AimController(this, 0, AimProfile.MACHINE_GUN));
    }

    @Override
    public net.minecraft.world.phys.Vec3 getTurretOrigin(int id) {
        // Cañón montado en el morro
        return position().add(0, 0.5, 1.0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.65)
                .add(Attributes.MOVEMENT_SPEED, 0.55)
                .add(Attributes.FOLLOW_RANGE, 100.0);
    }
}
