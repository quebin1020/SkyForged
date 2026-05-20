package com.skyforge.entity.boss;

/**
 * Parte rompible de un boss aéreo.
 *
 * Cada parte tiene su propia HP. Al destruirse:
 *   - Se desactiva la torreta vinculada (linkedTurretId >= 0)
 *   - Se sincroniza al cliente vía bitmask para ocultar el hueso del modelo
 *   - Puede disparar un cambio de fase si la configuración lo indica
 *
 * Ejemplo de uso en AbstractBossAerialEntity:
 *   addBossPart("cannon_left",  "cannon_left_bone",  turretId=0, maxHp=50f);
 *   addBossPart("cannon_right", "cannon_right_bone", turretId=1, maxHp=50f);
 */
public class BossPart {

    private final String partId;
    private final String boneName;
    private final int linkedTurretId;
    private final float maxHealth;

    private float currentHealth;
    private boolean destroyed;

    public BossPart(String partId, String boneName, int linkedTurretId, float maxHealth) {
        this.partId = partId;
        this.boneName = boneName;
        this.linkedTurretId = linkedTurretId;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.destroyed = false;
    }

    public void damage(float amount) {
        if (destroyed) return;
        currentHealth = Math.max(0f, currentHealth - amount);
        if (currentHealth <= 0f) destroyed = true;
    }

    public boolean isDestroyed()      { return destroyed; }
    public String getPartId()         { return partId; }
    public String getBoneName()       { return boneName; }
    public int getLinkedTurretId()    { return linkedTurretId; }
    public float getMaxHealth()       { return maxHealth; }
    public float getCurrentHealth()   { return currentHealth; }
    public float getHealthFraction()  { return destroyed ? 0f : currentHealth / maxHealth; }
}
