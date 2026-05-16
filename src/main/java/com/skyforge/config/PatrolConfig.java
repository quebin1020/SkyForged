package com.skyforge.config;
public class PatrolConfig {

    // Distancia del patrol point
    public double minDistance = 40;
    public double maxDistance = 80;

    // Cuánto puede desviarse respecto al forward
    public float maxAngleOffset = 45f;

    // Altura preferida
    public double preferredAltitude = 90;

    // Variación vertical random
    public double verticalVariance = 5;

    // Seguir altura del terreno
    public boolean followTerrain = true;

    // Distancia sobre el terreno
    public double terrainClearance = 20;

    // Probabilidad de giro brusco
    public double suddenTurnChance = 0.05;

    // Preferencia a seguir hacia adelante
    // 1 = totalmente forward
    // 0 = totalmente random
    public double forwardBias = 0.9;

    // Permitir patrols detrás
    public boolean allowBehind = false;
}