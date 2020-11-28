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
public abstract class AbstractLong<T> extends IntegerValue<AbstractLong<T>>
{
    protected final long value;
    
    protected AbstractLong(long value)
    {
        this.value = value;
    }
    
    @Override
    public byte toByte() { return (byte) value; }

    @Override
    public short toShort() { return (short) value; }

    @Override
    public int toInt() { return (int) value; }

    @Override
    public long toLong() { return value; }
    
    @Override
    public AbstractLong<T> clamp(AbstractLong<T> min, AbstractLong<T> max) { return instance(Utils.clamp(value, min.value, max.value)); }

    @Override
    public AbstractLong<T> changeBitState(int index, boolean state) { return instance(bitstate(value, index, state)); }

    @Override
    public boolean getBitState(int index) { return bitstate(value, index); }

    @Override
    public boolean equals(AbstractLong<T> o) { return value == o.value; }

    @Override
    public int hashCode() { return Long.hashCode(value); }

    @Override
    public String toString() { return Long.toString(value); }

    @Override
    public int compareTo(AbstractLong<T> o) { return Long.compare(value, o.value); }
    
    
    protected abstract AbstractLong<T> instance(long value);
}
