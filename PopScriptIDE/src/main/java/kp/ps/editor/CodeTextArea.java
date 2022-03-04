/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kp.ps.editor.completion.PopScriptCompletionProvider;
import kp.ps.editor.highlight.HighlightNamespace;
import kp.ps.editor.highlight.PopScriptTokenMakerFactory;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.ScriptCompiler;
import kp.ps.utils.Pointer;
import kp.ps.utils.Utils;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Marc
 */
public class CodeTextArea extends RSyntaxTextArea implements FileDocumentReference
{
    private final JTabbedPane pages;
    private final CollapsibleSectionPanel basePanel;
    private final ButtonTabComponent buttonTab;
    private final PopScriptCompletionProvider completionProvider;
    private final AutoCompletion autoCompletion;
    private final HelpElementsManager helpElements;
    private final PopScriptParser parser;
    private final PopScriptTokenMakerFactory tokenMakerFactory;
    private final ErrorList errors;
    private String name;
    private Path file;
    private boolean hasChanges;
    
    public CodeTextArea(
            JTabbedPane pages,
            JTree helpTree,
            String name,
            Runnable buttonTabAction,
            Consumer<ErrorList> actionAfterParse)
    {
        super(20, 60);
        
        this.tokenMakerFactory = new PopScriptTokenMakerFactory();
        ((RSyntaxDocument) super.getDocument()).setTokenMakerFactory(tokenMakerFactory);
        
        this.file = null;
        this.name = Objects.requireNonNull(name);
        this.pages = Objects.requireNonNull(pages);
        this.basePanel = new CollapsibleSectionPanel(true);
        this.errors = new ErrorList();
        
        super.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if(!hasChanges)
                {
                    hasChanges = true;
                    updateTabTitle();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                if(!hasChanges)
                {
                    hasChanges = true;
                    updateTabTitle();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                if(!hasChanges)
                {
                    hasChanges = true;
                    updateTabTitle();
                }
            }
        });
        
        this.completionProvider = new PopScriptCompletionProvider();
        
        this.helpElements = new HelpElementsManager(helpTree, completionProvider);
        
        this.autoCompletion = new AutoCompletion(completionProvider);
        autoCompletion.setParameterAssistanceEnabled(true);
        autoCompletion.setShowDescWindow(true);
        autoCompletion.setAutoCompleteEnabled(true);
        autoCompletion.setAutoActivationEnabled(true);
        autoCompletion.setAutoCompleteSingleChoices(true);
        
        CompletionCellRenderer cellRenderer = new CompletionCellRenderer();
        cellRenderer.setShowTypes(true);
        cellRenderer.setTypeColor(new Color(150, 150, 255));
        cellRenderer.setParamColor(Color.BLUE);
        autoCompletion.setListCellRenderer(cellRenderer);
        
        autoCompletion.setDescriptionWindowColor(new Color(41, 49, 52));
        
        autoCompletion.install(this);
        
        final Runnable paramButtonTabAction = buttonTabAction == null ? () -> {} : buttonTabAction;
        this.buttonTab = new ButtonTabComponent(pages, this, () -> {
            paramButtonTabAction.run();
            autoCompletion.uninstall();
        });
        
        setSyntaxEditingStyle(Utils.POPSCRIPT_TEXT_TYPE);
        this.parser = new PopScriptParser(this, errors, actionAfterParse, completionProvider, helpElements, tokenMakerFactory);
        
        setCodeFoldingEnabled(true);
        setMarkOccurrences(true);
        setParserDelay(500);
        addParser(parser);
        
        RTextScrollPane scroll = new RTextScrollPane(this);
        undoLastAction();
        basePanel.add(scroll);
        
        try
        {
            Theme theme = Theme.load(Utils.getClasspathResourceAsStream("/kp/ps/editor/themes/main-theme.xml"));
            theme.apply(this);
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    
    public final Path getFile() { return file; }
    
    public final PopScriptParser getParser() { return parser; }
    
    public final ErrorList getErrors() { return errors; }
    
    private int getTabIndex()
    {
        int len = pages.getTabCount();
        for(int i=0;i<len;i++)
        {
            Component panel = pages.getComponentAt(i);
            if(panel != null && ((JPanel) pages.getComponentAt(i)).getComponent(0) == basePanel)
                return i;
        }
        return -1;
    }
    
    public final void updateTabTitle()
    {
        String title = hasChanges ? name + '*' : name;
        int idx = getTabIndex();
        if(idx >= 0)
        {
            pages.setTitleAt(idx, title);
            pages.updateUI();
        }
    }
    
    public final void setTitle(String title)
    {
        name = Objects.requireNonNull(title);
        updateTabTitle();
    }
    
    public final void clearChanges()
    {
        if(hasChanges)
        {
            hasChanges = false;
            updateTabTitle();
        }
    }
    
    public final boolean hasChanges() { return hasChanges; }
    
    public final void linkToPages()
    {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(basePanel);
        
        pages.addTab(name, contentPanel);
        pages.setTabComponentAt(pages.getTabCount() - 1, buttonTab);
    }
    
    public final void destroy() { buttonTab.push(); }
    
    public final String getAreaName() { return name; }
    
    public final void setFile(Path file) { this.file = file; }

    @Override
    public Path getFilePath() { return file; }
    
    public static final CodeTextArea castFrom(Component component)
    {
        if(component == null)
            return null;
        return (CodeTextArea) ((RTextScrollPane)(((CollapsibleSectionPanel)((JPanel) component).getComponent(0))).getComponent(0)).getTextArea();
    }
    
    public final void linkHelper()
    {
        updateHelpers();
    }
    
    public final void compileAndUpdateHelpers()
    {
        Pointer<HighlightNamespace> hpPointer = new Pointer();
        ScriptCompiler.compile(getText(), errors, file, completionProvider.getBaseProvider(), hpPointer);
        tokenMakerFactory.setHighlightManagerRoot(hpPointer.get());
        updateHelpers();
    }
    
    public final void updateHelpers()
    {
        helpElements.update();
    }
}
