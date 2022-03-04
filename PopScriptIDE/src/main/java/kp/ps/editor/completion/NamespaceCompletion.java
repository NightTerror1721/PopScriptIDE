/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor.completion;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 *
 * @author Marc
 */
public class NamespaceCompletion extends BasicCompletion
{
    private final String name;
    
    /**
     * Constructor.
     *
     * @param provider The parent completion provider.
     * @param name
     * @param replacementText The text to replace.
     */
    public NamespaceCompletion(CompletionProvider provider, String name, String replacementText)
    {
        super(provider, replacementText);
        this.name = name;
    }


    /**
     * Constructor.
     *
     * @param provider The parent completion provider.
     * @param name
     * @param replacementText The text to replace.
     * @param shortDesc A short description of the completion.  This will be
     *        displayed in the completion list.  This may be <code>null</code>.
     */
    public NamespaceCompletion(CompletionProvider provider, String name, String replacementText, String shortDesc)
    {
        super(provider, replacementText, shortDesc);
        this.name = name;
    }


    /**
     * Constructor.
     *
     * @param provider The parent completion provider.
     * @param name
     * @param replacementText The text to replace.
     * @param shortDesc A short description of the completion.  This will be
     *        displayed in the completion list.  This may be <code>null</code>.
     * @param summary The summary of this completion.  This should be HTML.
     *        This may be <code>null</code>.
     */
    public NamespaceCompletion(CompletionProvider provider, String name, String replacementText, String shortDesc, String summary)
    {
        super(provider, replacementText, shortDesc, summary);
        this.name = name;
    }
    
    public final String getName() { return name; }
}
