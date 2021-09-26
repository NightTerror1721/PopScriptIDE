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
import kp.ps.script.compiler.CompilerState;
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
    
    public static final List<Instruction> parse(CompilerState state, CodeReader source, boolean singleInstruction, ErrorList errors)
    {
        CodeParser parser = new CodeParser(state);
        
        if(singleInstruction)
        {
            Instruction inst = parse(state, source, parser, null, errors);
            if(inst == null)
                return Collections.emptyList();
            return Collections.singletonList(inst);
        }
        
        LinkedList<Instruction> insts = new LinkedList<>();
        while(source.hasNext())
        {
            Instruction inst = parse(state, source, parser, insts.isEmpty() ? null : insts.getLast(), errors);
            if(inst != null)
                insts.add(inst);
        }
        
        return insts;
    }
    
    private static Instruction parse(CompilerState state, CodeReader source, CodeParser parser, Instruction prev, ErrorList errors)
    {
        parser.clearStoredComment();
        int firstLine = source.getCurrentLine();
        try
        {
            Fragment firstFrag = parser.parseFragment(source, true, errors);
            
            if(firstFrag == null)
                return null;
            
            if(firstFrag.isCommand())
            {
                return parseCommand(state, source, parser, (Command) firstFrag, prev, errors);
            }
            else if(firstFrag.isType())
            {
                return DeclarationInstruction.parse(source, parser, (Type) firstFrag, errors);
            }
            else if(firstFrag.isStatement())
            {
                return StatementInstruction.parse(source, parser, errors, firstFrag);
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
            errors.addError(state.getCurrentSourceFile(), firstLine, lastLine, ex);
            return null;
        }
    }
    
    private static Instruction parseCommand(
            CompilerState state,
            CodeReader source,
            CodeParser parser,
            Command command,
            Instruction prev,
            ErrorList errors) throws CompilerException
    {
        switch(command.getCommandId())
        {
            case IF:
                return ConditionalInstruction.parse(source, parser, prev, false, errors);
                
            case ELSE:
                return ConditionalInstruction.parse(source, parser, prev, true, errors);
                
            case EVERY:
                return EveryInstruction.parse(source, parser, errors);
                
            case MAIN:
                return MainInitInstruction.parse(source, parser, true, errors);
                
            case INIT:
                return MainInitInstruction.parse(source, parser, false, errors);
                
            case NAMESPACE:
                return NamespaceInstruction.parse(source, parser, errors);
                
            case MACRO:
                return MacroDeclarationInstruction.parse(state, source, parser, errors);
                
            case YIELD:
                return YieldInstruction.parse(source, parser, errors);
                
            case IMPORT:
                return ImportInstruction.parse(source, parser, errors);
        }
        
        throw new CompilerException("Unimpletented command '%s'.", command);
    }
}
