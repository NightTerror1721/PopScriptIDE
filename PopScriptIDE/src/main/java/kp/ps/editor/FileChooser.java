/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public final class FileChooser
{
    private FileChooser() {}
    
    private static JFileChooser SCRIPTS;
    private static JFileChooser scripts()
    {
        if(SCRIPTS == null)
        {
            SCRIPTS = new JFileChooser(Utils.getUserDirectory().toFile());
            SCRIPTS.setAcceptAllFileFilterUsed(true);
            SCRIPTS.setFileSelectionMode(JFileChooser.FILES_ONLY);
            SCRIPTS.setMultiSelectionEnabled(false);
            SCRIPTS.setFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f) { return f.isDirectory() || f.getName().endsWith(Utils.LANGUAGE_FILE_FORMAT); }

                @Override
                public String getDescription()
                {
                    return "SmartPopScript (" + Utils.LANGUAGE_FILE_FORMAT + ")";
                }
            });
        }
        return SCRIPTS;
    }
    
    private static JFileChooser DC_SCRIPTS;
    private static JFileChooser dcscripts()
    {
        if(DC_SCRIPTS == null)
        {
            DC_SCRIPTS = new JFileChooser(Utils.getUserDirectory().toFile());
            DC_SCRIPTS.setAcceptAllFileFilterUsed(true);
            DC_SCRIPTS.setFileSelectionMode(JFileChooser.FILES_ONLY);
            DC_SCRIPTS.setMultiSelectionEnabled(false);
            DC_SCRIPTS.setFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f) { return f.isDirectory() || f.getName().endsWith(".dat"); }

                @Override
                public final String getDescription() { return "Compiled Script (.dat)"; }

            });
        }
        return DC_SCRIPTS;
    }
    
    private static Path insertFormat(Path path, String format)
    {
        path = path.toAbsolutePath();
        if(!path.getFileName().endsWith(format))
            path = new File(path.toFile().getName() + format).getAbsoluteFile().toPath();
        return path;
    }
    
    public static final Path openScript(Component parent)
    {
        JFileChooser chooser = scripts();
        if(chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return chooser.getSelectedFile().toPath();
    }
    
    public static final Path saveScript(Component parent)
    {
        JFileChooser chooser = scripts();
        if(chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return insertFormat(chooser.getSelectedFile().toPath(), Utils.LANGUAGE_FILE_FORMAT);
    }
    
    public static final Path importCompiled(Component parent)
    {
        JFileChooser chooser = dcscripts();
        if(chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return chooser.getSelectedFile().toPath();
    }
    
    public static final Path compileAndExport(Component parent)
    {
        JFileChooser chooser = dcscripts();
        if(chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return insertFormat(chooser.getSelectedFile().toPath(), ".dat");
    }
}
