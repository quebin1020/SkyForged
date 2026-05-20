package com.skyforge.entity.boss;

import com.skyforge.ai.combat.AimController;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base para bosses aéreos multi-estado con partes rompibles.
 *
 * ── Configuración en el constructor de la subclase ──────────────────────────
 *
 *   // 1. Registrar torretas (igual que AbstractAerialEntity)
 *   addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(0,2,3));
 *   addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.HE_SHELL,           new Vec3(0,2,-3));
 *   turretInit();
 *
 *   // 2. Registrar partes rompibles
 *   addBossPart("cannon_front", "cannon_front",  turretId=0, maxHp=80f);
 *   addBossPart("cannon_rear",  "cannon_rear",   turretId=1, maxHp=80f);
 *
 *   // 3. Definir fases (de inicio a final)
 *   initBoss("Helicopter Mk-II Boss",
 *       BossPhase.start(),
 *       BossPhase.escalate(0.65f, CBCAmmoType.AP_SHELL, 1.3f, 0.8f, "Phase 2 Engaged!"),
 *       BossPhase.escalate(0.30f, CBCAmmoType.AP_SHELL, 1.6f, 0.6f, "Final Phase!")
 *   );
 *
 * ── Transición de fases ───────────────────────────────────────────────────
 * Al bajar a un threshold de HP se llama onPhaseChange() y se aplica:
 *   - overrideAmmo: reemplaza el ammo de TODAS las torretas activas
 *   - speedMultiplier: modifica FlightConfig.speedMultiplier para el movimiento
 *   - displayMessage: imprime en chat de jugadores cercanos
 *
 * ── Partes rompibles ─────────────────────────────────────────────────────
 * damagePartDirect("cannon_front", 30f) reduce la HP de esa parte.
 * Al destruirse: la torreta vinculada se desactiva y se sincroniza al cliente
 * via bitmask para que el renderer oculte el hueso.
 *
 * ── Client-side ──────────────────────────────────────────────────────────
 * isPartDestroyed(partId) se puede llamar desde el renderer para ocultar huesos.
 */
public abstract class AbstractBossAerialEntity extends AbstractAerialEntity {

    // Bitmask de partes destruidas — hasta 32 partes
    private static final EntityDataAccessor<Integer> PARTS_DESTROYED =
            SynchedEntityData.defineId(AbstractBossAerialEntity.class, EntityDataSerializers.INT);

    // Boss bar visible para jugadores cercanos
    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.literal("Boss"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.NOTCHED_6
    );

    // Fases
    protected BossPhase[] phases;
    protected int currentPhaseIndex = 0;

    // Partes rompibles
    private final Map<String, BossPart> bossParts = new LinkedHashMap<>();
    private final Map<String, Integer>  partBitIndex = new LinkedHashMap<>();

    // Torretas desactivadas por destrucción de partes
    protected final Set<Integer> disabledTurrets = new HashSet<>();

    protected AbstractBossAerialEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARTS_DESTROYED, 0);
    }

    // ── Builder de partes rompibles ───────────────────────────────────────────

    /**
     * Registra una parte rompible.
     * @param partId         Identificador único (ej. "cannon_left")
     * @param boneName       Nombre del hueso en el geo.json (ej. "cannon_left")
     * @param linkedTurretId Torreta que se desactiva al destruirse (-1 = ninguna)
     * @param maxHealth      HP de la parte
     */
    protected void addBossPart(String partId, String boneName, int linkedTurretId, float maxHealth) {
        int bitIndex = bossParts.size();
        partBitIndex.put(partId, bitIndex);
        bossParts.put(partId, new BossPart(partId, boneName, linkedTurretId, maxHealth));
    }

    /**
     * Configura el boss: nombre, barra de vida y fases.
     * Llamar tras addTurret() + turretInit() + addBossPart().
     *
     * @param displayName Nombre en la boss bar
     * @param phases      Fases en orden; el primero debe tener hpThreshold=1.0
     */
    protected void initBoss(String displayName, BossPhase... phases) {
        this.phases = phases;
        bossBar.setName(Component.literal(displayName));
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        bossBar.setProgress(Math.max(0f, Math.min(1f, getHealth() / getMaxHealth())));
        checkPhaseTransition();
    }

    private void checkPhaseTransition() {
        if (phases == null || phases.length == 0) return;
        float hpFrac = getHealth() / getMaxHealth();

        while (currentPhaseIndex < phases.length - 1
                && hpFrac <= phases[currentPhaseIndex + 1].hpThreshold()) {
            currentPhaseIndex++;
            applyPhase(phases[currentPhaseIndex]);
        }
    }

    private void applyPhase(BossPhase phase) {
        // Reemplazar ammo en todas las torretas activas
        if (phase.overrideAmmo() != null) {
            for (Map.Entry<Integer, AimController> entry : turrets.entrySet()) {
                if (!disabledTurrets.contains(entry.getKey())) {
                    entry.getValue().setAmmoType(phase.overrideAmmo());
                }
            }
        }

        // Aplicar multiplicador de velocidad al movimiento
        if (movement != null && movement.getConfig() != null) {
            movement.getConfig().speedMultiplier = phase.speedMultiplier();
        }

        // Mensaje en el mundo
        if (phase.displayMessage() != null) {
            level().players().forEach(player ->
                    player.sendSystemMessage(Component.literal(phase.displayMessage())));
        }

        onPhaseChange(currentPhaseIndex, phase);
    }

    /** Override en subclase para comportamiento extra al cambiar fase. */
    protected void onPhaseChange(int phaseIndex, BossPhase phase) {}

    // ── Partes rompibles ──────────────────────────────────────────────────────

    /**
     * Aplica daño directo a una parte específica.
     * Usa esto desde un sistema de hit-detection custom.
     */
    public void damagePartDirect(String partId, float damage) {
        BossPart part = bossParts.get(partId);
        if (part == null || part.isDestroyed()) return;
        part.damage(damage);
        if (part.isDestroyed()) {
            onPartDestroyed(partId, part);
        }
    }

    protected void onPartDestroyed(String partId, BossPart part) {
        // Marcar bit en EntityData (sincroniza al cliente)
        Integer bitIdx = partBitIndex.get(partId);
        if (bitIdx != null) {
            entityData.set(PARTS_DESTROYED, entityData.get(PARTS_DESTROYED) | (1 << bitIdx));
        }
        // Desactivar torreta vinculada
        if (part.getLinkedTurretId() >= 0) {
            disabledTurrets.add(part.getLinkedTurretId());
        }
    }

    // ── Queries (servidor y cliente) ──────────────────────────────────────────

    /** Comprueba si una parte está destruida (legible desde el renderer). */
    public boolean isPartDestroyed(String partId) {
        Integer bitIdx = partBitIndex.get(partId);
        if (bitIdx == null) return false;
        return (entityData.get(PARTS_DESTROYED) & (1 << bitIdx)) != 0;
    }

    public int getCurrentPhase()                 { return currentPhaseIndex; }
    public Map<String, BossPart> getBossParts()  { return bossParts; }

    // ── Override: torretas desactivadas son null para el attack controller ────

    @Override
    public AimController getAimController(int id) {
        if (disabledTurrets.contains(id)) return null;
        return super.getAimController(id);
    }

    // ── Boss bar ─────────────────────────────────────────────────────────────

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossBar.removePlayer(player);
    }
}
