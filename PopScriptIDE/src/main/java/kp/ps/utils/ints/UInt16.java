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
public final class UInt16 extends AbstractInt<UInt16>
{
    public static final UInt16 ZERO = new UInt16(0);
    public static final UInt16 ONE = new UInt16(1);
    
    public static final UInt16 MAX = new UInt16(0xffff);
    public static final UInt16 MIN = new UInt16(0);
    
    public static final int BYTES = Short.BYTES;
    
    public UInt16(int value) { super(Utils.clamp(value, 0, 0xffff)); }
    
    
    @Override
    protected final UInt16 instance(int value) { return new UInt16(value); }
    
    
    public static final UInt16 valueOf(byte value) { return new UInt16(value); }
    public static final UInt16 valueOf(short value) { return new UInt16(value); }
    public static final UInt16 valueOf(int value) { return new UInt16(value); }
    public static final UInt16 valueOf(long value) { return new UInt16((int) value); }
    public static final UInt16 valueOf(IntegerValue<?> value) { return new UInt16(value.toInt()); }
    
    
    public static final void write(byte[] buffer, int offset, UInt16 value) { IOUtils.writeUnsignedInt16(buffer, offset, value.value); }
    public static final UInt16 read(byte[] buffer, int offset) { return new UInt16(IOUtils.readUnsignedInt16(buffer, offset)); }
    
    public static final void write(OutputStream os, UInt16 value) throws IOException { IOUtils.writeUnsignedInt16(os, value.value); }
    public static final UInt16 read(InputStream is, int offset) throws IOException { return new UInt16(IOUtils.readUnsignedInt16(is)); }
}
