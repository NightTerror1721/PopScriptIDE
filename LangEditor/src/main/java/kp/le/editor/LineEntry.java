package kp.le.editor;

import java.util.Objects;
import javax.swing.JPanel;
import kp.le.langfile.LanguageRepository.LineReference;

/**
 *
 * @author Marc
 */
public class LineEntry extends JPanel
{
    private final LangEditor editor;
    private final LineReference line;
    private final int id;
    private final EntryType type;
    
    public LineEntry(LangEditor editor, LineReference line)
    {
        this.editor = Objects.requireNonNull(editor);
        this.line = Objects.requireNonNull(line);
        
        var data = line.getData();
        this.id = data.id();
        this.type = data.type();
        
        initComponents();
        
        switch(type)
        {
            case NORMAL -> lTextId.setText("Text: " + id);
            case MESSAGE -> lTextId.setText("Msg ID: " + id);
            case LEVEL -> lTextId.setText("Level ID: " + id);
        }
        
        update();
    }
    
    public final LangEditor getEditor() { return editor; }
    public final LineReference getLine() { return line; }
    public final int getId() { return id; }
    public final EntryType getType() { return type; }
    
    public final void update()
    {
        tText.setText(line.get());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bEdit = new javax.swing.JButton();
        tText = new javax.swing.JTextField();
        lTextId = new javax.swing.JLabel();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        bEdit.setText("...");
        bEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEditActionPerformed(evt);
            }
        });

        tText.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lTextId, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tText, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tText)
                    .addComponent(bEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lTextId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEditActionPerformed
        LineEdit.open(this);
    }//GEN-LAST:event_bEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bEdit;
    private javax.swing.JLabel lTextId;
    private javax.swing.JTextField tText;
    // End of variables declaration//GEN-END:variables
}
