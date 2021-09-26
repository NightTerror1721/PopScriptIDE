/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import static kp.ps.editor.HelpElementsManager.ICON_FIELDS_TAG;
import static kp.ps.editor.HelpElementsManager.ICON_FUNCTIONS_TAG;
import static kp.ps.editor.HelpElementsManager.ICON_GLOBAL_FIELDS;
import static kp.ps.editor.HelpElementsManager.ICON_GLOBAL_FUNCTIONS;
import static kp.ps.editor.HelpElementsManager.ICON_MACROS_TAG;
import static kp.ps.editor.HelpElementsManager.ICON_NAMESPACES_TAG;
import static kp.ps.editor.HelpElementsManager.ICON_USER_DEFINED;
import kp.ps.utils.Utils;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

/**
 *
 * @author Marc
 */
public class ElementHelperTreeCellRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, selected,expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Icon icon = null;

        if(!tree.getModel().getRoot().equals(node))
        {
            Object data = node.getUserObject();
            if(data != null)
            {
                if(data instanceof String)
                {
                    switch((String) data)
                    {
                        case ICON_USER_DEFINED:
                        case ICON_GLOBAL_FIELDS:
                        case ICON_GLOBAL_FUNCTIONS:
                        case ICON_NAMESPACES_TAG:
                        case ICON_FIELDS_TAG:
                        case ICON_FUNCTIONS_TAG:
                        case ICON_MACROS_TAG:
                            icon = Utils.getFolderIcon();
                            break;
                    }
                }
                else if(data instanceof MacroCompletion)
                    icon = Utils.getMacroIcon();
                else if(data instanceof FunctionCompletion)
                    icon = Utils.getFunctionIcon();
                else if(data instanceof VariableCompletion)
                    icon = Utils.getFieldIcon();
                else if(data instanceof NamespaceCompletion)
                    icon = Utils.getNamespaceIcon();
            }
        }

        setIcon(icon);
        return this;
    }
}
