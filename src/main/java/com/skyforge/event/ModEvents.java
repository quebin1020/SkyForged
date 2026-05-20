package com.skyforge.event;

import com.skyforge.entity.*;
import com.skyforge.entity.boss.BossGunshipEntity;
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
                ModEntities.BASIC_AIRSHIP.get(),

                AirshipEntity
                        .createAttributes()
                        .build()
        );
        event.put(
                ModEntities.BASIC_PLANE.get(),

                PlaneEntity
                        .createAttributes()
                        .build()
        );

        event.put(
                ModEntities.SCOUT.get(),
                ScoutEntity.createAttributes().build()
        );

        event.put(
                ModEntities.GUNSHIP.get(),
                GunshipEntity.createAttributes().build()
        );

        event.put(
                ModEntities.BASIC_TURRET.get(),
                BasicTurretEntity.createAttributes().build()
        );

        event.put(
                ModEntities.BOSS_GUNSHIP.get(),
                BossGunshipEntity.createAttributes().build()
        );

        event.put(
                ModEntities.BOMBER.get(),
                BomberEntity.createAttributes().build()
        );

        event.put(
                ModEntities.MISSILE_HELICOPTER.get(),
                MissileHelicopterEntity.createAttributes().build()
        );

        event.put(
                ModEntities.DROPSHIP.get(),
                DropshipEntity.createAttributes().build()
        );

        event.put(
                ModEntities.HEAVY_DROPSHIP.get(),
                HeavyDropshipEntity.createAttributes().build()
        );
    }
}