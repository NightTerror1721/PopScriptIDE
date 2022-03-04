package kp.ps.editor.highlight;

import kp.ps.script.compiler.functions.Macro;

/**
 *
 * @author Marc
 */
public class HighlightMacro extends HighlightElement
{
    HighlightMacro(Macro macro)
    {
        super(macro.getName());
    }
    
    @Override
    public final HighlightElementType getType() { return HighlightElementType.MACRO; }
}
