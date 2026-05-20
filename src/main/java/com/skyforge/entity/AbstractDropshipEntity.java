package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.CombatBehavior;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.HelicopterAttackController;
import com.skyforge.config.PatrolPresets;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Base para los dropships de SkyForge.
 *
 * Ciclo de vida:
 *  APPROACHING → vuela hacia el punto de hover sobre el objetivo
 *  HOVERING    → llega al hover point, espera brevemente
 *  DEPLOYING   → spawna mobs del manifiesto con intervalo configurable
 *  RETREATING  → vuela lejos y desaparece
 *
 * Al morir: explota y spawna todos los mobs restantes del manifiesto.
 *
 * El manifiesto se define en la subclase con buildManifest().
 * Cada entrada es un EntityType que se spawna en secuencia.
 */
public abstract class AbstractDropshipEntity extends AbstractAerialEntity {

    // ── Constantes ────────────────────────────────────────────────────────────

    protected static final double HOVER_HEIGHT    = 28.0;
    protected static final double HOVER_THRESHOLD = 6.0;
    protected static final int    PRE_DEPLOY_WAIT = 30;   // ticks antes del primer spawn
    protected static final int    DEPLOY_INTERVAL = 40;   // ticks entre spawns

    // ── Estado ────────────────────────────────────────────────────────────────

    private enum DropState { APPROACHING, HOVERING, DEPLOYING, RETREATING }

    private DropState dropState   = DropState.APPROACHING;
    private int       deployIndex = 0;
    private int       deployTimer = 0;
    private Vec3      retreatTarget;

    // Manifiesto: lista de EntityType a spawnear en orden
    private final List<EntityType<? extends Mob>> manifest;

    // ── Constructor ───────────────────────────────────────────────────────────

    protected AbstractDropshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.manifest = buildManifest();
    }

    /** Define qué mobs spawna este dropship, en orden. */
    protected abstract List<EntityType<? extends Mob>> buildManifest();

    // ── Inicialización de sistemas ────────────────────────────────────────────

    protected void initDropshipSystems() {
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new DropshipCombatBehavior(this);
        this.attackController = null;   // sin armas ofensivas por defecto
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        tickDropLogic();
    }

    private void tickDropLogic() {
        switch (dropState) {
            case APPROACHING -> checkArrival();
            case HOVERING    -> tickHover();
            case DEPLOYING   -> tickDeploy();
            case RETREATING  -> tickRetreat();
        }
    }

    private void checkArrival() {
        LivingEntity target = getCombatTarget();
        if (target == null) return;
        Vec3 hoverPoint = target.position().add(0, HOVER_HEIGHT, 0);
        if (position().distanceTo(hoverPoint) < HOVER_THRESHOLD) {
            dropState   = DropState.HOVERING;
            deployTimer = PRE_DEPLOY_WAIT;
        }
    }

    private void tickHover() {
        if (deployTimer > 0) { deployTimer--; return; }
        dropState   = DropState.DEPLOYING;
        deployTimer = 0;
    }

    private void tickDeploy() {
        if (deployIndex >= manifest.size()) {
            startRetreat();
            return;
        }
        if (deployTimer > 0) { deployTimer--; return; }
        spawnNextMob();
        deployTimer = DEPLOY_INTERVAL;
    }

    private void spawnNextMob() {
        if (!(level() instanceof ServerLevel sl)) return;
        EntityType<? extends Mob> type = manifest.get(deployIndex);
        Mob mob = type.create(sl);
        if (mob != null) {
            double angle = getRandom().nextDouble() * Math.PI * 2;
            double dist  = 2 + getRandom().nextDouble() * 4;
            mob.moveTo(getX() + Math.cos(angle) * dist,
                       getY() - 2,
                       getZ() + Math.sin(angle) * dist,
                       getRandom().nextFloat() * 360f, 0f);
            sl.addFreshEntity(mob);
        }
        deployIndex++;
    }

    private void startRetreat() {
        dropState = DropState.RETREATING;
        LivingEntity lastTarget = getCombatTarget();
        Vec3 away = lastTarget != null
                ? position().subtract(lastTarget.position()).normalize()
                : new Vec3(getRandom().nextDouble() - 0.5, 0, getRandom().nextDouble() - 0.5).normalize();
        retreatTarget = position().add(away.scale(400)).add(0, 60, 0);
        if (movement != null) movement.setTargetPosition(retreatTarget);
    }

    private void tickRetreat() {
        if (retreatTarget == null) { startRetreat(); return; }
        if (movement != null) movement.setTargetPosition(retreatTarget);
        if (position().distanceTo(retreatTarget) < 30) discard();
    }

    // ── Muerte ────────────────────────────────────────────────────────────────

    @Override
    protected void tickDeath() {
        if (this.deathTime == 0) {
            if (!level().isClientSide()) {
                level().explode(this, getX(), getY(), getZ(),
                        getDeathExplosionRadius(), Level.ExplosionInteraction.NONE);
                // Spawnea los mobs restantes en modo caótico
                if (level() instanceof ServerLevel sl) {
                    while (deployIndex < manifest.size()) {
                        EntityType<? extends Mob> type = manifest.get(deployIndex);
                        Mob mob = type.create(sl);
                        if (mob != null) {
                            double a = getRandom().nextDouble() * Math.PI * 2;
                            double d = 2 + getRandom().nextDouble() * 6;
                            mob.moveTo(getX() + Math.cos(a)*d, getY(), getZ() + Math.sin(a)*d,
                                       getRandom().nextFloat() * 360f, 0f);
                            sl.addFreshEntity(mob);
                        }
                        deployIndex++;
                    }
                }
            }
        }
        ++this.deathTime;
        if (this.deathTime >= 3) this.discard();
    }

    @Override protected float getDeathExplosionRadius() { return 4.5f; }

    public DropState getDropState() { return dropState; }

    // ── CombatBehavior: hover directamente sobre el objetivo ──────────────────

    private static class DropshipCombatBehavior extends HelicopterCombatBehavior {
        DropshipCombatBehavior(AbstractAerialEntity entity) {
            super(entity);
            this.orbitSpeed = 0;
        }

        @Override
        public Vec3 getAttackPosition(LivingEntity target) {
            AbstractDropshipEntity dropship = (AbstractDropshipEntity) entity;
            if (dropship.dropState == DropState.RETREATING && dropship.retreatTarget != null) {
                return dropship.retreatTarget;
            }
            return target.position().add(0, HOVER_HEIGHT, 0);
        }
    }
}
