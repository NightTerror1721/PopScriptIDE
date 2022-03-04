package kp.ps.editor.highlight;

import java.util.Set;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

/**
 *
 * @author Marc
 */
public final class PopScriptTokenMakerFactory extends TokenMakerFactory
{
    private final TokenMakerFactory factory = TokenMakerFactory.getDefaultInstance();
    private final HighlightManager highlightManager = new HighlightManager();
    
    public final void setHighlightManagerRoot(HighlightNamespace element)
    {
        highlightManager.setRoot(element);
    }
    
    @Override
    protected final TokenMaker getTokenMakerImpl(String key)
    {
        TokenMaker maker = factory.getTokenMaker(key);
        if(maker instanceof PopScriptTokenMaker)
            ((PopScriptTokenMaker) maker).setHighlightManager(highlightManager);
        return maker;
    }

    @Override
    public final Set<String> keySet() { return factory.keySet(); }
}
