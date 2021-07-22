/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import kp.ps.script.compiler.CompilerException;
import kp.ps.script.parser.FragmentList.Pointer;

/**
 *
 * @author Marc
 */
public final class StatementParser
{
    private StatementParser() {}
    
    public static final Statement parse(FragmentList list) throws CompilerException
    {
        Pointer it = list.createPointer();
        Statement operand = packPart(it);
        if(it.end())
            return operand;
        return packOperation(it, operand);
    }
    
    private static Statement packPart(Pointer it) throws CompilerException
    {
        if(it.end())
            throw new CompilerException("unexpected end of instruction");
        return packPostUnary(it, packPreUnary(it));
    }
    
    private static Statement packPreUnary(Pointer it) throws CompilerException
    {
        Fragment part = it.value();
        it.increase();
        if(part.isOperator())
        {
            if(it.end())
                throw new CompilerException("unexpected end of instruction");
            Operator prefix = (Operator) part;
            if(!prefix.isPrefixUnary())
                throw new CompilerException("Operator " + prefix + " cannot be a non unary prefix operator");
            
            part = packNextOperatorPart(it, prefix);
            if(!part.isStatement())
                throw new CompilerException("Expected valid operand. But found: " + part);
            
            return Operation.unary(prefix, (Statement) part);
        }
        if(!part.isStatement())
            throw new CompilerException("Expected valid operand. But found: " + part);
        
        return (Statement) part;
    }
    
    private static Statement packPostUnary(Pointer it, Statement pre) throws CompilerException
    {
        if(it.end())
            return pre;
        Fragment part = it.value();
        
        if(part.isOperator())
        {
            Operator sufix = (Operator) part;
            if(!sufix.isUnary())
                return pre;
            
            it.increase();
            if(sufix.hasRightToLeftOrder())
                throw new CompilerException("Operator " + sufix + " cannot be an unary sufix operator");
            
            if(!pre.isStatement())
                throw new CompilerException("Expected valid operand. But found: " + part);
            
            return packPostUnary(it, Operation.unary(sufix, pre));
        }
        return pre;
    }
    
    private static Operator findNextOperatorSymbol(FragmentList list, int index)
    {
        int len = list.size();
        for(int i = index; i < len; ++i)
            if(list.get(i).isOperator())
                return (Operator) list.get(i);
        return null;
    }
    
    private static Statement getSuperOperatorScope(Pointer it, Operator opBase) throws CompilerException
    {
        int start = it.index();
        for(; !it.end(); it.increase())
        {
            if(!it.value().isOperator())
                continue;
            
            Operator op = (Operator) it.value();
            if(opBase.hasPriorityOver(op))
            {
                //it.decrease();
                return parse(it.list().subList(start, it.index() - start));
            }
        }
        return parse(it.list().subList(start));
    }
    
    private static Statement packOperation(Pointer it, Statement operand1) throws CompilerException
    {
        if(!it.value().isOperator())
            throw new CompilerException("Expected a valid operator between operands. \"" + it.value() + "\"");
        
        Operator operator = (Operator) it.value();
        it.increase();
        Statement operation;
        
        if(operator.isTernary())
        {
            int start = it.index();
            int terOp = 0;
            for(; !it.end(); it.increase())
            {
                Fragment c = it.value();
                if(c instanceof Operator && ((Operator) c).getOperatorId() == OperatorId.ELVIS)
                    terOp++;
                else if(c == Separator.TWO_POINTS)
                {
                    if(terOp == 0)
                        break;
                    terOp--;
                }
            }
            if(it.end())
                throw new CompilerException("Expected a : in ternary operator");
            
            Statement response1 = parse(it.list().subList(start, it.index()  - start));
            it.increase();
            Statement response2 = parse(it.list().subList(it.index()));
            it.finish();
            return Operation.elvis(operand1, response1, response2);
        }
        else if(operator.isBinary())
        {
            Statement operand2 = packNextOperatorPart(it, operator);
            operation = Operation.binary(operator, operand1, operand2);
        }
        else if(operator.isAssignment())
        {
            Statement operand2 = packNextOperatorPart(it, operator);
            operation = Operation.assignment(operator, operand1, operand2);
        }
        else if(operator.isFunctionCall())
        {
            if(it.end())
                throw new CompilerException("Expected a valid arguments list in call operator.");
            
            Fragment args = it.value();
            it.increase();
            operation = Operation.functionCall(operand1, args);
        }
        else if(operator.isNamespaceResolution())
        {
            if(it.end())
                throw new CompilerException("Expected a valid identifier in property get operator.");
            
            Fragment identifier = it.value();
            it.increase();
            operation = Operation.namespaceResolver(operand1, identifier);
        }
        else throw new CompilerException("Invalid operator type: " + operator);
        
        
        if(it.end())
            return operation;
        return packOperation(it, operation);
    }
    
    private static Statement packNextOperatorPart(Pointer it, Operator operator) throws CompilerException
    {
        Operator nextOperator = findNextOperatorSymbol(it.list(), it.index());
        if(nextOperator != null && operator.comparePriorityTo(nextOperator) >= 0)
            nextOperator = null;

        Statement operand2;
        if(nextOperator != null)
            operand2 = getSuperOperatorScope(it, operator);
        else operand2 = packPart(it);
        
        if(operator.isNamespaceResolution() &&
                !operand2.isIdentifier())
            throw new CompilerException("Expected a valid identifier in namespace resolver operator: " + operand2);
        return operand2;
    }
    
    /*public static final Operation tryParseAssignmentNewFunction(FragmentList list) throws CompilerException
    {
        Pointer it = list.createPointer();
        if(it.end())
            return null;
        Fragment identifier = it.value();
        it.increase();
        if(identifier.isIdentifier())
        {
            if(it.end() || Operator.isOperator(it.value(), OperatorId.FUNCTION_CALL))
                return null;
            it.increase();
        }
        else return null;
        if(it.end())
            throw new CompilerException("Expected a valid arguments list in function declaration.");
        Fragment args = it.value();
        it.increase();
        if(it.end())
            throw new CompilerException("Expected a valid arguments list in function declaration.");
        Fragment scope = it.listValue();
        it.increase();
        return Operation.newFunction(identifier, args, scope);
    }*/
}
