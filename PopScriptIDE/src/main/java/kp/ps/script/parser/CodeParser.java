/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.io.EOFException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.instruction.Instruction;
import kp.ps.script.instruction.InstructionParser;
import kp.ps.utils.CodeReader;
import kp.ps.utils.HexadecimalDecoder;

/**
 *
 * @author Asus
 */
public final class CodeParser
{
    private final CodeQueue accumulated;
    private final CompilerState state;
    private String storedComment;
    
    public CodeParser(CompilerState state, Fragment last)
    {
        this.accumulated = new CodeQueue(last);
        this.state = Objects.requireNonNull(state);
    }
    public CodeParser(CompilerState state) { this(state, null); }
    
    public final Fragment parseFragment(CodeReader source, boolean isFinishValid, ErrorList errors) throws CompilerException
    {
        Fragment frag = parseFragment0(source, isFinishValid, errors);
        if(frag != null && frag.isType() && source.hasNext())
        {
            int lastIndex = source.getCurrentIndex();
            LinkedList<Fragment> old = new LinkedList<>(accumulated.list);
            Fragment nextType = parseFragment0(source, isFinishValid, errors);
            if(nextType != null && nextType.isType())
                ((Type) frag).insert((Type) nextType);
            else
            {
                source.setIndex(lastIndex);
                accumulated.list.clear();
                accumulated.list = old;
            }
        }
        return frag;
    }
    
    private Fragment parseFragment0(CodeReader source, boolean isFinishValid, ErrorList errors) throws CompilerException
    {
        if(!accumulated.isEmpty())
            return accumulated.dequeue();
        FragmentBuilder builder = new FragmentBuilder(source, accumulated, isFinishValid);
        
        try
        {
            for(;;)
            {
                char c = source.next();
                
                main_switch:
                switch(c)
                {
                    case '\t':
                    case '\r':
                    case '\n':
                    case ' ': {
                        if(builder.flush())
                            return accumulated.dequeue();
                    } break;
                    
                    case ';': {
                        builder.flush();
                        return accumulated.enqret(Separator.SEMI_COLON);
                    }
                    
                    case '(': {
                        builder.flush();
                        CodeReader scopeSource = extractScope(source, '(', ')');
                        FragmentList list = parseSubStatement(scopeSource, errors);
                        if(!accumulated.hasLast() || Command.is(accumulated.last(), CommandId.YIELD) ||
                                (!accumulated.last().isStatement() && !accumulated.last().isCommand())) //Parenthesis
                        {
                            return accumulated.enqret(StatementParser.parse(list));
                        }
                        else if(accumulated.last().isCommand() && Command.hasArguments(accumulated.last())) //Command arguments
                            return accumulated.enqret(ArgumentList.argsToCall(list));
                        else if(accumulated.isMacroDeclaration()) //Macro arguments
                            return accumulated.enqret(ArgumentList.argsToDeclaration(list));
                        else //Arguments
                        {
                            accumulated.enqueue(Operator.fromId(OperatorId.FUNCTION_CALL));
                            return accumulated.enqret(ArgumentList.argsToCall(list));
                        } 
                    }
                    
                    case ')': throw new CompilerException("Unexpected end of parenthesis ')'");
                    
                    case '{': {
                        builder.flush();
                        CodeReader scopeSource = extractScope(source, '{', '}');
                        if(!accumulated.hasLast() || (!accumulated.last().isStatement() && !accumulated.last().isCommand() &&
                                !accumulated.last().isArgumentList()))
                        {
                            throw new CompilerException("Unexpected start of scope '{'");
                        }
                        else // Scope
                        {
                            Scope scope = parseScope(scopeSource, errors);
                            return accumulated.enqret(scope);
                        }
                    }
                    
                    case '}': throw new CompilerException("Unexpected end of scope parenthesis '}'");
                    
                    case '\"': {
                        builder.flush();

                        final char base = c;
                        builder.disableFinish();
                        for(;;)
                        {
                            c = source.next();
                            if(c == base)
                                break;
                            if(c == '\\')
                            {
                                c = source.next();
                                switch(c)
                                {
                                    case 'n': builder.append('\n'); break;
                                    case 'r': builder.append('\r'); break;
                                    case 't': builder.append('\t'); break;
                                    case 'u': {
                                        if(!source.canPeek(4))
                                            throw new CompilerException("Invalid unicode scape");
                                        String hexCode = new String(source.nextArray(4));
                                        builder.append(HexadecimalDecoder.decodeUnicode(hexCode));
                                    } break;
                                    case '\\': builder.append('\\'); break;
                                    case '\'': builder.append('\''); break;
                                    case '\"': builder.append('\"'); break;
                                }
                                continue;
                            }
                            builder.append(c);
                        }
                        builder.enableFinish();
                        String value = builder.toString();
                        builder.clear();
                        return accumulated.enqret(new StringLiteral(value));
                    }
                    
                    case ',': {
                        builder.flush();
                        return accumulated.enqret(Separator.COMMA);
                    }
                    
                    case ':': {
                        builder.flush();
                        return accumulated.enqret(Separator.TWO_POINTS);
                    }
                    
                    case '?': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: ?");
                        return accumulated.enqret(Operator.fromId(OperatorId.ELVIS));
                    }
                    
                    case '|': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: |");
                        c = source.next();
                        switch(c)
                        {
                            default:
                                throw new CompilerException("Unexpected character: |");
                            case '|': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: |");
                                accumulated.enqueue(Operator.fromId(OperatorId.OR));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '&': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: &");
                        c = source.next();
                        switch(c)
                        {
                            default:
                                throw new CompilerException("Unexpected character: &");
                            case '&': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: &");
                                accumulated.enqueue(Operator.fromId(OperatorId.AND));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '^': 
                        throw new CompilerException("Invalid symbol " + c);
                    
                    case '.': {
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: .");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(!builder.isEmpty() && isInteger(builder.toString()))
                                {
                                    builder.append('.');
                                    break main_switch;
                                }
                                builder.flush();
                                accumulated.enqueue(Operator.fromId(OperatorId.NAMESPACE_RESOLUTION));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '!': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: !");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.LOGICAL_NOT));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: =");
                                accumulated.enqueue(Operator.fromId(OperatorId.DIFFERENT));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '=': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: =");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.ASSIGNATION));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: =");
                                accumulated.enqueue(Operator.fromId(OperatorId.EQUALS));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '>': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: >");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.GREATER));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: >");
                                accumulated.enqueue(Operator.fromId(OperatorId.GREATER_EQUALS));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '<': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: <");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.LESS));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: <");
                                accumulated.enqueue(Operator.fromId(OperatorId.LESS_EQUALS));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '-': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: -");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(accumulated.hasLast() && accumulated.last().isStatement())
                                {
                                    if(Character.isDigit(c))
                                    {
                                        builder.append('-');
                                        break main_switch;
                                    }
                                    accumulated.enqueue(Operator.fromId(OperatorId.SUBTRACT));
                                }
                                else accumulated.enqueue(Operator.fromId(OperatorId.UNARY_MINUS));
                            } break;
                            case '-': {
                                if(!accumulated.hasLast() || !accumulated.last().isStatement())
                                    accumulated.enqueue(Operator.fromId(OperatorId.PREFIX_DECREASE));
                                else accumulated.enqueue(Operator.fromId(OperatorId.SUFFIX_DECREASE));
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: -");
                                accumulated.enqueue(Operator.fromId(OperatorId.ASSIGNATION_SUBTRACT));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '+': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: +");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(accumulated.hasLast() && accumulated.last().isStatement())
                                {
                                    if(Character.isDigit(c))
                                        break main_switch;
                                    accumulated.enqueue(Operator.fromId(OperatorId.ADD));
                                }
                            } break;
                            case '+': {
                                if(!accumulated.hasLast() || !accumulated.last().isStatement())
                                    accumulated.enqueue(Operator.fromId(OperatorId.PREFIX_INCREASE));
                                else accumulated.enqueue(Operator.fromId(OperatorId.SUFFIX_INCREASE));
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: +");
                                accumulated.enqueue(Operator.fromId(OperatorId.ASSIGNATION_ADD));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '/': {
                        boolean flushResult = builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: /");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.DIVIDE));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: /");
                                accumulated.enqueue(Operator.fromId(OperatorId.ASSIGNATION_DIVIDE));
                            } break;
                            case '/': {
                                storedComment = source.seekOrEndAndReturn('\n');
                                if(!flushResult)
                                    break main_switch;
                            } break;
                            case '*': {
                                storedComment = source.seekOrEndAndReturn('*', '/');
                                if(!flushResult)
                                    break main_switch;
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '*': {
                        builder.flush();
                        if(!source.canPeek(0))
                            throw new CompilerException("Unexpected character: *");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.fromId(OperatorId.MULTIPPLY));
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(0))
                                    throw new CompilerException("Unexpected character: *");
                                accumulated.enqueue(Operator.fromId(OperatorId.ASSIGNATION_MULTIPLY));
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    
                    default: {
                        builder.append(c);
                    } break;
                }
            }
            
            /*if(!builder.isEmpty())
                throw new CompilerException("Unexpected End of File");*/
        }
        catch(EOFException ex)
        {
            if(!builder.canFinish())
                throw new CompilerException("Unexpected End of File");
            return builder.isEmpty() ? null : builder.decode();
        }
    }
    
    public final void setLast(Fragment last) { accumulated.setLast(last); }
    
    public final boolean checkNextOrBacktrack(CodeReader source, CommandId command, ErrorList errors) throws CompilerException
    {
        if(!source.hasNext())
            return false;
        
        int lastIndex = source.getCurrentIndex();
        LinkedList<Fragment> old = new LinkedList<>(accumulated.list);
        Fragment frag = parseFragment0(source, true, errors);
        if(frag == null || !frag.isCommand() || !Command.is(frag, command))
        {
            source.setIndex(lastIndex);
            accumulated.list.clear();
            accumulated.list = old;
            return false;
        }
        return true;
    }
    
    public final Statement parseInlineInstruction(CodeReader source, ErrorList errors, Fragment... preFragments) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        if(preFragments != null && preFragments.length > 0)
            frags.addAll(Arrays.asList(preFragments));
        accumulated.setLast(frags.isEmpty() ? null : frags.getLast());
        Fragment frag;
        //int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, true, errors)) != null)
        {
            if(frag != null)
                accumulated.setLast(frag);
            if(frag == Separator.SEMI_COLON)
                break;
            frags.add(frag);
        }
        FragmentList list = frags.isEmpty()
                ? new FragmentList()
                : new FragmentList(frags);
        return StatementParser.parse(list);
    }
    
    public final FragmentList parseInlineInstructionAsList(CodeReader source, Fragment last, ErrorList errors) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        Fragment frag;
        //int firstLine = source.getCurrentLine();
        accumulated.setLast(frags.isEmpty() ? last : frags.getLast());
        while((frag = parseFragment(source, true, errors)) != null)
        {
            if(frag != null)
                accumulated.setLast(frag);
            if(frag == Separator.SEMI_COLON)
                break;
            frags.add(frag);
        }
        return frags.isEmpty()
                ? new FragmentList()
                : new FragmentList(frags);
    }
    
    public final void findEmptyInlineInstruction(CodeReader source, Command last, ErrorList errors) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        accumulated.setLast(frags.isEmpty() ? last : frags.getLast());
        if(parseFragment(source, true, errors) != Separator.SEMI_COLON)
            throw new CompilerException("Expected empty instruction after " + last + " command");
    }
    
    public final FragmentList parseUntilScopeAsList(CodeReader source, Command last, ErrorList errors) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        Fragment frag;
        //int firstLine = source.getCurrentLine();
        accumulated.setLast(frags.isEmpty() ? last : frags.getLast());
        while((frag = parseFragment(source, true, errors)) != null)
        {
            if(frag != null)
                accumulated.setLast(frag);
            frags.add(frag);
            if(frag.isScope())
                break;
        }
        return frags.isEmpty()
                ? new FragmentList()
                : new FragmentList(frags);
    }
    
    public final FragmentList parseUntilScopeOrInlineAsList(CodeReader source, Command last, ErrorList errors) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        Fragment frag;
        //int firstLine = source.getCurrentLine();
        accumulated.setLast(frags.isEmpty() ? last : frags.getLast());
        while((frag = parseFragment(source, true, errors)) != null)
        {
            if(frag != null)
                accumulated.setLast(frag);
            if(frag == Separator.SEMI_COLON || frag == null)
                break;
            frags.add(frag);
            if(frag.isScope())
                break;
        }
        return frags.isEmpty()
                ? new FragmentList()
                : new FragmentList(frags);
    }
    
    public final FragmentList parseCommandArgsAndScope(CodeReader source, Command last, ErrorList errors)
            throws CompilerException
    {
        //int firstLine = source.getCurrentLine();
        accumulated.setLast(last);
        Fragment args = parseFragment(source, true, errors);
        if(args == null || !args.isArgumentList())
            throw new CompilerException("Expected valid arguments before " + last + " command. But found: " + args);
        int lastIndex = source.getCurrentIndex();
        LinkedList<Fragment> old = new LinkedList<>(accumulated.list);
        accumulated.setLast(args);
        Fragment scope = parseFragment(source, true, errors);
        if(scope == Separator.SEMI_COLON || scope == null)
            return new FragmentList(args, Scope.EMPTY_SCOPE);
        if(scope.isScope())
            return new FragmentList(args, scope);
        
        source.setIndex(lastIndex);
        accumulated.list.clear();
        accumulated.list = old;
        scope = new Scope(InstructionParser.parse(state, source, true, errors));
        return new FragmentList(args, scope);
    }
    
    public final FragmentList parseCommandScope(CodeReader source, Command last, ErrorList errors) throws CompilerException
    {
        //int firstLine = source.getCurrentLine();
        accumulated.setLast(last);
        int lastIndex = source.getCurrentIndex();
        LinkedList<Fragment> old = new LinkedList<>(accumulated.list);
        Fragment scope = parseFragment(source, true, errors);
        if(scope == Separator.SEMI_COLON || scope == null)
            return new FragmentList(Scope.EMPTY_SCOPE);
        if(scope.isScope())
            return new FragmentList(scope);
        
        source.setIndex(lastIndex);
        accumulated.list.clear();
        accumulated.list = old;
        scope = new Scope(InstructionParser.parse(state, source, true, errors));
        return new FragmentList(scope);
    }
    
    private FragmentList parseSubStatement(CodeReader source, ErrorList errors) throws CompilerException
    {
        LinkedList<Fragment> frags = new LinkedList<>();
        Fragment frag;
        //int firstLine = source.getCurrentLine();
        CodeParser parser = new CodeParser(state);
        while((frag = parser.parseFragment(source, true, errors)) != null)
        {
            if(frag != null)
                parser.accumulated.setLast(frag);
            frags.add(frag);
        }
        return frags.isEmpty()
                ? new FragmentList()
                : new FragmentList(frags);
    }
    
    private Scope parseScope(CodeReader source, ErrorList errors) throws CompilerException
    {
        List<Instruction> instrs = InstructionParser.parse(state, source, false, errors);
        return new Scope(instrs);
    }
    
    private static CodeReader extractScope(CodeReader source, char cstart, char cend) throws CompilerException
    {
        int startIndex = source.getCurrentIndex();
        int scope = 0;
        try
        {
            for(;;)
            {
                char c = source.next();
                if(c == cstart)
                    scope++;
                else if(c == cend)
                {
                    if(scope == 0)
                        return source.subpart(startIndex, source.getCurrentIndex() - 1);
                    scope--;
                }
            }
        }
        catch(EOFException ex) { throw new CompilerException("Char " + cstart + " is not a valid char"); }
    }
    
    private static boolean isInteger(String str)
    {
        for(char c : str.toCharArray())
            if(!Character.isDigit(c))
                return false;
        return true;
    }
    
    public final String getStoredComment() { return storedComment; }
    public final void clearStoredComment() { storedComment = null; }
    
    
    
    
    private static final class FragmentBuilder
    {
        private final CodeReader source;
        private final CodeQueue queue;
        private final StringBuilder sb = new StringBuilder(8);
        private final boolean canFinish;
        private boolean finishEnabled = true;
        
        private FragmentBuilder(CodeReader source, CodeQueue queue, boolean canFinish)
        {
            this.source = Objects.requireNonNull(source);
            this.queue = Objects.requireNonNull(queue);
            this.canFinish = canFinish;
        }
        
        public final int length() { return sb.length(); }
        public final boolean isEmpty() { return sb.length() < 1; }
        public final void clear() { sb.delete(0, sb.length()); }
        
        public final void enableFinish() { finishEnabled = true; }
        public final void disableFinish() { finishEnabled = false; }
        
        public final boolean canFinish() { return canFinish && finishEnabled; }
        
        public final FragmentBuilder append(byte value) { sb.append(value); return this; }
        public final FragmentBuilder append(short value) { sb.append(value); return this; }
        public final FragmentBuilder append(int value) { sb.append(value); return this; }
        public final FragmentBuilder append(long value) { sb.append(value); return this; }
        public final FragmentBuilder append(float value) { sb.append(value); return this; }
        public final FragmentBuilder append(double value) { sb.append(value); return this; }
        public final FragmentBuilder append(boolean value) { sb.append(value); return this; }
        public final FragmentBuilder append(char value) { sb.append(value); return this; }
        public final FragmentBuilder append(String value) { sb.append(value); return this; }
        public final FragmentBuilder append(char[] value) { sb.append(value); return this; }
        public final FragmentBuilder append(Object value) { sb.append(value); return this; }
        
        public final boolean flush() throws CompilerException, EOFException
        {
            if(isEmpty())
                return !queue.isEmpty();
            Fragment frag = decode();
            clear();
            queue.enqueue(frag);
            return true;
        }
        
        public final Fragment decode() throws CompilerException
        {
            if(isEmpty())
                throw new IllegalStateException();
            
            String str;
            switch(str = sb.toString())
            {
                case "0": return Literal.ZERO;
                case "1": return Literal.ONE;
                case "-1": return Literal.MINUSONE;
            }
            
            Command cmd = Command.fromName(str);
            if(cmd != null)
                return cmd;
            
            Type type = Type.fromName(str);
            if(type != null)
                return type;
            
            Fragment frag = Literal.parse(str);
            if(frag != null)
                return frag;
            return Identifier.valueOf(str);
        }
        
        @Override
        public final String toString() { return sb.toString(); }
    }
    
    private static final class CodeQueue
    {
        private Fragment last;
        private LinkedList<Fragment> list = new LinkedList<>();
        
        private CodeQueue(Fragment last) { this.last = last; }
        
        public final void setLast(Fragment last) { this.last = last; }
        
        public final CodeQueue enqueue(Fragment frag) { list.add(frag); return this; }
        
        public final Fragment dequeue() { return list.removeFirst(); }
        
        public final boolean hasLast() { return last != null || !list.isEmpty(); }
        public final Fragment last() { return list.isEmpty() ? last : list.getLast(); }
        
        public final boolean isEmpty() { return list.isEmpty(); }
        
        public final Fragment enqret(Fragment frag)
        {
            if(list.isEmpty())
                return frag;
            Fragment first = list.removeFirst();
            list.add(frag);
            return first;
        }
        
        public final boolean isMacroDeclaration()
        {
            Fragment prev1 = last();
            if(prev1 == null || (!prev1.isIdentifier() && !prev1.isNamespaceResolverOperation()))
                return false;
            
            if(list.isEmpty())
                return false;
            
            else if(list.size() == 1)
                return last != null && Command.is(last, CommandId.MACRO);
            
            Fragment prev2 = list.get(list.size() - 2);
            return prev2 != null && Command.is(prev2, CommandId.MACRO);
        }
    }
}
