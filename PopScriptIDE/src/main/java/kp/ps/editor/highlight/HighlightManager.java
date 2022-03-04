package kp.ps.editor.highlight;

/**
 *
 * @author Marc
 */
public final class HighlightManager
{
    private HighlightNamespace root;
    
    public final void setRoot(HighlightNamespace element)
    {
        this.root = element;
    }
    
    public final HighlightElementType findIdentifierType(String... parts)
    {
        if(root == null || parts == null || parts.length < 1)
            return null;
        
        HighlightElement currentElement = root;
        for(int i = 0; i < parts.length; ++i)
        {
            HighlightElement child = currentElement.getChild(parts[i]);
            if(child == null)
                return null;
            
            currentElement = child;
        }
        
        return currentElement == null ? null : currentElement.getType();
    }
}
