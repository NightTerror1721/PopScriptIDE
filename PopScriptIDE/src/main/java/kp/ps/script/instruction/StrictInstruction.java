package kp.ps.script.instruction;

import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.parser.CodeParser;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class StrictInstruction extends Instruction
{
    private StrictInstruction(int firstLine, int lastLine)
    {
        super(firstLine, lastLine);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use 'strict' command in non static environment.");
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use 'strict' command in non static environment.");
    }

    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        //checkStrictState(state);
    }

    @Override
    public final boolean hasYieldInstruction() { return false; }
    
    @Override
    public final boolean isStrictInstruction() { return true; }
    
    public static final StrictInstruction parse(CompilerState state, CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        parser.checkEmptyStatement(reader, errors);
        int last = reader.getCurrentLine();
        
        checkStrictState(state);
        state.enableStrictMode();
        
        return new StrictInstruction(first, last);
    }
    
    private static void checkStrictState(CompilerState state) throws CompilerException
    {
        if(state.isStrictModeEnabled())
            throw new CompilerException("'strict' command alredy used after.");
    }
}
