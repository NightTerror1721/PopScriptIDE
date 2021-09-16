/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Operation;
import kp.ps.script.parser.Separator;
import kp.ps.script.parser.Statement;
import kp.ps.script.parser.StatementParser;
import kp.ps.script.parser.Type;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class DeclarationInstruction extends Instruction
{
    private final TypeModifier modifier;
    private final TypeId type;
    private final Statement[] statements;
    
    private DeclarationInstruction(int firstLine, int lastLine, TypeModifier modifier, TypeId type, Statement[] statements) throws CompilerException
    {
        super(firstLine, lastLine);
        
        this.modifier = Objects.requireNonNull(modifier);
        this.type = Objects.requireNonNull(type);
        this.statements = Objects.requireNonNull(statements);
        
        for(int i = 0; i < statements.length; ++i)
        {
            if(statements[i] == null)
                throw new IllegalStateException();
            if((!statements[i].isAssignmentOperation() && !statements[i].isIdentifier()))
                throw new CompilerException("Expected valid identifier or assignation in declaration statement. But found %s", statements[i]);
        }
        
        TypeId.check(modifier, type);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        for(Statement statement : statements)
        {
            if(statement.isIdentifier())
                createElement(state, statement, false);
            else
            {
                Operation op = (Operation) statement;
                createElement(state, op.getBinaryLeftOperand(), false);
                
                StatementTask task = StatementCompiler.toTask(state, statement);
                switch(modifier)
                {
                    case VAR:
                        task.varCompile(state, code);
                        break;
                        
                    case CONST:
                        task.constCompile();
                        break;
                        
                    case INTERNAL:
                        task.internalCompile();
                        break;
                }
            }
        }
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        if(modifier != TypeModifier.CONST)
            throw new CompilerException("Cannot declare non const elements in const environment.");
        
        for(Statement statement : statements)
        {
            if(statement.isIdentifier())
                createElement(state, statement, false);
            else
            {
                Operation op = (Operation) statement;
                createElement(state, op.getBinaryLeftOperand(), false);
                StatementCompiler.toTask(state, statement).constCompile();
            }
        }
    }

    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        if(modifier == TypeModifier.VAR)
            throw new CompilerException("Cannot declare vars out of any main, init or macro.");
        
        for(Statement statement : statements)
        {
            if(statement.isIdentifier())
                createElement(state, statement, true);
            else
            {
                Operation op = (Operation) statement;
                createElement(state, op.getBinaryLeftOperand(), true);
                
                StatementTask task = StatementCompiler.toTask(state, statement);
                if(modifier == TypeModifier.CONST)
                    task.constCompile();
                else task.internalCompile();
            }
        }
    }
    
    @Override
    public final boolean hasYieldInstruction() { return false; }
    
    private void createElement(CompilerState state, Statement statement, boolean isStatic) throws CompilerException
    {
        if(!statement.isIdentifier())
            throw new CompilerException("Expected valid identifier in declaration statement.");
        
        switch(modifier)
        {
            case VAR:
                if(isStatic)
                    throw new IllegalStateException();
                state.getLocalElements().createVariable(statement.toString());
                break;
                
            case CONST:
                if(isStatic || !state.getNamespace().isGlobal())
                    state.getNamespace().addField(NamespaceField.constant(statement.toString()));
                else state.getLocalElements().createConstant(statement.toString());
                break;
                
            case INTERNAL:
                if(type == TypeId.INT)
                {
                    if(isStatic || !state.getNamespace().isGlobal())
                        state.getNamespace().addField(NamespaceField.internal(statement.toString()));
                    else state.getLocalElements().createInternal(statement.toString());
                }
                else
                {
                    if(isStatic || !state.getNamespace().isGlobal())
                        state.getNamespace().addField(NamespaceField.typedValue(statement.toString(), type));
                    else state.getLocalElements().createTypedValue(statement.toString(), type);
                }
                break;
                
            default:
                throw new IllegalStateException();
        }
    }
    
    public static final DeclarationInstruction parse(CodeReader reader, CodeParser parser, Type type, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList list = parser.parseInlineInstructionAsList(reader, type, errors);
        int last = reader.getCurrentLine();
        if(list.isEmpty())
            throw new CompilerException("Expected valid identifier after %s.", type);
        
        FragmentList[] parts = list.split(Separator.COMMA);
        Statement[] statements = new Statement[parts.length];
        
        for(int i = 0; i < statements.length; ++i)
        {
            if(parts[i] == null || parts[i].isEmpty())
                throw new CompilerException("Expected valid identifier after %s.", type);
            
            statements[i] = StatementParser.parse(parts[i]);
        }
        
        return new DeclarationInstruction(first, last, type.getModifier(), type.getTypeId(), statements);
    }
}
