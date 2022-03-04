/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.le.editor;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import kp.le.util.Utils;

/**
 *
 * @author Marc
 */
public final class FileChooser
{
    private FileChooser() {}
    
    private static JFileChooser LANGS;
    private static JFileChooser langs()
    {
        if(LANGS == null)
        {
            LANGS = new JFileChooser(Utils.getUserDirectory().toFile());
            LANGS.setAcceptAllFileFilterUsed(true);
            LANGS.setFileSelectionMode(JFileChooser.FILES_ONLY);
            LANGS.setMultiSelectionEnabled(false);
            LANGS.setFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f) { return f.isDirectory() || f.getName().endsWith(Utils.LANGUAGE_FILE_FORMAT); }

                @Override
                public String getDescription()
                {
                    return "Language File format (" + Utils.LANGUAGE_FILE_FORMAT + ")";
                }
            });
        }
        return LANGS;
    }
    
    private static Path insertFormat(Path path, String format)
    {
        path = path.toAbsolutePath();
        if(!path.getFileName().toString().endsWith(format))
            path = new File(path.toFile().getName() + format).getAbsoluteFile().toPath();
        return path;
    }
    
    public static final Path openLang(Component parent)
    {
        JFileChooser chooser = langs();
        if(chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return chooser.getSelectedFile().toPath();
    }
    
    public static final Path saveLang(Component parent)
    {
        JFileChooser chooser = langs();
        if(chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;
        return insertFormat(chooser.getSelectedFile().toPath(), Utils.LANGUAGE_FILE_FORMAT);
    }
}
