/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import kp.ps.editor.completion.PopScriptNamespaceCompletionProvider;
import kp.ps.editor.completion.MacroCompletion;
import kp.ps.editor.completion.PopScriptCompletionProvider;
import kp.ps.editor.completion.PopScriptBaseCompletionProvider;
import kp.ps.editor.completion.NamespaceCompletion;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

/**
 *
 * @author Marc
 */
public class HelpElementsManager
{
    public static final String ICON_NAMESPACES_TAG = "Namespaces";
    public static final String ICON_FIELDS_TAG = "Fields";
    public static final String ICON_FUNCTIONS_TAG = "Functions";
    public static final String ICON_MACROS_TAG = "Macros";
    
    public static final String ICON_GLOBAL_FUNCTIONS = "Global Functions";
    public static final String ICON_GLOBAL_FIELDS = "Global Fields";
    public static final String ICON_USER_DEFINED = "User defined";
    
    private static DefaultMutableTreeNode rootNode;
    private static DefaultMutableTreeNode customsNode;
    private static DefaultTreeModel model;
    
    private final JTree tree;
    
    
    private final PopScriptCompletionProvider rootCompletionProvider;
    
    public HelpElementsManager(JTree tree, PopScriptCompletionProvider rootCompletionProvider)
    {
        this.tree = Objects.requireNonNull(tree);
        this.rootCompletionProvider = Objects.requireNonNull(rootCompletionProvider);
        
        init();
    }
    
    public final void update()
    {
        //Enumeration<TreePath> expanded = tree.getExpandedDescendants(new TreePath(customsNode.getPath()));
        
        fillCustoms(rootCompletionProvider.getBaseProvider(), customsNode);
        model.nodeStructureChanged(customsNode);
        expandAllNodes(customsNode);
        
        
        /*if(expanded != null)
        {
            TreePath customRootPath = new TreePath(customsNode.getPath());
            while(expanded.hasMoreElements())
            {
                TreePath path = expanded.nextElement();
                if(customRootPath.isDescendant(path))
                    tree.expandPath(path);
            }
        }
        model.nodeChanged(customsNode);*/
    }
    
    private static int sortCompletions(Completion c0, Completion c1)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(c0.getReplacementText(), c1.getReplacementText());
    }
    
    private void init()
    {
        if(rootNode == null)
        {
            rootNode = new DefaultMutableTreeNode("<root>", true);
            customsNode = new DefaultMutableTreeNode("User defined", true);
            
            rootNode.add(customsNode);

            model = new DefaultTreeModel(rootNode);
            tree.setModel(model);
            tree.setRootVisible(false);
            tree.setShowsRootHandles(true);
            model.nodeStructureChanged(rootNode);
            
            PopScriptBaseCompletionProvider globalsProvider = PopScriptCompletionProvider.getRootCompletionProvider();
            DefaultMutableTreeNode fields = new DefaultMutableTreeNode(ICON_GLOBAL_FIELDS, true);
            DefaultMutableTreeNode functions = new DefaultMutableTreeNode(ICON_GLOBAL_FUNCTIONS, true);

            globalsProvider.getCompletions().stream()
                    .sorted(HelpElementsManager::sortCompletions)
                    .forEach(completion -> {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(completion, false);
                        if(completion instanceof FunctionCompletion)
                            functions.add(node);
                        else if(completion instanceof VariableCompletion)
                            fields.add(node);
                    });

            rootNode.add(fields);
            rootNode.add(functions);
            
            model.nodeStructureChanged(rootNode);
        }
    }
    
    private void fillCustoms(PopScriptNamespaceCompletionProvider provider, DefaultMutableTreeNode root)
    {
        /*DefaultMutableTreeNode namespaces = new DefaultMutableTreeNode(ICON_NAMESPACES_TAG, true);
        DefaultMutableTreeNode fields = new DefaultMutableTreeNode(ICON_FIELDS_TAG, true);
        DefaultMutableTreeNode functions = new DefaultMutableTreeNode(ICON_FUNCTIONS_TAG, true);
        DefaultMutableTreeNode macros = new DefaultMutableTreeNode(ICON_MACROS_TAG, true);*/
        
        LinkedList<DefaultMutableTreeNode> namespaces = new LinkedList<>();
        LinkedList<DefaultMutableTreeNode> fields = new LinkedList<>();
        LinkedList<DefaultMutableTreeNode> functions = new LinkedList<>();
        LinkedList<DefaultMutableTreeNode> macros = new LinkedList<>();
        
        Map<String, PopScriptNamespaceCompletionProvider> children = provider.getChildren();
        
        provider.getCompletions().stream()
                .sorted(HelpElementsManager::sortCompletions)
                .forEach(completion -> {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(completion, false);
                    if(completion instanceof MacroCompletion)
                    {
                        macros.add(node);
                    }
                    else if(completion instanceof FunctionCompletion)
                    {
                        functions.add(node);
                    }
                    else if(completion instanceof VariableCompletion)
                    {
                        fields.add(node);
                    }
                    else if(completion instanceof NamespaceCompletion)
                    {
                        node.setAllowsChildren(true);
                        NamespaceCompletion ncomp = (NamespaceCompletion) completion;
                        if(ncomp.getName() != null && !ncomp.getName().isEmpty())
                        {
                            PopScriptNamespaceCompletionProvider prov = children.getOrDefault(ncomp.getName(), null);
                            if(prov != null)
                            {
                                namespaces.add(node);
                                fillCustoms(prov, node);
                            }
                        }
                    }
                });
        
        root.removeAllChildren();
        
        fillCompletions(root, fields);
        fillCompletions(root, macros);
        fillCompletions(root, functions);
        fillCompletions(root, namespaces);
    }
    
    private static void fillCompletions(DefaultMutableTreeNode root, List<DefaultMutableTreeNode> list)
    {
        if(!list.isEmpty())
            list.forEach(root::add);
    }
    
    private void expandAllNodes(TreeNode root)
    {
        Enumeration<? extends TreeNode> children = root.children();
        while(children.hasMoreElements())
        {
            TreeNode node = children.nextElement();
            tree.expandPath(new TreePath(((DefaultMutableTreeNode) node).getPath()));
            if(node.getChildCount() > 0)
                expandAllNodes(node);
        }
    }
    
    private static Map<String, DefaultMutableTreeNode> extractNodes(DefaultMutableTreeNode base)
    {
        HashMap<String, DefaultMutableTreeNode> map = new HashMap<>();
        Enumeration<TreeNode> children = base.children();
        if(children != null)
        {
            while(children.hasMoreElements())
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                Completion comp = (Completion) node.getUserObject();
                map.put(comp.getReplacementText(), node);
            }
        }
        return map;
    }
    
    private static DefaultMutableTreeNode findCategory(DefaultMutableTreeNode root, String tag)
    {
        int len = root.getChildCount();
        for(int i = 0; i < len; ++i)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if(node.getUserObject().equals(tag))
                return node;
        }
        return new DefaultMutableTreeNode(tag, true);
    }
}
