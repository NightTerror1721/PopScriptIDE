package kp.ps.editor.highlight;

import java.util.HashMap;
import kp.ps.script.namespace.Namespace;

/**
 *
 * @author Marc
 */
public class HighlightNamespace extends HighlightElement
{
    private final HashMap<String, HighlightElement> children = new HashMap<>();
    
    public HighlightNamespace(Namespace namespace)
    {
        super(namespace.isGlobal() ? "<global>" : namespace.getName());
        namespace.getChildrenNamespace().forEach(nm -> children.put(nm.getName(), new HighlightNamespace(nm)));
        namespace.getFields().forEach(field -> children.put(field.getName(), new HighlightField(field)));
        namespace.getMacros().forEach(macro -> children.put(macro.getName(), new HighlightMacro(macro)));
    }
    
    @Override
    public final HighlightElementType getType() { return HighlightElementType.NAMESPACE; }
    
    @Override
    public final HighlightElement getChild(String identifier)
    {
        return children.getOrDefault(identifier, null);
    }
}
