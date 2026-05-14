package com.skyforge.event;

import com.skyforge.entity.DebugHelicopterEntity;
import com.skyforge.entity.ModEntities;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEvents {

    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(
                ModEntities.DEBUG_HELICOPTER.get(),
                DebugHelicopterEntity.createAttributes().build()
        );
    }
}