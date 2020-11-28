/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Objects;

/**
 *
 * @author Marc
 */
public class Assignment extends Statement
{
    private final AssignmentType type;
    private final Identifier left;
    private final Statement right;
    
    public Assignment(Operator operator, Statement left, Statement right) throws SyntaxException
    {
        switch(operator.getOperatorId())
        {
            case ASSIGNATION: this.type = AssignmentType.NORMAL; break;
            case ASSIGNATION_ADD: this.type = AssignmentType.ADD; break;
            case ASSIGNATION_SUBTRACT: this.type = AssignmentType.SUBTRACT; break;
            case ASSIGNATION_MULTIPLY: this.type = AssignmentType.MULTIPLY; break;
            case ASSIGNATION_DIVIDE: this.type = AssignmentType.DIVIDE; break;
            default: throw new IllegalArgumentException();
        }
        
        if(Objects.requireNonNull(left).getFragmentType() != FragmentType.IDENTIFIER)
            throw new SyntaxException("In assignment left part can only put a valid identifier. But found: " + left);
        this.left = (Identifier) left;
        
        this.right = Objects.requireNonNull(right);
    }
    
    public final Identifier getLeftPart() { return left; }
    public final Statement getRightPart() { return right; }
    
    public final AssignmentType getAssignmentType() { return type; }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.ASSIGNMENT; }

    @Override
    public final String toString() { return left + " " + type.getAssociatedOperator() + " " + right; }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Assignment)
        {
            Assignment a = (Assignment) o;
            return type == a.type &&
                    left.equals(a.left) &&
                    right.equals(a.right);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + Objects.hashCode(this.left);
        hash = 83 * hash + Objects.hashCode(this.right);
        return hash;
    }
    
}
