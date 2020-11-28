/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Asus
 */
public final class HexadecimalDecoder
{
    private HexadecimalDecoder() {}
    
    private static final int CHAR_NUMBER_ZER0 = '0';
    private static final int CHAR_NUMBER_NINE = '9';
    private static final int CHAR_NUMBER_LOWER_A = 'a';
    private static final int CHAR_NUMBER_UPPER_A = 'A';
    private static final int CHAR_NUMBER_LOWER_F = 'f';
    private static final int CHAR_NUMBER_UPPER_F = 'F';
    
    public static final int decode(char c) throws CompilerException
    {
        int code = c;
        if(code <= CHAR_NUMBER_NINE)
        {
            if(code < CHAR_NUMBER_ZER0)
                throw new CompilerException("Invalid hexadecimal value: " + c);
            return code - CHAR_NUMBER_ZER0;
        }
        if(code <= CHAR_NUMBER_UPPER_F)
        {
            if(code < CHAR_NUMBER_UPPER_A)
                throw new CompilerException("Invalid hexadecimal value: " + c);
            return code - CHAR_NUMBER_UPPER_A + 0xA;
        }
        if(code <= CHAR_NUMBER_LOWER_F)
        {
            if(code < CHAR_NUMBER_LOWER_A)
                throw new CompilerException("Invalid hexadecimal value: " + c);
            return code - CHAR_NUMBER_LOWER_A + 0xA;
        }
        throw new CompilerException("Invalid hexadecimal value: " + c);
    }
    
    public static final char decodeUnicode(String str) throws CompilerException
    {
        if(str.length() != 4)
            throw new IllegalArgumentException();
        
        return (char) ((decode(str.charAt(0)) << 12) +
                (decode(str.charAt(1)) << 8) +
                (decode(str.charAt(2)) << 4) +
                (decode(str.charAt(3))));
    }
}
