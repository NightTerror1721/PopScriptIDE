/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor.completion;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import kp.ps.script.namespace.Namespace;
import kp.ps.utils.Utils;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 *
 * @author Marc
 */
public class PopScriptNamespaceCompletionProvider extends PopScriptBaseCompletionProvider
{
    private final HashMap<String, PopScriptNamespaceCompletionProvider> children = new HashMap<>();
    
    PopScriptNamespaceCompletionProvider()
    {
        setParameterizedCompletionParams('(', ", ", ')');
    }
    
    PopScriptNamespaceCompletionProvider(CompletionProvider parent)
    {
        this();
        setParent(parent);
    }
    
    public final Map<String, PopScriptNamespaceCompletionProvider> getChildren()
    {
        return Collections.unmodifiableMap(children);
    }
    
    @Override
    public final void clear()
    {
        children.values().forEach(PopScriptNamespaceCompletionProvider::clear);
        children.clear();
        super.clear();
    }
    
    public final void fill(Namespace namespace)
    {
        clear();
        
        LinkedList<Completion> comps = new LinkedList<>();
        
        namespace.getChildrenNamespace().forEach(child -> {
            PopScriptNamespaceCompletionProvider pchild = new PopScriptNamespaceCompletionProvider();
            children.put(child.getName(), pchild);
            pchild.fill(child);
            
            NamespaceCompletion comp = new NamespaceCompletion(PopScriptNamespaceCompletionProvider.this, child.getName(), child.getName());
            comp.setIcon(Utils.getNamespaceIcon());
            comps.add(comp);
        });
        
        namespace.getFields().forEach(field -> {
            PopScriptCompletionProvider.parseCompletion(this, comps, field);
        });
        
        namespace.getMacros().forEach(macro -> {
            PopScriptCompletionProvider.parseCompletion(this, comps, macro);
        });
        
        addCompletions(comps);
    }
    
    public final CompletionProvider getProviderFor(JTextComponent comp)
    {
        String[] parts = getAlreadyEnteredNestedText(comp);
        if(parts == null || parts.length < 1)
            return null;
        
        if(parts.length == 1)
            return this;
        
        return getProviderFor(parts, 0);
    }
    
    private PopScriptNamespaceCompletionProvider getProviderFor(String[] parts, int index)
    {
        if(index >= parts.length)
            throw new IllegalStateException();
        
        if(index + 1 == parts.length)
            return this;
        
        PopScriptNamespaceCompletionProvider child = children.getOrDefault(parts[index], null);
        if(child == null)
            return null;
        
        return child.getProviderFor(parts, index + 1);
    }
    
    private String[] getAlreadyEnteredNestedText(JTextComponent comp)
    {
        Segment segment = new Segment();
        Document doc = comp.getDocument();
        int lineEnd = comp.getCaretPosition();
        Element root = doc.getDefaultRootElement();
        int index = root.getElementIndex(lineEnd);
        boolean end = false, foundDot = true;
        LinkedList<String> nestedTexts = new LinkedList<>();

        while(!end && index > 0)
        {
            Element elem = root.getElement(index);
            int lineStart = elem.getStartOffset();
            int lineLen = lineEnd - lineStart;
            try { doc.getText(lineStart, lineLen, segment); }
            catch(BadLocationException ex)
            {
                ex.printStackTrace(System.err);
                return new String[0];
            }

            int segEnd = segment.offset + lineLen;
            int off = segEnd - 1;
            while(!end && off >= segment.offset)
            {
                final char ch = segment.array[off];
                if(isValidChar(ch))
                {
                    if(foundDot)
                        off--;
                    else
                    {
                        if(off + 1 < segEnd)
                        {
                            nestedTexts.addFirst(new String(segment.array, off + 1, segEnd - (off + 1)));
                            segEnd = off;
                        }
                        end = true;
                    }
                }
                else if(isWhitespace(ch))
                {
                    if(off + 1 < segEnd)
                    {
                        nestedTexts.addFirst(new String(segment.array, off + 1, segEnd - (off + 1)));
                        foundDot = false;
                    }
                    segEnd = off;
                    off--;
                }
                else if(ch == '.')
                {
                    if(off + 1 < segEnd)
                        nestedTexts.addFirst(new String(segment.array, off + 1, segEnd - (off + 1)));
                    else if(foundDot)
                    {
                        if(!nestedTexts.isEmpty())
                        {
                            nestedTexts.clear();
                            end = true;
                            break;
                        }
                        else nestedTexts.addFirst(EMPTY_STRING);
                    }

                    foundDot = true;
                    segEnd = off;
                    off--;
                }
                else
                {
                    if(off + 1 < segEnd)
                        nestedTexts.addFirst(new String(segment.array, off + 1, segEnd - (off + 1)));
                    foundDot = false;
                    end = true;
                }
            }

            if(!end)
            {
                if(off + 1 < segEnd)
                {
                    nestedTexts.addFirst(new String(segment.array, off + 1, segEnd - (off + 1)));
                    foundDot = false;
                }

                lineEnd = lineStart - 1;
                index = root.getElementIndex(lineEnd);
            }
        }

        if(foundDot)
            nestedTexts.clear();

        return nestedTexts.isEmpty()
                ? new String[0]
                : nestedTexts.stream().toArray(String[]::new);
    }

    private boolean isWhitespace(char ch)
    {
        return Character.isWhitespace(ch);
    }
}
