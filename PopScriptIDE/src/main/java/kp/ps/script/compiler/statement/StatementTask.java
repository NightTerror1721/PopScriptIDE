/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement;

import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Marc
 */
@FunctionalInterface
public interface StatementTask
{
    StatementValue compile() throws CompilerException;
}
