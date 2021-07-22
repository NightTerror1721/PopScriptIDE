/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.Arrays;
import kp.ps.script.compiler.TypeId;
import kp.ps.script.instruction.Instruction;
import kp.ps.script.parser.ArgumentList;
import kp.ps.script.parser.Scope;
import kp.ps.script.parser.args.Argument;

/**
 *
 * @author Marc
 */
public class Macro
{
    private final Parameter[] params;
    private final Instruction[] instructions;
    
    public Macro(ArgumentList args, Scope scope)
    {
        this.params = args.stream()
                .map(Parameter::new)
                .toArray(Parameter[]::new);
        this.instructions = scope.getInstructions();
    }
    
    public final int getParameterCount() { return params.length; }
    public final Parameter getParameter(int index) { return params[index]; }
    
    public final Instruction[] getInstructions() { return Arrays.copyOf(instructions, instructions.length); }
    
    public final class Parameter
    {
        private final TypeId type;
        private final String name;
        
        private Parameter(Argument arg)
        {
            if(!arg.isDeclarationArgument())
                throw new IllegalStateException();
            
            this.type = arg.getDeclarationType().getTypeId();
            this.name = arg.getDeclarationIdentifier().getIdentifier();
        }
        
        public final TypeId getType() { return type; }
        public final String getName() { return name; }
    }
}
