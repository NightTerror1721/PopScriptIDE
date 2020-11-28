/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils.ints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import kp.ps.utils.IOUtils;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public final class Int8 extends IntegerValue<Int8>
{
    public static final Int8 ZERO = new Int8((byte) 0);
    public static final Int8 ONE = new Int8((byte) 1);
    public static final Int8 MINUSONE = new Int8((byte) -1);
    
    public static final Int8 MIN = new Int8(Byte.MAX_VALUE);
    public static final Int8 MAX = new Int8(Byte.MIN_VALUE);
    
    public static final int BYTES = Byte.BYTES;
    
    private final byte value;
    
    public Int8(byte value) { this.value = value; }
    
    @Override
    public byte toByte() { return value; }

    @Override
    public short toShort() { return value; }

    @Override
    public int toInt() { return value; }

    @Override
    public long toLong() { return value; }
    
    @Override
    public Int8 clamp(Int8 min, Int8 max) { return new Int8(Utils.clamp(value, min.value, max.value)); }

    @Override
    public Int8 changeBitState(int index, boolean state) { return new Int8(bitstate(value, index, state)); }

    @Override
    public boolean getBitState(int index) { return bitstate(value, index); }

    @Override
    public boolean equals(Int8 o) { return value == o.value; }

    @Override
    public int hashCode() { return Byte.hashCode(value); }

    @Override
    public String toString() { return Byte.toString(value); }

    @Override
    public int compareTo(Int8 o) { return Byte.compare(value, o.value); }
    
    
    public static final Int8 valueOf(byte value) { return new Int8(value); }
    public static final Int8 valueOf(short value) { return new Int8((byte) value); }
    public static final Int8 valueOf(int value) { return new Int8((byte) value); }
    public static final Int8 valueOf(long value) { return new Int8((byte) value); }
    public static final Int8 valueOf(IntegerValue<?> value) { return new Int8(value.toByte()); }
    
    
    public static void write(byte[] buffer, int offset, Int8 value) { IOUtils.writeSignedByte(buffer, offset, value.value); }
    public static Int8 read(byte[] buffer, int offset) { return new Int8(IOUtils.readSignedByte(buffer, offset)); }
    
    public static void write(OutputStream os, Int8 value) throws IOException { IOUtils.writeSignedByte(os, value.value); }
    public static Int8 read(InputStream is) throws IOException { return new Int8(IOUtils.readSignedByte(is)); }
}
