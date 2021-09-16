/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import java.util.Arrays;
import java.util.Iterator;

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
    
    public static final <T> T self(T value) { return value; }
    
    public static final <L, R> ZippedIterator<L, R> iteratorOf(Iterator<L> left, Iterator<R> right)
    {
        return new ZippedIterator<>(left, right);
    }
    
    public static final <L, R> ZippedIterator<L, R> iteratorOf(Iterable<L> left, Iterable<R> right)
    {
        return new ZippedIterator<>(left.iterator(), right.iterator());
    }
    
    public static final <L, R> Iterable<Pair<L, R>> iterableOf(Iterator<L> left, Iterator<R> right)
    {
        return () -> new ZippedIterator(left, right);
    }
    
    public static final <L, R> Iterable<Pair<L, R>> iterableOf(Iterable<L> left, Iterable<R> right)
    {
        return () -> new ZippedIterator(left.iterator(), right.iterator());
    }
    
    public static final String stringDup(char character, int count)
    {
        if(count < 1)
            return "";
        
        if(count == 1)
            return Character.toString(character);
        
        char[] buf = new char[count];
        Arrays.fill(buf, character);
        return new String(buf);
    }
}
