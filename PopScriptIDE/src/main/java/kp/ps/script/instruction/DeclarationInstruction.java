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
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Operation;
import kp.ps.script.parser.Operator;
import kp.ps.script.parser.OperatorId;
import kp.ps.script.parser.Separator;
import kp.ps.script.parser.Statement;
import kp.ps.script.parser.StatementParser;
import kp.ps.script.parser.Type;
import kp.ps.utils.CodeReader;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class DeclarationInstruction extends Instruction
{
    private final TypeModifier modifier;
    private final TypeId type;
    private final Statement[] statements;
    private final boolean global;
    private final boolean init;
    
    private DeclarationInstruction(
            int firstLine,
            int lastLine,
            TypeModifier modifier,
            TypeId type,
            Statement[] statements,
            boolean isGlobal,
            boolean initMode) throws CompilerException
    {
        super(firstLine, lastLine);
        
        this.modifier = Objects.requireNonNull(modifier);
        this.type = Objects.requireNonNull(type);
        this.statements = Objects.requireNonNull(statements);
        this.global = isGlobal;
        this.init = initMode;
        
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
        if(init && state.isStrictModeEnabled())
            throw new CompilerException("Cannot use 'global init' command in 'strict' mode.");
        
        for(Statement statement : statements)
        {
            if(statement.isIdentifier())
            {
                createElement(state, statement, false);
                if(global && init)
                    compileGlobalInit(state, statement, Int32.ZERO);
            }
            else
            {
                Operation op = (Operation) statement;
                createElement(state, op.getBinaryLeftOperand(), false);
                
                StatementTask task = StatementCompiler.toTask(state, statement);
                switch(modifier)
                {
                    case VAR:
                        if(global && init)
                            compileGlobalInit(state, op);
                        else task.varCompile(state, code);
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
        if(global)
            throw new CompilerException("Cannot declare global vars in const environment.");
        
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
        if(global)
            throw new CompilerException("Cannot declare global vars out of any main, init or macro.");
        
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
                if(global)
                    state.getLocalElements().createGlobalVariable(statement.toString());
                else state.getLocalElements().createVariable(statement.toString());
                break;
                
            case CONST:
                if(global)
                    throw new IllegalStateException();
                
                if(isStatic || !state.getNamespace().isGlobal())
                    state.getNamespace().addField(NamespaceField.constant(statement.toString()));
                else state.getLocalElements().createConstant(statement.toString());
                break;
                
            case INTERNAL:
                if(global)
                    throw new IllegalStateException();
                
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
    
    private void compileGlobalInit(CompilerState state, Operation op) throws CompilerException
    {
        if(!op.getOperator().equals(Operator.fromId(OperatorId.ASSIGNATION)))
            throw new CompilerException("Cannot use '%s' operator in 'global init' assignment. Can only use '='.", op.getOperator());
        
        Int32 initValue = StatementCompiler.toTask(state, op.getBinaryRightOperand()).constCompile().getConstantValue();
        compileGlobalInit(state, op.getBinaryLeftOperand(), initValue);
    }
    
    private void compileGlobalInit(CompilerState state, Statement identifier, Int32 initValue) throws CompilerException
    {
        Element var = state.getLocalElements().get(identifier.toString());
        if(!var.isVariable())
            throw new IllegalStateException();
        
        var.initGlobalVariable(initValue);
    }
    
    public static final DeclarationInstruction parse(CodeReader reader, CodeParser parser, Type type, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList list = parser.parseInlineInstructionAsList(reader, type, errors);
        int last = reader.getCurrentLine();
        if(list.isEmpty())
            throw new CompilerException("Expected valid identifier after %s.", type);
        
        Statement[] statements = parseStatements(list, type);
        return new DeclarationInstruction(first, last, type.getModifier(), type.getTypeId(), statements, false, false);
    }
    
    public static final DeclarationInstruction parseGlobal(CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList list = parser.parseInlineInstructionAsList(reader, Command.fromId(CommandId.GLOBAL), errors);
        int last = reader.getCurrentLine();
        if(list.isEmpty())
            throw new CompilerException("Expected valid type or 'init' command after %s.", Command.fromId(CommandId.GLOBAL));
        
        boolean initMode = false;
        if(list.get(0).equals(Command.fromId(CommandId.INIT)))
        {
            if(list.size() == 1)
                throw new CompilerException("Expected valid type or 'init' command after %s.", Command.fromId(CommandId.INIT));
            list = list.subList(1);
            initMode = true;
        }
        
        Type type;
        if(list.get(0).isType())
        {
            type = list.get(0);
            if(list.size() == 1)
                throw new CompilerException("Expected valid identifier after %s.", type);
            list = list.subList(1);
        }
        else
        {
            type = Type.fromId(TypeId.INT);
            type.insertModifier(TypeModifier.VAR);
        }
        
        Statement[] statements = parseStatements(list, type);
        return new DeclarationInstruction(first, last, type.getModifier(), type.getTypeId(), statements, true, initMode);
    }
    
    private static Statement[] parseStatements(FragmentList list, Type type) throws CompilerException
    {
        FragmentList[] parts = list.split(Separator.COMMA);
        Statement[] statements = new Statement[parts.length];
        
        for(int i = 0; i < statements.length; ++i)
        {
            if(parts[i] == null || parts[i].isEmpty())
                throw new CompilerException("Expected valid identifier after %s.", type);
            
            statements[i] = StatementParser.parse(parts[i]);
        }
        
        return statements;
    }
}
