/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

/**
 *
 * @author Marc
 */
public final class Utils
{
    private Utils() {}
    
    public static byte clamp(byte value, int min, int max)
    {
        return (byte) Math.max(min, Math.min(max, value));
    }
    
    public static short clamp(short value, int min, int max)
    {
        return (short) Math.max(min, Math.min(max, value));
    }
    
    public static int clamp(int value, int min, int max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static long clamp(long value, long min, long max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static float clamp(float value, float min, float max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }
}
