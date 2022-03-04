package kp.le.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Marc
 */
public final class Utils
{
    private Utils() {}
    
    private static final Charset CP_1252 = Charset.forName("Cp1252");
    public static final Charset POP_LANG_CHARSET = CP_1252;
    
    public static final String LANGUAGE_FILE_FORMAT_NAME = "dat";
    public static final String LANGUAGE_FILE_FORMAT = "." + LANGUAGE_FILE_FORMAT_NAME;
    
    private static Image LOGO;
    
    
    public static final int max(IntStream stream)
    {
        return stream.reduce(Integer.MIN_VALUE, Math::max);
    }
    
    public static final <T> int max(Stream<T> stream, ToIntFunction<T> mapper)
    {
        return max(stream.mapToInt(mapper));
    }
    
    public static final <T> int max(Collection<T> c, ToIntFunction<T> mapper)
    {
        return max(c.stream(), mapper);
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
}
