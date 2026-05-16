package com.skyforge.config;

import com.skyforge.config.PatrolConfig;

public class PatrolPresets {

    public static PatrolConfig helicopter() {

        PatrolConfig config = new PatrolConfig();

        config.minDistance = 40;
        config.maxDistance = 90;

        config.maxAngleOffset = 90f;

        config.verticalVariance = 10;

        config.followTerrain = true;

        config.terrainClearance = 25;

        config.forwardBias = 0.65;

        config.allowBehind = true;

        return config;
    }

    public static PatrolConfig plane() {

        PatrolConfig config = new PatrolConfig();

        config.minDistance = 200;
        config.maxDistance = 500;

        config.maxAngleOffset = 35f;

        config.verticalVariance = 5;

        config.followTerrain = false;

        config.preferredAltitude = 140;

        config.forwardBias = 0.98;

        config.allowBehind = false;

        return config;
    }

    public static PatrolConfig boat() {

        PatrolConfig config = new PatrolConfig();

        config.minDistance = 100;
        config.maxDistance = 250;

        config.maxAngleOffset = 20f;

        config.verticalVariance = 0;

        config.followTerrain = false;

        config.forwardBias = 0.99;

        config.allowBehind = false;

        return config;
    }
}