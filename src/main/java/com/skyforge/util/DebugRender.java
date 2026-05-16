package com.skyforge.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.logging.Logger;

import static com.skyforge.SkyforgeMod.LOGGER1;

public class DebugRender {
    public static void drawLine(Level level, Vec3 vstart, Vec3 vend){
        if (!level.isClientSide()) return;
        LOGGER1.info("vector: " + vstart.x + vstart.y + vstart.z);
        for (int i = 0;i <= 20;i++){
            double t = i/20;
            Vec3 p = vstart.lerp(vend,t);

            level.addParticle(
                    ParticleTypes.END_ROD,
                    p.x,p.y,p.z,
                    0,0,0
            );

        }
    }
}
