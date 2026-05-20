package com.skyforge.ai.combat;

import com.skyforge.integration.cbc.CBCAmmoType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AimController {

    public enum AimMode {
        FREE_TRACKING,   // helicóptero — tracking completo
        LIMITED_TURN,    // airship — movimiento lento/rígido
        FIXED_GUN        // avión — solo dispara si ya está alineado
    }

    protected final AimProfile profile;
    protected final CombatPlatform platform;
    protected final int turretId;

    protected Vec3 aimedPosition;
    protected Vec3 predictedPosition;

    protected AimMode mode;
    protected CBCAmmoType ammoType;

    // Estos campos son sobreescritura del perfil cuando se ajustan vía setters
    protected double aimSpeed;
    protected double projectileSpeed;
    protected double firingTolerance;
    protected double baseInaccuracy;
    protected boolean predictiveAim;

    // ── Constructores ─────────────────────────────────────────────────────────

    /**
     * Constructor completo: perfil + ammo + modo explícito.
     */
    public AimController(CombatPlatform platform, int id, AimProfile profile, CBCAmmoType ammoType, AimMode mode) {
        this.platform = platform;
        this.turretId = id;
        this.profile = profile;
        this.ammoType = ammoType;
        this.mode = mode;
        // Copiar valores del perfil como defaults sobreescribibles
        this.aimSpeed = profile.aimSpeed;
        this.projectileSpeed = profile.projectileSpeed;
        this.firingTolerance = profile.firingTolerance;
        this.baseInaccuracy = profile.baseInaccuracy;
        this.predictiveAim = profile.predictive;
    }

    /**
     * Constructor con ammo — modo derivado del perfil (canRotate → FREE_TRACKING).
     */
    public AimController(CombatPlatform platform, int id, AimProfile profile, CBCAmmoType ammoType) {
        this(platform, id, profile, ammoType, profile.defaultAimMode());
    }

    /**
     * Constructor legacy sin ammo — usa MACHINE_GUN_BULLET como default.
     */
    public AimController(CombatPlatform platform, int id, AimProfile profile) {
        this(platform, id, profile, CBCAmmoType.MACHINE_GUN_BULLET, profile.defaultAimMode());
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    public void tick(LivingEntity target) {
        if (target == null) {
            aimedPosition = null;
            predictedPosition = null;
            return;
        }
        updatePrediction(target);
        updateAim();
    }

    protected void updatePrediction(LivingEntity target) {
        Vec3 targetPos = target.position();
        if (!predictiveAim) {
            predictedPosition = targetPos;
            return;
        }
        double distance = platform.getCombatPosition().distanceTo(targetPos);
        double travelTime = distance / projectileSpeed;
        predictedPosition = targetPos.add(target.getDeltaMovement().scale(travelTime));
    }

    protected void updateAim() {
        if (predictedPosition == null) return;
        if (aimedPosition == null) {
            aimedPosition = predictedPosition;
            return;
        }

        Vec3 delta = predictedPosition.subtract(aimedPosition);

        switch (mode) {
            case FREE_TRACKING -> aimedPosition = aimedPosition.add(delta.scale(aimSpeed));
            case LIMITED_TURN  -> aimedPosition = aimedPosition.add(delta.scale(aimSpeed * 0.4));
            case FIXED_GUN -> {
                if (!isAligned()) return;
                aimedPosition = predictedPosition;
            }
        }
    }

    // ── Disparar ─────────────────────────────────────────────────────────────

    /**
     * Dispara usando la munición asignada a esta torreta.
     * Aplica inaccuracy automáticamente.
     */
    public void fire(ServerLevel level, Entity owner, float charge, float spread) {
        if (ammoType == null) return;
        Vec3 dir = applyInaccuracy(getAimDirection());
        Vec3 origin = platform.getTurretOrigin(turretId);
        ammoType.fire(level, owner, origin, dir, charge, spread);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean canShoot() {
        if (predictedPosition == null) return false;

        Vec3 origin  = platform.getTurretOrigin(turretId);
        Vec3 current = getAimDirection();
        Vec3 desired = predictedPosition.subtract(origin).normalize();

        double dot   = Math.max(-1, Math.min(1, current.dot(desired)));
        double angle = Math.toDegrees(Math.acos(dot));
        return angle <= firingTolerance * getToleranceMultiplier();
    }

    public Vec3 getAimDirection() {
        if (aimedPosition == null) return Vec3.ZERO;
        Vec3 origin = platform.getTurretOrigin(turretId);
        return aimedPosition.subtract(origin).normalize();
    }

    public Vec3 applyInaccuracy(Vec3 direction) {
        direction = direction.add(
                (platform.getCombatRandom().nextDouble() - 0.5) * baseInaccuracy,
                (platform.getCombatRandom().nextDouble() - 0.5) * baseInaccuracy,
                (platform.getCombatRandom().nextDouble() - 0.5) * baseInaccuracy
        );
        return direction.normalize();
    }

    // ── Internos ─────────────────────────────────────────────────────────────

    private double getToleranceMultiplier() {
        return switch (mode) {
            case FREE_TRACKING -> 1.0;
            case LIMITED_TURN  -> 0.6;
            case FIXED_GUN     -> 0.25;
        };
    }

    private boolean isAligned() {
        if (predictedPosition == null) return false;
        Vec3 origin  = platform.getTurretOrigin(turretId);
        Vec3 current = getAimDirection();
        Vec3 desired = predictedPosition.subtract(origin).normalize();
        double dot   = Math.max(-1, Math.min(1, current.dot(desired)));
        return Math.toDegrees(Math.acos(dot)) <= firingTolerance * 0.25;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public CBCAmmoType getAmmoType()                { return ammoType; }
    public void setAmmoType(CBCAmmoType type)       { this.ammoType = type; }
    public void setMode(AimMode mode)               { this.mode = mode; }
    public AimMode getMode()                        { return mode; }
    public void setAimSpeed(double v)               { this.aimSpeed = v; }
    public void setProjectileSpeed(double v)        { this.projectileSpeed = v; }
    public void setFiringTolerance(double v)        { this.firingTolerance = v; }
    public void setBaseInaccuracy(double v)         { this.baseInaccuracy = v; }
    public Vec3 getAimedPosition()                  { return aimedPosition; }
    public Vec3 getPredictedPosition()              { return predictedPosition; }
}
