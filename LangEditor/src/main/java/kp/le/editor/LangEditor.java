package kp.le.editor;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import kp.le.langfile.LanguageRepository;
import kp.le.util.Utils;

/**
 *
 * @author Marc
 */
public class LangEditor extends JFrame
{
    private final LanguageRepository repository;
    private final LineTable generalTable;
    private final LineTable messageTable;
    private final LineTable levelTable;
    
    private boolean modified;
    
    private Path selectedFile;
    private String loadedFileName;
    
    private LangEditor()
    {
        this.repository = new LanguageRepository();
        this.generalTable = new LineTable(this, repository);
        this.messageTable = new LineTable(this, repository, EntryType.MESSAGE);
        this.levelTable = new LineTable(this, repository, EntryType.LEVEL);
        
        initComponents();
        Utils.focus(this);
        
        init();
        pack();
    }
    
    public static final void open()
    {
        EventQueue.invokeLater(() ->
        {
            var editor = new LangEditor();
            editor.setVisible(true);
        });
    }
    
    private void init()
    {
        tabs.addTab("General", generalTable);
        tabs.addTab("Dialog Messages", messageTable);
        tabs.addTab("Level Names", levelTable);
        Utils.setIcon(this);
        updateTitle();
    }
    
    private void buildEntries()
    {
        generalTable.buildEntries();
        messageTable.buildEntries();
        levelTable.buildEntries();
    }
    
    private void save(boolean forceFileSelection)
    {
        if(forceFileSelection || selectedFile == null)
        {
            var path = FileChooser.saveLang(this);
            if(path == null)
                return;
            
            selectedFile = path;
            loadedFileName = selectedFile.getFileName().toString();
        }
        
        try
        {
            repository.write(selectedFile);
            modified = false;
            updateTitle();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during file save: " + ex.getMessage(),
                    "File save error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveAs()
    {
        save(true);
    }
    
    private boolean saveIfIsNeeded()
    {
        if(modified)
        {
            int opt = JOptionPane.showConfirmDialog(this, "Do you want to save the changes?",
                    "Save file", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            
            return switch(opt)
            {
                case JOptionPane.YES_OPTION -> {
                    save(false);
                    yield true;
                }
                    
                case JOptionPane.NO_OPTION -> true;
                    
                default -> false;
            };
        }
        
        return true;
    }
    
    private void load()
    {
        if(!saveIfIsNeeded())
            return;
        
        var path = FileChooser.openLang(this);
        if(path == null)
            return;
        
        selectedFile = path;
        loadedFileName = selectedFile.getFileName().toString();
        
        try
        {
            repository.read(selectedFile);
            selectedFile = null;
            modified = false;
            buildEntries();
            updateTitle();
            repaint();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during file load: " + ex.getMessage(),
                    "File load error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void newLangFile()
    {
        if(!saveIfIsNeeded())
            return;
        
        repository.readTemplate();
        selectedFile = null;
        loadedFileName = null;
        modified = true;
        buildEntries();
        updateTitle();
        repaint();
    }
    
    public final void markAsModified()
    {
        modified = true;
        updateTitle();
    }
    
    private void updateTitle()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("PopLangEditor");
        
        if(!repository.isEmpty())
        {
            sb.append(" - ");
            
            if(loadedFileName == null)
                sb.append("<new file>");
            else
            {
                sb.append(loadedFileName);
                if(modified)
                    sb.append(" [NOT SAVED CHANGES]");
            }
        }
        
        setTitle(sb.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Open");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Save");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem4.setText("Save as...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator3);

        jMenuItem5.setText("Exit");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        newLangFile();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        load();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        save(false);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        saveAs();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if(!saveIfIsNeeded())
            return;
        
        dispose();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(!saveIfIsNeeded())
            return;
        
        dispose();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
