/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.io.PrintWriter;

/**
 *
 * @author Marc
 */
public interface Element
{
    void decompile(PrintWriter output, int identation);
}
