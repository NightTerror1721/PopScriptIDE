/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.Separator;
import kp.ps.script.parser.Type;
import kp.ps.utils.CodeReader;

/**
 *
 * @author mpasc
 */
public final class InstructionParser
{
    private InstructionParser() {}
    
    public static final List<Instruction> parse(CodeReader source, boolean singleInstruction, ErrorList errors)
    {
        if(singleInstruction)
        {
            Instruction inst = parse(source, null, errors);
            if(inst == null)
                return Collections.emptyList();
            return Collections.singletonList(inst);
        }
        
        LinkedList<Instruction> insts = new LinkedList<>();
        while(source.hasNext())
        {
            Instruction inst = parse(source, insts.isEmpty() ? null : insts.getLast(), errors);
            if(inst != null)
                insts.add(inst);
        }
        
        return insts;
    }
    
    private static Instruction parse(CodeReader source, Instruction prev, ErrorList errors)
    {
        int firstLine = source.getCurrentLine();
        try
        {
            CodeParser parser = new CodeParser();
            Fragment firstFrag = parser.parseFragment(source, true, errors);
            
            if(firstFrag == null)
                return null;
            
            if(firstFrag.isCommand())
            {
                return parseCommand(source, (Command) firstFrag, prev, errors);
            }
            else if(firstFrag.isType())
            {
                return DeclarationInstruction.parse(source, (Type) firstFrag, errors);
            }
            else if(firstFrag.isStatement())
            {
                return StatementInstruction.parse(source, errors, firstFrag);
            }
            else if(firstFrag.equals(Separator.SEMI_COLON))
            {
                return null;
            }
            else throw new CompilerException("Unexpected element '%s'.", firstFrag);
        }
        catch(CompilerException ex)
        {
            int lastLine = source.getCurrentLine();
            errors.addError(firstLine, lastLine, ex);
            return null;
        }
    }
    
    private static Instruction parseCommand(CodeReader source, Command command, Instruction prev, ErrorList errors) throws CompilerException
    {
        switch(command.getCommandId())
        {
            case IF:
                return ConditionalInstruction.parse(source, prev, false, errors);
                
            case ELSE:
                return ConditionalInstruction.parse(source, prev, true, errors);
                
            case EVERY:
                return EveryInstruction.parse(source, errors);
                
            case MAIN:
                return MainInitInstruction.parse(source, true, errors);
                
            case INIT:
                return MainInitInstruction.parse(source, false, errors);
                
            case NAMESPACE:
                return NamespaceInstruction.parse(source, errors);
        }
        
        throw new CompilerException("Unimpletented command '%s'.", command);
    }
}
