/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.types.TypeId;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;

/**
 *
 * @author Marc
 */
public final class Utils
{
    private Utils() {}
    
    public static final String POPSCRIPT_TEXT_TYPE = "text/popscript";
    public static final String LANGUAGE_FILE_FORMAT_NAME = "spopscr";
    public static final String LANGUAGE_FILE_FORMAT = '.' + LANGUAGE_FILE_FORMAT_NAME;
    private static Image LOGO;
    
    private static Icon ICON_NAMESPACE;
    private static Icon ICON_FUNCTION;
    private static Icon ICON_FIELD;
    private static Icon ICON_MACRO;
    private static Icon ICON_FIELDS_CATEGORY;
    private static Icon ICON_FUNCTIONS_CATEGORY;
    private static Icon ICON_MACROS_CATEGORY;
    
    
    public static byte clamp(byte value, int min, int max)
    {
        return (byte) Math.max(min, Math.min(max, value));
    }
    
    public static short clamp(short value, int min, int max)
    {
        return (short) Math.max(min, Math.min(max, value));
    }
    
    public static int clamp(int value, int min, int max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static long clamp(long value, long min, long max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static float clamp(float value, float min, float max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }
    
    public static final <T> T self(T value) { return value; }
    
    public static final <L, R> ZippedIterator<L, R> iteratorOf(Iterator<L> left, Iterator<R> right)
    {
        return new ZippedIterator<>(left, right);
    }
    
    public static final <L, R> ZippedIterator<L, R> iteratorOf(Iterable<L> left, Iterable<R> right)
    {
        return new ZippedIterator<>(left.iterator(), right.iterator());
    }
    
    public static final <L, R> Iterable<Pair<L, R>> iterableOf(Iterator<L> left, Iterator<R> right)
    {
        return () -> new ZippedIterator(left, right);
    }
    
    public static final <L, R> Iterable<Pair<L, R>> iterableOf(Iterable<L> left, Iterable<R> right)
    {
        return () -> new ZippedIterator(left.iterator(), right.iterator());
    }
    
    public static final String stringDup(char character, int count)
    {
        if(count < 1)
            return "";
        
        if(count == 1)
            return Character.toString(character);
        
        char[] buf = new char[count];
        Arrays.fill(buf, character);
        return new String(buf);
    }
    
    public static final void setDefaultLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    
    public static final void initPopScriptLanguage()
    {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping(POPSCRIPT_TEXT_TYPE, "kp.ps.editor.PopScriptHighlight");
        
        FoldParserManager.get().addFoldParserMapping(POPSCRIPT_TEXT_TYPE, new CurlyFoldParser(true, false));
    }
    
    public static final void useSystemLookAndFeel()
    {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) { ex.printStackTrace(System.err); }
    }
    
    public static void focus(Window frame)
    {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = frame.getSize();
        frame.setLocation((screen.width - window.width) / 2,
                        (screen.height - window.height) / 2);
    }
    
    public static void focus(JDialog dialog)
    {
        Container parent = dialog.getParent();
        if(!(parent instanceof JDialog) && !(parent instanceof JFrame))
        {
            focus((Window) dialog);
            return;
        }
        
        Point p = parent.getLocation();
        Dimension screen = parent.getSize();
        Dimension window = dialog.getSize();
        p.x += (screen.width - window.width) / 2;
        p.y += (screen.height - window.height) / 2;
        
        dialog.setLocation(p);
    }
    
    public static final void setIcon(JFrame frame)
    {
        if(LOGO == null)
        {
            try { LOGO = ImageIO.read(getClasspathResourceUrl("/logo.png")); }
            catch(IOException ex) { ex.printStackTrace(System.err); }
        }
        if(LOGO != null)
            frame.setIconImage(LOGO);
    }
    
    public static final Path getUserDirectory()
    {
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }
    
    public static final URL getClasspathResourceUrl(String path)
    {
        if(!path.startsWith("/"))
            path = "/" + path;
        return Utils.class.getResource(path);
    }
    
    public static final InputStream getClasspathResourceAsStream(String path)
    {
        if(!path.startsWith("/"))
            path = "/" + path;
        return Utils.class.getResourceAsStream(path);
    }
    
    public static final String getFileName(Path file)
    {
        String name = file.getFileName().toString();
        int index = name.lastIndexOf('.');
        return index < 0 ? name : name.substring(0, index);
    }
    
    public static final String getFileExtension(Path file)
    {
        String name = file.getFileName().toString();
        int index = name.lastIndexOf('.');
        return index < 0 ? "" : name.substring(index + 1);
    }
    
    public static final void generateFunctionsFile(Path path) { ScriptToken.generateFunctionsFile(path); }
    
    public static final void generateInternalAndTypeValuesFile(Path path)
    {
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path)))
        {
            for(ScriptInternal internal : ScriptInternal.values())
                pw.println(internal.getInternalName());
            
            TypedValue.all().stream()
                    .filter(value -> (value.getType() != TypeId.STATE && value.getType() != TypeId.ACTION))
                    .forEachOrdered(value -> pw.println(value.getToken().getLangName()));
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    
    private static Icon loadIcon(String name)
    {
        try
        {
            Image image = ImageIO.read(getClasspathResourceAsStream("/kp/ps/editor/icons/" + name));
            return new ImageIcon(image);
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
            return null;
        }
    }
    
    public static final Icon getNamespaceIcon()
    {
        if(ICON_NAMESPACE == null)
            ICON_NAMESPACE = loadIcon("namespace.gif");
        return ICON_NAMESPACE;
    }
    
    public static final Icon getFieldIcon()
    {
        if(ICON_FIELD == null)
            ICON_FIELD = loadIcon("field.gif");
        return ICON_FIELD;
    }
    
    public static final Icon getFunctionIcon()
    {
        if(ICON_FUNCTION == null)
            ICON_FUNCTION = loadIcon("function.gif");
        return ICON_FUNCTION;
    }
    
    public static final Icon getMacroIcon()
    {
        if(ICON_MACRO == null)
            ICON_MACRO = loadIcon("macro.gif");
        return ICON_MACRO;
    }
    
    public static final Icon getFieldsCategoryIcon()
    {
        if(ICON_FIELDS_CATEGORY == null)
            ICON_FIELDS_CATEGORY = loadIcon("fields_category.gif");
        return ICON_FIELDS_CATEGORY;
    }
    
    public static final Icon getFunctionsCategoryIcon()
    {
        if(ICON_FUNCTIONS_CATEGORY == null)
            ICON_FUNCTIONS_CATEGORY = loadIcon("functions_category.gif");
        return ICON_FUNCTIONS_CATEGORY;
    }
    
    public static final Icon getMacrosCategoryIcon()
    {
        if(ICON_MACROS_CATEGORY == null)
            ICON_MACROS_CATEGORY = loadIcon("macros_category.gif");
        return ICON_MACROS_CATEGORY;
    }
    
    public static final Icon getNamespacesCategoryIcon()
    {
        return getNamespaceIcon();
    }
}
