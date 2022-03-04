package kp.ps.editor.highlight;

import java.util.LinkedList;
import java.util.Objects;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

/**
 *
 * @author Marc
 */
public class PopScriptTokenMaker extends PopScriptHighlight
{
    private HighlightManager highlightManager;
    
    final void setHighlightManager(HighlightManager highlightManager)
    {
        this.highlightManager = Objects.requireNonNull(highlightManager);
    }
    
    @Override
    public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink)
    {
        if(tokenType == TokenTypes.IDENTIFIER)
            tokenType = findIdentifierType(array, start, end);
        
        super.addToken(array, start, end, tokenType, startOffset, hyperlink);
    }
    
    private int findIdentifierType(char[] array, int start, int end)
    {
        if(highlightManager == null)
            return TokenTypes.IDENTIFIER;
        
        String[] parts = generateCompleteElement(array, start, end);
        HighlightElementType type = highlightManager.findIdentifierType(parts);
        if(type == null)
            return TokenTypes.IDENTIFIER;
        
        switch(type)
        {
            case FIELD: return TokenTypes.RESERVED_WORD_2;
            case MACRO: return TokenTypes.FUNCTION;
            case NAMESPACE: return TokenTypes.DATA_TYPE;
        }
        
        return TokenTypes.IDENTIFIER;
    }
    
    private String[] generateCompleteElement(char[] array, int start, int end)
    {
        start = findStartPoint(array, start);
        
        int offset = start;
        int len = 0;
        LinkedList<String> parts = new LinkedList<>();
        for(int i = start; i <= end; ++i)
        {
            char c = array[i];
            if(isIdentifierSeparator(c))
            {
                if(len <= 0)
                    return null;
                
                parts.add(new String(array, offset, len));
                offset = i + 1;
                len = 0;
            }
            else if(isValidChar(c))
                len++;
            else
                return null;
        }
        
        if(len <= 0)
            return null;
        parts.add(new String(array, offset, len));
        
        return parts.stream().toArray(String[]::new);
    }
    
    private int findStartPoint(char[] array, int end)
    {
        for(int i = end - 1; i >= 0; --i)
        {
            if(!isValidChar(array[i]) && !isIdentifierSeparator(array[i]))
                return i + 1;
        }
        return 0;
    }
    
    private static boolean isValidChar(char ch)
    {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }
    
    private static boolean isIdentifierSeparator(char ch)
    {
        return ch == '.';
    }
}
