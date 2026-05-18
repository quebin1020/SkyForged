package com.skyforge.config;

public class PatrolConfig {

    /*
        DISTANCIA
     */

    // Distancia mínima del patrol point
    public double minDistance = 40;

    // Distancia máxima del patrol point
    public double maxDistance = 80;

    /*
        DIRECCIÓN
     */

    // Cuánto puede desviarse respecto al forward
    public float maxAngleOffset = 45f;

    // Preferencia a seguir hacia adelante
    // 1 = totalmente forward
    // 0 = totalmente random
    public double forwardBias = 0.9;

    // Permitir patrols detrás
    public boolean allowBehind = false;

    // Probabilidad de giro brusco
    public double suddenTurnChance = 0.05;

    /*
        ALTURA
     */

    // Altura relativa preferida
    // Se suma a la altura actual
    public double preferredAltitude = 20;

    // Variación vertical random
    public double verticalVariance = 5;

    // Altura mínima permitida
    public double minimumAltitude = 50;

    // Altura máxima permitida
    public double maximumAltitude = 200;

    /*
        TERRENO
     */

    // Seguir altura del terreno
    public boolean followTerrain = true;

    // Distancia sobre el terreno
    public double terrainClearance = 20;
}