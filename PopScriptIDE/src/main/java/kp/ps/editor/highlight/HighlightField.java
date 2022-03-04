package kp.ps.editor.highlight;

import kp.ps.script.namespace.NamespaceField;

/**
 *
 * @author Marc
 */
public class HighlightField extends HighlightElement
{
    HighlightField(NamespaceField field)
    {
        super(field.getName());
    }
    
    @Override
    public final HighlightElementType getType() { return HighlightElementType.FIELD; }
}
