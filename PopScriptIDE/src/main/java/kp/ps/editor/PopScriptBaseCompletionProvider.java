/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.JTextComponent;
import kp.ps.utils.Relevance;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;

/**
 *
 * @author Marc
 */
public class PopScriptBaseCompletionProvider extends DefaultCompletionProvider
{
    private boolean computedCache = false;
    protected final List<Completion>[] cache = initCache();
    
    final List<Completion> getCompletions() { return completions; }
    
    private static List<Completion>[] initCache()
    {
        List<Completion>[] lists = new List[Relevance.count()];
        for(int i = 0; i < lists.length; ++i)
            lists[i] = new LinkedList<>();
        return lists;
    }
    
    protected void clearCache()
    {
        if(computedCache)
        {
            computedCache = false;
            for(List<Completion> list : cache)
                list.clear();
        }
    }
    
    protected final List<Completion>[] getCompletionsCache()
    {
        if(!computedCache)
        {
            computedCache = true;
            completions.forEach(completion -> cache[completion.getRelevance()].add(completion));
            for(List<Completion> list : cache)
                list.sort(comparator);
        }
        return cache;
    }
    
    /**
     * Adds a single completion to this provider.  If you are adding multiple
     * completions to this provider, for efficiency reasons please consider
     * using {@link #addCompletions(List)} instead.
     *
     * @param c The completion to add.
     * @throws IllegalArgumentException If the completion's provider isn't
     *         this {@code CompletionProvider}.
     * @see #addCompletions(List)
     * @see #removeCompletion(Completion)
     * @see #clear()
     */
    @Override
    public void addCompletion(Completion c)
    {
        super.addCompletion(c);
        clearCache();
    }


    /**
     * Adds {@link Completion}s to this provider.
     *
     * @param completions The completions to add.  This cannot be
     *        <code>null</code>.
     * @throws IllegalArgumentException If a completion's provider isn't
     *         this {@code CompletionProvider}.
     * @see #addCompletion(Completion)
     * @see #removeCompletion(Completion)
     * @see #clear()
     */
    @Override
    public void addCompletions(List<Completion> completions)
    {
        super.addCompletions(completions);
        clearCache();
    }


    /**
     * Adds simple completions for a list of words.
     *
     * @param words The words.
     * @see BasicCompletion
     */
    @Override
    protected void addWordCompletions(String[] words)
    {
        super.addWordCompletions(words);
        clearCache();
    }


    @Override
    protected void checkProviderAndAdd(Completion c)
    {
        super.checkProviderAndAdd(c);
        clearCache();
    }


    /**
     * Removes all completions from this provider.  This does not affect
     * the parent {@code CompletionProvider}, if there is one.
     *
     * @see #addCompletion(Completion)
     * @see #addCompletions(List)
     * @see #removeCompletion(Completion)
     */
    @Override
    public void clear()
    {
        super.clear();
        clearCache();
    }
    
    /**
     * Removes the specified completion from this provider.  This method
     * will not remove completions from the parent provider, if there is one.
     *
     * @param c The completion to remove.
     * @return <code>true</code> if this provider contained the specified
     *         completion.
     * @see #clear()
     * @see #addCompletion(Completion)
     * @see #addCompletions(List)
     */
    @Override
    public boolean removeCompletion(Completion c)
    {
        boolean result = super.removeCompletion(c);
        clearCache();
        return result;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<Completion> getCompletionsImpl(JTextComponent comp)
    {

        List<Completion> retVal = new ArrayList<>();
        String text = getAlreadyEnteredText(comp);

        if(text != null)
        {
            for(List<Completion> cacheCompletions : getCompletionsCache())
                fillCompletion(retVal, text, cacheCompletions);
        }

        return retVal;
    }
    
    private void fillCompletion(List<Completion> retVal, String text, List<Completion> completions)
    {
        int index = Collections.binarySearch(completions, text, comparator);
        if(index < 0) // No exact match
        { 
            index = -index - 1;
        }
        else
        {
            // If there are several overloads for the function being
            // completed, Collections.binarySearch() will return the index
            // of one of those overloads, but we must return all of them,
            // so search backward until we find the first one.
            int pos = index - 1;
            while(pos > 0 && comparator.compare(completions.get(pos), text) == 0)
            {
                retVal.add(completions.get(pos));
                pos--;
            }
        }

        while (index<completions.size())
        {
            Completion c = completions.get(index);
            if(Util.startsWithIgnoreCase(c.getInputText(), text))
            {
                retVal.add(c);
                index++;
            }
            else break;
        }
    }
    
    
    /**
     * Returns a list of {@code Completion}s in this provider with the
     * specified input text.
     *
     * @param inputText The input text to search for.
     * @return A list of {@link Completion}s, or <code>null</code> if there
     *         are no matching {@code Completion}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Completion> getCompletionByInputText(String inputText)
    {
        ArrayList<Completion> retVal = new ArrayList<>();
        for(List<Completion> cacheCompletions : getCompletionsCache())
                fillCompletionByInputText(retVal, inputText, cacheCompletions);
        return retVal;
    }
    
    @SuppressWarnings("empty-statement")
    private void fillCompletionByInputText(List<Completion> retVal, String inputText, List<Completion> completions)
    {
        // Find any entry that matches this input text (there may be > 1).
        int end = Collections.binarySearch(completions, inputText, comparator);
        if (end < 0)
            return;

        // There might be multiple entries with the same input text.
        int start = end;
        while (start > 0 && comparator.compare(completions.get(start-1), inputText)==0) {
            start--;
        }
        int count = completions.size();
        //@SuppressWarnings("empty-statement")
        while (++end < count && comparator.compare(completions.get(end), inputText)==0);

        retVal.addAll(completions.subList(start, end)); // (inclusive, exclusive)
    }
}
