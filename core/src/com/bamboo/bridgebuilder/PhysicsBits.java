package com.bamboo.bridgebuilder;

/** Used for Box2d Body categories and masks. */
public class PhysicsBits
{
    final public static short CHARACTER_PHYSICS = 0x0001;
    final public static short WORLD_PHYSICS = 0x0002;
    final public static short COLLISION_SORT_PHYSICS = 0x0004;
    final public static short BOUNDING_BODY_PHYSICS = 0x0008;
    final public static short ABILITY_PHYSICS = 0x0010;
    final public static short LIGHT_PHYSICS = 0x0020;
    final public static short FOG_PHYSICS = 0x0040;
    final public static short WIND_PHYSICS = 0x0080;
    final public static short VIEW_PHYSICS = 0x0100;
    final public static short MAP_VIEW_PHYSICS = 0x0200; // Things in the map that AI need to raycast to see
}