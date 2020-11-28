/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

/**
 *
 * @author Marc
 */
public enum AssignmentType
{
    NORMAL,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE;
    
    public final Operator getAssociatedOperator()
    {
        switch(this)
        {
            case NORMAL: return Operator.fromId(OperatorId.ASSIGNATION);
            case ADD: return Operator.fromId(OperatorId.ASSIGNATION_ADD);
            case SUBTRACT: return Operator.fromId(OperatorId.ASSIGNATION_SUBTRACT);
            case MULTIPLY: return Operator.fromId(OperatorId.ASSIGNATION_MULTIPLY);
            case DIVIDE: return Operator.fromId(OperatorId.ASSIGNATION_DIVIDE);
        }
        
        throw new IllegalStateException();
    }
}
