/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.text.BadLocationException;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.ScriptCompiler;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

/**
 *
 * @author Marc
 */
public class PopScriptParser extends AbstractParser
{
    private final FileDocumentReference file;
    private final ErrorList errors;
    private final Consumer<ErrorList> actionAfterParse;
    private final PopScriptCompletionProvider completionProvider;
    private final HelpElementsManager helpElements;
    
    public PopScriptParser(
            FileDocumentReference file,
            ErrorList errors,
            Consumer<ErrorList> actionAfterParse,
            PopScriptCompletionProvider completionProvider,
            HelpElementsManager helpElements)
    {
        this.file = Objects.requireNonNull(file);
        this.errors = Objects.requireNonNull(errors);
        this.actionAfterParse = actionAfterParse;
        this.completionProvider = Objects.requireNonNull(completionProvider);
        this.helpElements = Objects.requireNonNull(helpElements);
        super.setEnabled(true);
    }
    
    @Override
    public ParseResult parse(RSyntaxDocument doc, String style)
    {
        try
        {
            Path filePath = file.hasFile() ? file.getFilePath() : null;
            errors.clear();
            ScriptCompiler.compile(doc.getText(0, doc.getLength()), errors, filePath, completionProvider.getBaseProvider());
            helpElements.update();
        }
        catch(BadLocationException ex) {}
        
        if(actionAfterParse != null)
            actionAfterParse.accept(errors);
        
        return errors.generateParseResult(this);
    }
    
}
