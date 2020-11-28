/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils.ints;

import kp.ps.utils.Utils;
import static kp.ps.utils.ints.IntegerValue.bitstate;

/**
 *
 * @author Marc  
 */
public abstract class AbstractInt<T> extends IntegerValue<AbstractInt<T>>
{
    protected final int value;
    
    protected AbstractInt(int value)
    {
        this.value = value;
    }
    
    @Override
    public byte toByte() { return (byte) value; }

    @Override
    public short toShort() { return (short) value; }

    @Override
    public int toInt() { return value; }

    @Override
    public long toLong() { return value; }
    
    @Override
    public AbstractInt<T> clamp(AbstractInt<T> min, AbstractInt<T> max) { return instance(Utils.clamp(value, min.value, max.value)); }

    @Override
    public AbstractInt<T> changeBitState(int index, boolean state) { return instance(bitstate(value, index, state)); }

    @Override
    public boolean getBitState(int index) { return bitstate(value, index); }

    @Override
    public boolean equals(AbstractInt<T> o) { return value == o.value; }

    @Override
    public int hashCode() { return Integer.hashCode(value); }

    @Override
    public String toString() { return Integer.toString(value); }

    @Override
    public int compareTo(AbstractInt<T> o) { return Integer.compare(value, o.value); }
    
    
    protected abstract AbstractInt<T> instance(int value);
}
