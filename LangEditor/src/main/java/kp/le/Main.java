package kp.le;

import java.io.IOException;
import kp.le.editor.LangEditor;
import kp.le.util.Utils;

/**
 *
 * @author Marc
 */
public class Main
{
    public static void main(String[] args) throws IOException
    {
        Utils.useSystemLookAndFeel();
        LangEditor.open();
    }
}
