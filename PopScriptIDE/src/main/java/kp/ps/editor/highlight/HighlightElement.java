package kp.ps.editor.highlight;

import java.util.Objects;

/**
 *
 * @author Marc
 */
public abstract class HighlightElement
{
    protected final String identifier;
    
    protected HighlightElement(String identifier)
    {
        this.identifier = Objects.requireNonNull(identifier);
    }
    
    public final String getIdentifier() { return identifier; }
    
    public final boolean isField() { return getType() == HighlightElementType.FIELD; }
    public final boolean isMacro() { return getType() == HighlightElementType.MACRO; }
    public final boolean isNamespace() { return getType() == HighlightElementType.NAMESPACE; }
    
    public abstract HighlightElementType getType();
    
    public HighlightElement getChild(String identifier) { throw new UnsupportedOperationException(); }
}
