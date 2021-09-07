/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps;

import java.util.List;
import kp.ps.script.compiler.functions.actions.InnerFunction;
import kp.ps.script.compiler.functions.actions.InnerFunctionPool;

/**
 *
 * @author Marc
 */
public class Main
{
    public static void main(String[] args)
    {
        List<InnerFunction> actions = InnerFunctionPool.list();
        for(InnerFunction func : actions)
            System.out.println(func);
    }
}
