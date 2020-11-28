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
public final class UInt8 extends AbstractShort<UInt8>
{
    public static final UInt8 ZERO = new UInt8((short) 0);
    public static final UInt8 ONE = new UInt8((short) 1);
    
    public static final UInt8 MAX = new UInt8((short) 0xff);
    public static final UInt8 MIN = new UInt8((short) 0);
    
    public static final int BYTES = Byte.BYTES;
    
    public UInt8(short value) { super(Utils.clamp(value, 0, 0xff)); }
    
    
    @Override
    protected final UInt8 instance(short value) { return new UInt8(value); }
    
    
    public static final UInt8 valueOf(byte value) { return new UInt8(value); }
    public static final UInt8 valueOf(short value) { return new UInt8(value); }
    public static final UInt8 valueOf(int value) { return new UInt8((short) value); }
    public static final UInt8 valueOf(long value) { return new UInt8((short) value); }
    public static final UInt8 valueOf(IntegerValue<?> value) { return new UInt8(value.toShort()); }
    
    
    public static final void write(byte[] buffer, int offset, UInt8 value) { IOUtils.writeUnsignedByte(buffer, offset, value.value); }
    public static final UInt8 read(byte[] buffer, int offset) { return new UInt8(IOUtils.readUnsignedByte(buffer, offset)); }
    
    public static final void write(OutputStream os, UInt8 value) throws IOException { IOUtils.writeUnsignedByte(os, value.value); }
    public static final UInt8 read(InputStream is, int offset) throws IOException { return new UInt8(IOUtils.readUnsignedByte(is)); }
}
