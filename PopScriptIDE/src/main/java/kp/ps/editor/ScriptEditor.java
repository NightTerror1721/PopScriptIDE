/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import kp.ps.script.Script;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.ErrorList.ErrorEntry;
import kp.ps.script.compiler.ScriptCompiler;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public class ScriptEditor extends JFrame
{
    private ScriptEditor()
    {
        initComponents();
        setResizable(true);
        Utils.focus(this);
        
        setSize(1280, 720);
        
        initTerminalErrors();
        pack();
    }
    
    public static final void open()
    {
        ScriptEditor editor = new ScriptEditor();
        editor.setVisible(true);
    }
    
    private CodeTextArea createNewPage(String title)
    {
        CodeTextArea codeArea = new CodeTextArea(pages, Objects.requireNonNull(title), this::askSave, this::updateTerminalErrors);
        codeArea.linkToPages();
        return codeArea;
    }
    
    private void save(CodeTextArea area, Path file)
    {
        if(area == null || file == null)
            return;
        
        try(BufferedWriter bw = Files.newBufferedWriter(file))
        {
            area.write(bw);
            area.setFile(file);
            area.setTitle(Utils.getFileName(file));
            area.clearChanges();
            JOptionPane.showMessageDialog(this, "The file has been saved successfully!");
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "There was an error while saving the file.\n" + ex.getMessage(),
                    "Saving Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void save(CodeTextArea area)
    {
        if(area == null)
            return;
        
        Path file;
        if(!area.hasFile())
        {
            file = FileChooser.saveScript(this);
            if(file == null)
                return;
        }
        else file = area.getFile();
        
        save(area, file);
    }
    private void save() { save(getSelectedTextArea()); }
    
    private void saveAs(CodeTextArea area)
    {
        if(area != null)
        {
            Path file = FileChooser.saveScript(this);
            if(file != null)
                save(area, file);
        }
    }
    private void saveAs() { saveAs(getSelectedTextArea()); }
    
    private void askSave(CodeTextArea area)
    {
        if(area != null && area.hasChanges())
        {
            if(JOptionPane.showConfirmDialog(rootPane, "File " + area.getAreaName() +
                " contains changes without saving. Do you want to save them?",
                "Save file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
                save(area);
            }
        }
    }
    private void askSave() { askSave(getSelectedTextArea()); }
    
    private void saveAll()
    {
        int len = getTextAreaCount();
        for(int i = 0; i < len; ++i)
        {
            CodeTextArea area = getTextArea(i);
            if(area.hasChanges())
                save(area);
        }
    }
    
    private void askSaveAll()
    {
        int len = getTextAreaCount();
        for(int i = 0; i < len; ++i)
        {
            CodeTextArea area = getTextArea(i);
            if(area.hasChanges())
                askSave(area);
        }
    }
    
    private void load()
    {
        Path file = FileChooser.openScript(this);
        if(file == null)
            return;
        
        String name = Utils.getFileName(file);
        CodeTextArea area = createNewPage(name);
        try(BufferedReader br = Files.newBufferedReader(file))
        {
            area.read(br, null);
            area.setFile(file);
            area.clearChanges();
            if(getTextAreaCount() > 1)
                pages.setSelectedIndex(getTextAreaCount() - 1);
        }
        catch(IOException ex)
        {
            area.destroy();
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "An error occurred while loading the file.\n" + ex.getMessage(),
                    "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Script compile(CodeTextArea area)
    {
        if(area == null)
            return null;
        
        ErrorList errors = area.getErrors();
        Path areaFile = area.hasFile() ? area.getFile() : null;
        
        errors.clear(); 
        terminalTabs.setSelectedIndex(0);
        StringBuilder sb = new StringBuilder();
        sb.append("Compile Script \"").append(area.getAreaName()).append("\" at ").append(new Date()).append(":\n");
        long t1 = System.currentTimeMillis();
        Script script = ScriptCompiler.compile(area.getText(), errors, areaFile);
        long t2 = System.currentTimeMillis();
        sb.append("COMPILATION ").append(!errors.hasErrors() ? "SUCCESSFUL" : "FAILED")
                .append(" (total time: ").append(t2 - t1).append("ms)");
        if(errors.hasErrors())
            sb.append("\n").append(errors.generateParseResultAsString(area.getParser()));
        /*if(result.hasMessages())
            sb.append("\nCompiler messages:\n").append(result.getMessageLog());*/
        
        terminalText.setText(sb.toString());
        return errors.hasErrors() ? null : script;
    }
    private Script compile() { return compile(getSelectedTextArea()); }
    
    private void compileAndExport(CodeTextArea area)
    {
        if(area == null)
            return;
        
        Script script;
        if((script = compile(area)) == null)
        {
            JOptionPane.showMessageDialog(this, "Cannot export script \"" + area.getAreaName() +
                    "\" because has compilation errors.",
                    "Compilation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Path file = FileChooser.compileAndExport(this);
        if(file == null)
            return;
        
        try
        {
            script.write(file);
            JOptionPane.showMessageDialog(this, "The file has been exported successfully!");
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "There was an error while exporting the file.\n" + ex.getMessage(),
                    "Saving Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private CodeTextArea getTextArea(int index)
    {
        if(index < 0 || index >= pages.getTabCount())
            return null;
        return CodeTextArea.castFrom(pages.getComponentAt(index));
    }
    private CodeTextArea getSelectedTextArea()
    {
        return CodeTextArea.castFrom(pages.getSelectedComponent());
    }
    private int getTextAreaCount() { return pages.getTabCount(); }
    
    public final void repaintCurrentScript()
    {
        CodeTextArea area = getSelectedTextArea();
        if(area != null)
            area.repaint();
    }
    
    private void initTerminalErrors()
    {
        terminalErrors.getColumnModel().getColumn(0).setMinWidth(50);
        terminalErrors.getColumnModel().getColumn(0).setMaxWidth(300);
        terminalErrors.getColumnModel().getColumn(0).setPreferredWidth(175);
        
        terminalErrors.getColumnModel().getColumn(1).setMinWidth(20);
        terminalErrors.getColumnModel().getColumn(1).setMaxWidth(100);
        terminalErrors.getColumnModel().getColumn(1).setPreferredWidth(75);
    }
    
    private void updateTerminalErrors(ErrorList errors)
    {
        DefaultTableModel model = (DefaultTableModel) terminalErrors.getModel();
        int len = errors.getErrorCount();
        
        model.setRowCount(len);
        int off = 0;
        for(ErrorEntry error : errors)
        {
            String filename = error.getFilePath() == null ? "<current-file>" : error.getFilePath().getFileName().toString();
            model.setValueAt(filename, off, 0);
            model.setValueAt(error.getStartLine() + 1, off, 1);
            model.setValueAt(error.getMessage(), off, 2);
            off++;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        pages = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        textFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeElements = new javax.swing.JTree();
        terminalTabs = new javax.swing.JTabbedPane();
        terminalPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        terminalText = new javax.swing.JTextPane();
        helpPanel = new javax.swing.JPanel();
        errorsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        terminalErrors = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jSplitPane1.setDividerLocation(600);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(200, 200));
        jSplitPane2.setPreferredSize(new java.awt.Dimension(1280, 720));

        pages.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jSplitPane2.setRightComponent(pages);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textFilter)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textFilter)
        );

        jScrollPane2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane2.setViewportView(treeElements);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel3);

        jSplitPane1.setTopComponent(jSplitPane2);

        terminalPanel.setLayout(new java.awt.GridLayout(1, 1));

        terminalText.setEditable(false);
        jScrollPane1.setViewportView(terminalText);

        terminalPanel.add(jScrollPane1);

        terminalTabs.addTab("Terminal", terminalPanel);

        helpPanel.setLayout(new java.awt.GridLayout(1, 1));
        terminalTabs.addTab("Help", helpPanel);

        errorsPanel.setLayout(new java.awt.GridLayout(1, 1));

        terminalErrors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Source File", "Line", "Description"
            }
        ));
        terminalErrors.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        terminalErrors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        terminalErrors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        terminalErrors.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(terminalErrors);

        errorsPanel.add(jScrollPane3);

        terminalTabs.addTab("Errors", errorsPanel);

        jSplitPane1.setRightComponent(terminalTabs);

        jMenu1.setText("File");

        jMenuItem1.setText("New Script");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem2.setText("Open Script");
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        jMenuItem3.setText("Import Compiled Script");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Compile and Export Script");
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator3);

        jMenuItem5.setText("Save Script");
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Save all Scripts");
        jMenu1.add(jMenuItem6);

        jMenuItem7.setText("Save Script as...");
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator4);

        jMenuItem8.setText("Properties");
        jMenu1.add(jMenuItem8);
        jMenu1.add(jSeparator5);

        jMenuItem9.setText("Exit");
        jMenu1.add(jMenuItem9);

        menuBar.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem10.setText("Undo");
        jMenu2.add(jMenuItem10);

        jMenuItem11.setText("Redo");
        jMenu2.add(jMenuItem11);
        jMenu2.add(jSeparator6);

        jMenuItem12.setText("Cut");
        jMenu2.add(jMenuItem12);

        jMenuItem13.setText("Copy");
        jMenu2.add(jMenuItem13);

        jMenuItem14.setText("Paste");
        jMenu2.add(jMenuItem14);
        jMenu2.add(jSeparator7);

        jMenuItem15.setText("Compile");
        jMenu2.add(jMenuItem15);

        menuBar.add(jMenu2);

        jMenu3.setText("Search");

        jMenuItem16.setText("Find");
        jMenu3.add(jMenuItem16);

        jMenuItem17.setText("Replace");
        jMenu3.add(jMenuItem17);

        jMenuItem18.setText("Go to line");
        jMenu3.add(jMenuItem18);

        menuBar.add(jMenu3);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        createNewPage("NewScript");
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel errorsPanel;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane pages;
    private javax.swing.JTable terminalErrors;
    private javax.swing.JPanel terminalPanel;
    private javax.swing.JTabbedPane terminalTabs;
    private javax.swing.JTextPane terminalText;
    private javax.swing.JTextField textFilter;
    private javax.swing.JTree treeElements;
    // End of variables declaration//GEN-END:variables
}
