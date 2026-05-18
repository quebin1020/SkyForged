package com.skyforge.config;

public class PatrolPresets {

    public static PatrolConfig helicopter() {

        PatrolConfig config =
                new PatrolConfig();

        /*
            DISTANCIA
         */

        config.minDistance = 40;

        config.maxDistance = 90;

        /*
            DIRECCIÓN
         */

        config.maxAngleOffset = 90f;

        config.forwardBias = 0.65;

        config.allowBehind = true;

        config.suddenTurnChance = 0.08;

        /*
            ALTURA
         */

        config.preferredAltitude = 10;

        config.verticalVariance = 10;

        config.minimumAltitude = 60;

        config.maximumAltitude = 160;

        /*
            TERRENO
         */

        config.followTerrain = true;

        config.terrainClearance = 25;

        return config;
    }

    public static PatrolConfig plane() {

        PatrolConfig config =
                new PatrolConfig();

        /*
            DISTANCIA
         */

        config.minDistance = 200;

        config.maxDistance = 500;

        /*
            DIRECCIÓN
         */

        config.maxAngleOffset = 35f;

        config.forwardBias = 0.98;

        config.allowBehind = false;

        config.suddenTurnChance = 0.01;

        /*
            ALTURA
         */

        config.preferredAltitude = 0;

        config.verticalVariance = 5;

        config.minimumAltitude = 120;

        config.maximumAltitude = 320;

        /*
            TERRENO
         */

        config.followTerrain = false;

        return config;
    }

    public static PatrolConfig boat() {

        PatrolConfig config =
                new PatrolConfig();

        /*
            DISTANCIA
         */

        config.minDistance = 100;

        config.maxDistance = 250;

        /*
            DIRECCIÓN
         */

        config.maxAngleOffset = 20f;

        config.forwardBias = 0.99;

        config.allowBehind = false;

        config.suddenTurnChance = 0.005;

        /*
            ALTURA
         */

        config.preferredAltitude = 0;

        config.verticalVariance = 0;

        config.minimumAltitude = 62;

        config.maximumAltitude = 64;

        /*
            TERRENO
         */

        config.followTerrain = false;

        return config;
    }
}