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
public abstract class AbstractShort<T> extends IntegerValue<AbstractShort<T>>
{
    protected final short value;
    
    protected AbstractShort(short value)
    {
        this.value = value;
    }
    
    @Override
    public byte toByte() { return (byte) value; }

    @Override
    public short toShort() { return value; }

    @Override
    public int toInt() { return value; }

    @Override
    public long toLong() { return value; }
    
    @Override
    public AbstractShort<T> clamp(AbstractShort<T> min, AbstractShort<T> max) { return instance(Utils.clamp(value, min.value, max.value)); }

    @Override
    public AbstractShort<T> changeBitState(int index, boolean state) { return instance(bitstate(value, index, state)); }

    @Override
    public boolean getBitState(int index) { return bitstate(value, index); }

    @Override
    public boolean equals(AbstractShort<T> o) { return value == o.value; }

    @Override
    public int hashCode() { return Short.hashCode(value); }

    @Override
    public String toString() { return Short.toString(value); }

    @Override
    public int compareTo(AbstractShort<T> o) { return Short.compare(value, o.value); }
    
    
    protected abstract AbstractShort<T> instance(short value);
}
