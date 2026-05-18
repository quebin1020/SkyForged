package com.skyforge.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface CombatBehavior {

    Vec3 getAttackPosition(LivingEntity target);
}