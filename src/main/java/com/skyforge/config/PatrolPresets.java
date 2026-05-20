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

    /**
     * Scout — patrulla a baja altitud con cambios de dirección frecuentes.
     * Radio corto (50-150) para dar sensación de velocidad.
     */
    public static PatrolConfig scout() {

        PatrolConfig config = new PatrolConfig();

        config.minDistance = 50;
        config.maxDistance = 150;

        config.maxAngleOffset = 55f;
        config.forwardBias    = 0.9;
        config.allowBehind    = false;
        config.suddenTurnChance = 0.04;

        config.preferredAltitude = 0;
        config.verticalVariance  = 8;
        config.minimumAltitude   = 70;
        config.maximumAltitude   = 180;

        config.followTerrain   = false;

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