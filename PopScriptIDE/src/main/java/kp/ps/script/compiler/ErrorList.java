/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

/**
 *
 * @author Asus
 */
public final class ErrorList implements Iterable<ErrorList.ErrorEntry>
{
    private final LinkedList<ErrorEntry> errors = new LinkedList<>();
    
    public final ErrorEntry addError(Path file, int startLine, int endLine, CompilerException cause)
    {
        ErrorEntry e = new ErrorEntry(file, startLine, endLine, cause);
        errors.add(e);
        return e;
    }
    
    public final boolean hasErrors() { return !errors.isEmpty(); }
    
    public final List<ErrorEntry> getAllErrors() { return Collections.unmodifiableList(new ArrayList<>(errors)); }
    
    public final ErrorEntry getError(int index) { return errors.get(index); }
    
    public final int getErrorCount() { return errors.size(); }
    
    public final void clear() { errors.clear(); }
    
    public final ParseResult generateParseResult(Parser parser)
    {
        DefaultParseResult result = new DefaultParseResult(parser);
        errors.forEach(entry -> {
            String msg = entry.getFilePath() == null
                    ? "line " + entry.getStartLine() + ":" + entry.getCause().getMessage()
                    : entry.getFilePath() + "(line " + entry.getStartLine() + "): " + entry.getCause().getMessage();
            result.addNotice(new DefaultParserNotice(parser, msg, entry.getStartLine()));
        });
        return result;
    }
    
    public final String generateParseResultAsString(Parser parser)
    {
        StringBuilder sb = new StringBuilder();
        ParseResult result = generateParseResult(parser);
        result.getNotices().forEach(notice -> sb.append(notice.getMessage()).append('\n'));
        return sb.toString();
    }

    @Override
    public final Iterator<ErrorEntry> iterator() { return errors.iterator(); }
    
    public static final class ErrorEntry
    {
        private final Path file;
        private final int startLine;
        private final int endLine;
        private final CompilerException cause;
        
        private ErrorEntry(Path file, int startLine, int endLine, CompilerException cause)
        {
            this.file = file;
            this.startLine = startLine;
            this.endLine = endLine;
            this.cause = Objects.requireNonNull(cause);
        }
        
        public final Path getFilePath() { return file; }
        public final int getStartLine() { return startLine; }
        public final int getEndLine() { return endLine; }
        public final CompilerException getCause() { return cause; }
        
        public final String getMessage()
        {
            return cause.getMessage();
        }
    }
}
