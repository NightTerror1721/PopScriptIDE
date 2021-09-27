/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.JTextComponent;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.functions.InnerFunction;
import kp.ps.script.compiler.functions.InnerFunctionPool;
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.compiler.functions.Parameter;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.CommandId;
import kp.ps.utils.Utils;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;

/**
 *
 * @author Marc
 */
public class PopScriptCompletionProvider extends CompletionProviderBase implements ToolTipSupplier
{
    private static PopScriptRootCompletionProvider globalProvider;
    
    private final PopScriptNamespaceCompletionProvider baseProvider;


    /**
     * Constructor subclasses can use when they don't have their default
     * provider created at construction time.  They should call
     * {@link #setDefaultCompletionProvider(CompletionProvider)} in this
     * constructor.
     */
    public PopScriptCompletionProvider()
    {
        initGlobalProvider();
        this.baseProvider = new PopScriptNamespaceCompletionProvider(globalProvider);
    }
    
    public final PopScriptNamespaceCompletionProvider getBaseProvider() { return baseProvider; }


    /**
     * Calling this method will result in an
     * {@link UnsupportedOperationException} being thrown.  To set the
     * parameter completion parameters, do so on the provider returned by
     * {@link #getDefaultCompletionProvider()}.
     *
     * @throws UnsupportedOperationException Always.
     * @see #setParameterizedCompletionParams(char, String, char)
     */
    @Override
    public void clearParameterizedCompletionParams() {
        throw new UnsupportedOperationException();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlreadyEnteredText(JTextComponent comp)
    {
        if (!(comp instanceof RSyntaxTextArea))
                return EMPTY_STRING;

        CompletionProvider provider = baseProvider.getProviderFor(comp);
        return provider != null ? provider.getAlreadyEnteredText(comp) : globalProvider.getAlreadyEnteredText(comp);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Completion> getCompletionsAt(JTextComponent tc, Point p)
    {
        return baseProvider.getCompletionsAt(tc, p);
    }


    /**
     * Does the dirty work of creating a list of completions.
     *
     * @param comp The text component to look in.
     * @return The list of possible completions, or an empty list if there
     *         are none.
     */
    @Override
    protected List<Completion> getCompletionsImpl(JTextComponent comp)
    {
        if (comp instanceof RSyntaxTextArea)
        {
            CompletionProvider provider = baseProvider.getProviderFor(comp);
            if (provider != null)
                return provider.getCompletions(comp);
            return baseProvider.getCompletions(comp);
        }
        return Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc)
    {
        // Parameterized completions can only come from the "code" completion
        // provider.  We do not do function/method completions while editing
        // strings or comments.

        CompletionProvider provider = baseProvider.getProviderFor(tc);
        if(provider == null)
            return baseProvider.getParameterizedCompletions(tc);
        return provider.getParameterizedCompletions(tc);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char getParameterListEnd()
    {
        return baseProvider.getParameterListEnd();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterListSeparator()
    {
        return baseProvider.getParameterListSeparator();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char getParameterListStart()
    {
        return baseProvider.getParameterListStart();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoActivateOkay(JTextComponent tc)
    {
        CompletionProvider provider = baseProvider.getProviderFor(tc);
        return provider!=null ? provider.isAutoActivateOkay(tc) : false;
    }


    /**
     * Calling this method will result in an
     * {@link UnsupportedOperationException} being thrown.To set the
     * parameter completion parameters, do so on the provider returned by
     * {@link #getDefaultCompletionProvider()}.
     *
     * @param listStart
     * @param separator
     * @param listEnd
     * @throws UnsupportedOperationException Always.
     * @see #clearParameterizedCompletionParams()
     */
    @Override
    public void setParameterizedCompletionParams(char listStart, String separator, char listEnd) {
        throw new UnsupportedOperationException();
    }


    /**
     * Returns the tool tip to display for a mouse event.<p>
     *
     * For this method to be called, the {@code RSyntaxTextArea} must be
     * registered with the {@code javax.swing.ToolTipManager} like so:
     *
     * <pre>
     * ToolTipManager.sharedInstance().registerComponent(textArea);
     * </pre>
     *
     * @param textArea The text area.
     * @param e The mouse event.
     * @return The tool tip text, or <code>null</code> if none.
     */
    @Override
    public String getToolTipText(RTextArea textArea, MouseEvent e)
    {
        String tip = null;

        List<Completion> completions = getCompletionsAt(textArea, e.getPoint());
        if (completions != null && completions.size() > 0)
        {
            // Only ever 1 match for us in C...
            Completion c = completions.get(0);
            tip = c.getToolTipText();
        }

        return tip;
    }


    

    
        
    private static void initGlobalProvider()
    {
        if(globalProvider == null)
        {
            globalProvider = new PopScriptRootCompletionProvider();
            LinkedList<Completion> completions = new LinkedList<>();
            Namespace.getAllGlobals().forEach(field -> parseCompletion(globalProvider, completions, field));
            
            for(TypeId type : TypeId.values())
            {
                BasicCompletion comp = new BasicCompletion(globalProvider, type.getTypeName());
                comp.setIcon(Utils.getKeywordIcon());
                comp.setRelevance(Utils.DEFAULT_RELEVANCE);
                completions.add(comp);
            }
            
            for(CommandId cmd : CommandId.values())
            {
                BasicCompletion comp = new BasicCompletion(globalProvider, cmd.getCommandName());
                comp.setIcon(Utils.getKeywordIcon());
                comp.setRelevance(Utils.DEFAULT_RELEVANCE);
                completions.add(comp);
            }
                
            globalProvider.addCompletions(completions);
            globalProvider.setParameterizedCompletionParams('(', ", ", ')');
        }
    }
    
    static final void parseCompletion(CompletionProvider provider, List<Completion> completions, NamespaceField field)
    {
        switch(field.getFieldType())
        {
            case CONSTANT:
            case INTERNAL:
                try
                {
                    VariableCompletion comp = new VariableCompletion(provider, field.getName(), field.getCompleteType().toString());
                    comp.setIcon(Utils.getFieldIcon());
                    comp.setRelevance(Utils.FIELD_RELEVANCE);
                    completions.add(comp);
                }
                catch(CompilerException ex) { ex.printStackTrace(System.err); }
                break;

            case TYPED_VALUE: {
                if(field.getType() == TypeId.ACTION)
                {
                    try
                    {
                        if(InnerFunctionPool.exists(field.getTypedValue().getToken()))
                        {
                            InnerFunction func = InnerFunctionPool.get(field.getTypedValue().getToken());
                            FunctionCompletion cmp =
                                    new FunctionCompletion(provider, field.getName(), func.hasReturn() ? TypeId.INT.getTypeName() : "void");
                            LinkedList<ParameterizedCompletion.Parameter> pars = new LinkedList<>();
                            int len = func.getParametersCount();
                            for(int i = 0; i < len; ++i)
                            {
                                Parameter funcPar = func.getParameter(i);
                                ParameterizedCompletion.Parameter par = new ParameterizedCompletion.Parameter(
                                                funcPar.getType().toString(), funcPar.getName(), i + 1 == len);
                                pars.add(par);
                            }
                            cmp.setParams(pars);
                            cmp.setIcon(Utils.getFunctionIcon());
                            cmp.setRelevance(Utils.FUNCTION_RELEVANCE);
                            completions.add(cmp);
                            
                            /*if(func.getAction() == ScriptToken.BUILD_AT)
                            {
                                cmp.setReturnValueDescription("This function not return anything.");
                                cmp.getSummary();
                                cmp.setShortDescription("<html><font size=\"4\"><font face=\"Arial\"><font size=\"2\">Places\n" +
"a building plan of the specified building type at the coordinates\n" +
"given.&nbsp; If that land is not available, it will be placed as near\n" +
"as possible to the given coordinates.<br><br>\n" +
"</font></font></font>\n" +
"<hr size=\"2\" width=\"100%\"><font size=\"4\"><font face=\"Arial\"><font size=\"2\"><b><br>\n" +
"Syntax:<br>\n" +
"<br>\n" +
"</b><font face=\"Courier New\">DO BUILD_AT x z building param</font><br>\n" +
"<b><br><br>\n" +
"Parameters:<br>\n" +
"<br>\n" +
"</b></font></font></font><font size=\"4\"><font face=\"Arial\"><font size=\"2\"><font color=\"#ffcc00\">\n" +
"x</font>: Specifies the </font></font></font><font size=\"4\"><font face=\"Arial\"><font size=\"2\">X-coordinate of the location to build at.<br>\n" +
"<br><font color=\"#ffcc00\">\n" +
"z</font>: Specifies the Z-coordinate of the location to build at.</font></font></font><font size=\"4\"><font face=\"Arial\"><font size=\"2\"><br>\n" +
"<br>\n" +
"<font color=\"#ffcc00\">building</font>: Specifies which type of building\n" +
"plan to place down.&nbsp; See the <a href=\"https://ts.popre.net/archive/Downloads/Docs/PopScript_Wiki_HTML_Help_File.htm#Pop_Script_Internal_Variables\">Internal Game Variables</a> page for a list of all of the\n" +
"building type identifiers that you could use in this parameter.<br>\n" +
"<br>\n" +
"<font color=\"#ffcc00\">param</font>: ???&nbsp;&nbsp; May control the direction the building faces.<br>\n" +
"<b><br><br></html>");
                            }*/
                        }
                    }
                    catch(CompilerException ex) { ex.printStackTrace(System.err); }
                }
                else
                {
                    try
                    {
                        VariableCompletion comp = new VariableCompletion(provider, field.getName(), field.getCompleteType().toString());
                        comp.setIcon(Utils.getFieldIcon());
                        comp.setRelevance(Utils.FIELD_RELEVANCE);
                        completions.add(comp);
                    }
                    catch(CompilerException ex) { ex.printStackTrace(System.err); }
                }
            } break;
        }
    }
    
    static final void parseCompletion(CompletionProvider provider, List<Completion> completions, Macro macro)
    {
        MacroCompletion cmp = new MacroCompletion(provider, macro.getName(), macro.hasYield() ? TypeId.INT.getTypeName() : "void");
        LinkedList<ParameterizedCompletion.Parameter> pars = new LinkedList<>();
        int len = macro.getParameterCount();
        for(int i = 0; i < len; ++i)
        {
            Parameter funcPar = macro.getParameter(i);
            ParameterizedCompletion.Parameter par = new ParameterizedCompletion.Parameter(
                            funcPar.getType(), funcPar.getName(), i + 1 == len);
            pars.add(par);
        }
        cmp.setParams(pars);
        cmp.setIcon(Utils.getMacroIcon());
        cmp.setRelevance(Utils.MACRO_RELEVANCE);
        completions.add(cmp);
    }
    
    static final PopScriptRootCompletionProvider getRootCompletionProvider()
    {
        initGlobalProvider();
        return globalProvider;
    }
}
