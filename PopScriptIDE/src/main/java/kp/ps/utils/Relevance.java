/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

/**
 *
 * @author Marc
 */
public enum Relevance
{
    DEFAULT,
    NAMESPACE,
    FUNCTION,
    MACRO,
    FIELD;
    
    private static final Relevance[] VALUES = values();
    public static final int count() { return VALUES.length; }
}
