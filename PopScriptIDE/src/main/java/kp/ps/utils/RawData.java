/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import kp.ps.utils.ints.Int16;
import kp.ps.utils.ints.Int32;
import kp.ps.utils.ints.Int64;
import kp.ps.utils.ints.Int8;
import kp.ps.utils.ints.UInt16;
import kp.ps.utils.ints.UInt32;
import kp.ps.utils.ints.UInt8;

/**
 *
 * @author Marc
 */
public class RawData
{
    private final byte[] data;
    
    public RawData(int length)
    {
        this.data = new byte[length];
    }
    
    public final int size() { return data.length; }
    
    private void checkRange(int index, int size)
    {
        if(index < 0 || index + size > data.length)
            throw new IndexOutOfBoundsException();
    }
    
    public final void writeSignedInt8(int index, Int8 value)
    {
        checkRange(index, Int8.BYTES);
        Int8.write(data, index, value);
    }
    public final void writeUnsignedInt8(int index, UInt8 value)
    {
        checkRange(index, UInt8.BYTES);
        UInt8.write(data, index, value);
    }
    public final void writeSignedInt16(int index, Int16 value)
    {
        checkRange(index, Int16.BYTES);
        Int16.write(data, index, value);
    }
    public final void writeUnsignedInt16(int index, UInt16 value)
    {
        checkRange(index, UInt16.BYTES);
        UInt16.write(data, index, value);
    }
    public final void writeSignedInt32(int index, Int32 value)
    {
        checkRange(index, Int32.BYTES);
        Int32.write(data, index, value);
    }
    public final void writeUnsignedInt32(int index, UInt32 value)
    {
        checkRange(index, UInt32.BYTES);
        UInt32.write(data, index, value);
    }
    public final void writeSignedInt64(int index, Int64 value)
    {
        checkRange(index, Int64.BYTES);
        Int64.write(data, index, value);
    }
    
    public final Int8 readSignedInt8(int index)
    {
        checkRange(index, Int8.BYTES);
        return Int8.read(data, index);
    }
    public final UInt8 readUnsignedInt8(int index)
    {
        checkRange(index, UInt8.BYTES);
        return UInt8.read(data, index);
    }
    public final Int16 readSignedInt16(int index)
    {
        checkRange(index, Int16.BYTES);
        return Int16.read(data, index);
    }
    public final UInt16 readUnsignedInt16(int index)
    {
        checkRange(index, UInt16.BYTES);
        return UInt16.read(data, index);
    }
    public final Int32 readSignedInt32(int index)
    {
        checkRange(index, Int32.BYTES);
        return Int32.read(data, index);
    }
    public final UInt32 readUnsignedInt32(int index)
    {
        checkRange(index, UInt32.BYTES);
        return UInt32.read(data, index);
    }
    public final Int64 readSignedInt64(int index)
    {
        checkRange(index, Int64.BYTES);
        return Int64.read(data, index);
    }
    
    
    public final void writeTo(OutputStream os, int offset, int len) throws IOException
    {
        os.write(data, offset, Utils.clamp(len, 0, (data.length - offset)));
    }
    public final void writeTo(OutputStream os) throws IOException
    {
        os.write(data, 0, data.length);
    }
    
    public final void readFrom(InputStream is, int offset, int len) throws IOException
    {
        IOUtils.readFully(is, data, offset, Utils.clamp(len, 0, (data.length - offset)));
    }
    public final void readFrom(InputStream is) throws IOException
    {
        IOUtils.readFully(is, data);
    }
    
    
    public final void fill(int offset, int len, int value)
    {
        offset = Utils.clamp(offset, 0, data.length - 1);
        len = Utils.clamp(len, 0, data.length);
        for(; offset < len; ++offset)
            data[offset] = (byte) value;
    }
    public final void fill(int value) { fill(0, data.length, value); }
    
    
    public final RawData copy()
    {
        RawData rd = new RawData(data.length);
        System.arraycopy(data, 0, rd.data, 0, data.length);
        return rd;
    }
    
    public final void copyFrom(RawData other)
    {
        int len = Math.min(data.length, other.data.length);
        System.arraycopy(other.data, 0, data, 0, len);
    }
}
