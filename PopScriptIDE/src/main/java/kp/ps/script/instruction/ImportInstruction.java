/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Separator;
import kp.ps.script.parser.StringLiteral;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class ImportInstruction extends Instruction
{
    private final Path[] paths;
    
    private ImportInstruction(int firstLine, int lastLine, Path[] paths)
    {
        super(firstLine, lastLine);
        this.paths = Objects.requireNonNull(paths);
    }
    
    @Override
    void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use 'import' command in non static environment.");
    }

    @Override
    void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use 'import' command in non static environment.");
    }

    @Override
    void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        for(Path path : paths)
        {
            state.pushSourceFile(path);

            CodeReader source;
            try(InputStream input = Files.newInputStream(path))
            {
                source = new CodeReader(input);
            }
            catch(IOException ex)
            {
                throw new CompilerException("Cannot import '%s'. %s", path.toAbsolutePath(), ex.getMessage());
            }

            List<Instruction> insts = InstructionParser.parse(state, source, false, state.getErrors());
            InstructionCompiler.staticCompile(state, initCode, mainCode, insts);

            state.popSourceFile();
        }
    }

    @Override
    public boolean hasYieldInstruction() { return false; }
    
    public static final ImportInstruction parse(CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList frags = parser.parseInlineInstructionAsList(reader, Command.fromId(CommandId.IMPORT), errors);
        int last = reader.getCurrentLine();
        
        LinkedList<Path> paths = new LinkedList<>();
        FragmentList[] parts = frags.split(Separator.COMMA);
        for(FragmentList part : parts)
        {
            if(part.size() != 1)
                throw new CompilerException("Malformed file import path. Expected <\"path\">.");
            if(!part.get(0).isStringLiteral())
                throw new CompilerException("Malformed file import path. Expected <\"path\">, but found '%s'.", part.get(0));
            
            String string = part.<StringLiteral>get(0).getString();
            if(string != null && !string.isEmpty())
                paths.add(Paths.get(string));
        }
        
        return new ImportInstruction(first, last, paths.toArray(new Path[paths.size()]));
    }
    
}
