package com.skyforge.event;

import com.skyforge.entity.DebugHelicopterEntity;
import com.skyforge.entity.ModEntities;
import com.skyforge.entity.BasicTurretEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEvents {

    public static void registerAttributes(
            EntityAttributeCreationEvent event
    ) {

        event.put(
                ModEntities.DEBUG_HELICOPTER.get(),

                DebugHelicopterEntity
                        .createAttributes()
                        .build()
        );

        event.put(
                ModEntities.BASIC_TURRET.get(),

                Mob.createMobAttributes()

                        .add(
                                Attributes.MAX_HEALTH,
                                40
                        )

                        .add(
                                Attributes.FOLLOW_RANGE,
                                100
                        )

                        .build()
        );
    }
}