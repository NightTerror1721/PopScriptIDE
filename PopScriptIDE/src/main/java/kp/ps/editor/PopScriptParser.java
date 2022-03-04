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
import kp.ps.editor.completion.PopScriptCompletionProvider;
import kp.ps.editor.highlight.HighlightNamespace;
import kp.ps.editor.highlight.PopScriptTokenMakerFactory;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.ScriptCompiler;
import kp.ps.utils.Pointer;
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
    private final PopScriptTokenMakerFactory tokenMakerFactory;
    
    public PopScriptParser(
            CodeTextArea area,
            ErrorList errors,
            Consumer<ErrorList> actionAfterParse,
            PopScriptCompletionProvider completionProvider,
            HelpElementsManager helpElements,
            PopScriptTokenMakerFactory tokenMakerFactory)
    {
        this.file = Objects.requireNonNull(area);
        this.errors = Objects.requireNonNull(errors);
        this.actionAfterParse = actionAfterParse;
        this.completionProvider = Objects.requireNonNull(completionProvider);
        this.helpElements = Objects.requireNonNull(helpElements);
        this.tokenMakerFactory = Objects.requireNonNull(tokenMakerFactory);
        super.setEnabled(true);
    }
    
    @Override
    public ParseResult parse(RSyntaxDocument doc, String style)
    {
        try
        {
            Path filePath = file.hasFile() ? file.getFilePath() : null;
            Pointer<HighlightNamespace> hnPointer = new Pointer();
            errors.clear();
            ScriptCompiler.compile(doc.getText(0, doc.getLength()), errors, filePath, completionProvider.getBaseProvider(), hnPointer);
            helpElements.update();
            tokenMakerFactory.setHighlightManagerRoot(hnPointer.get());
        }
        catch(BadLocationException ex) {}
        
        if(actionAfterParse != null)
            actionAfterParse.accept(errors);
        
        return errors.generateParseResult(this);
    }
}
