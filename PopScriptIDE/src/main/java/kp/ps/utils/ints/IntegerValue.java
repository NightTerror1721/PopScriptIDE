/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils.ints;

/**
 *
 * @author Marc
 * @param <T>
 */
public abstract class IntegerValue<T extends IntegerValue<T>> implements Comparable<T>
{
    public abstract byte toByte();
    public abstract short toShort();
    public abstract int toInt();
    public abstract long toLong();
    
    public abstract T clamp(T min, T max);
    
    public abstract T changeBitState(int index, boolean state);
    public abstract boolean getBitState(int index);
    
    public abstract boolean equals(T o);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(getClass().isInstance(o))
            return equals((T) o);
        return false;
    }
    
    private static void checkRange(int value, int min, int max)
    {
        if(value < min || value > max)
            throw new IndexOutOfBoundsException();
    }
    
    private static int clear(byte base, int index)
    {
        checkRange(index, 0, 7);
        return (byte) ((base & 0xff) & (~(0x1 << index)));
    }
    private static int clear(short base, int index)
    {
        checkRange(index, 0, 15);
        return (short) ((base & 0xffff) & (~(0x1 << index)));
    }
    private static int clear(int base, int index)
    {
        checkRange(index, 0, 31);
        return base & ~(0x1 << index);
    }
    private static long clear(long base, int index)
    {
        checkRange(index, 0, 63);
       return base & ~(0x1L << index);
    }
    
    private static int bit32(int index, boolean state)
    {
        checkRange(index, 0, 31);
        return state ? (0x1 << index) : 0;
    }
    
    private static long bit64(int index, boolean state)
    {
        checkRange(index, 0, 63);
        return state ? (0x1L << index) : 0;
    }
    
    
    
    static byte bitstate(byte base, int index, boolean state)
    {
        return (byte) ((clear(base, index) | bit32(index, state)) & 0xff);
    }
    
    static short bitstate(short base, int index, boolean state)
    {
        return (short) ((clear(base, index) | bit32(index, state)) & 0xffff);
    }
    
    static int bitstate(int base, int index, boolean state)
    {
        return clear(base, index) | bit32(index, state);
    }
    
    static long bitstate(long base, int index, boolean state)
    {
        return clear(base, index) | bit64(index, state);
    }
    
    
    static boolean bitstate(byte base, int index)
    {
        return (base & bit32(index, true)) != 0;
    }
    static boolean bitstate(short base, int index)
    {
        return (base & bit32(index, true)) != 0;
    }
    static boolean bitstate(int base, int index)
    {
        return (base & bit32(index, true)) != 0;
    }
    static boolean bitstate(long base, int index)
    {
        return (base & bit64(index, true)) != 0;
    }
}
