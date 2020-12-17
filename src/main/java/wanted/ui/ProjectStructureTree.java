package wanted.ui;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import wanted.refactoring.BaseRefactorAction;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;


/**
 * A tree GUI for our Project Structure plugin. It displays the corresponding name and icon for the nodes in our tree
 * model using a custom cell renderer. Note that each node is an instance of DefaultMutableTreeNode, and its user data
 * is an instance of Project, String (Refactoring Technique Categories), or PsiElements including PsiClass, PsiMethod, or PsiStatement.
 * The tree GUI detect detects double-click mouse events for PsiElement nodes,
 * and shows the corresponding PsiElements in the editor. Finally,
 * whenever the underlying project changes, the corresponding node of the tree GUI is automatically chosen.
 *
 * @author Mintae Kim
 * @author seungjae yoo
 * @author seha Park
 * @author Jinu Noh
 * @author Chanyoung Kim
 * @author CSED332 2020 TAs
 */
class ProjectStructureTree extends Tree {

    private static final Icon Icon1 = IconLoader.getIcon("/general/projectStructure.svg");
    private static final Icon Icon2 = IconLoader.getIcon("/nodes/configFolder.svg");
    private static final Icon Icon3 = IconLoader.getIcon("/nodes/editorconfig.svg");

    /**
     * Creates a project structure tree for a given project.
     *
     * @param project a project
     */
    ProjectStructureTree(@NotNull Project project) {
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));

        // Set a cell renderer to display the name and icon of each node
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                              boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode) {
                    Object v = ((DefaultMutableTreeNode) value).getUserObject();
                    if (v instanceof Project) { // Project
                        setIcon(Icon1);
                        append(((Project) v).getName());
                    }
                    else if (v instanceof BaseRefactorAction){ // Category: Refactoring
                        setIcon(Icon2);
                        append(((BaseRefactorAction) v).storyName());
                    }
                    else if (v instanceof PsiField) {
                        setIcon(Icon3);
                        String fileName = ((PsiField) v).getContainingFile().getName();
                        append("[" + fileName + "] " + ((PsiField)v).getName());
                    }
                    else if (v instanceof PsiClass) {
                        setIcon(Icon3);

                        if (((PsiClass) v).getName() == null) { // Anonymous Class
                            append("Anonymous class");
                        } else {
                            append(((PsiClass) v).getName());
                        }
                    }
                    else if (v instanceof PsiMethod) {
                        setIcon(Icon3);
                        append(((PsiMethod) v).getName());
                    }
                    else if (v instanceof PsiStatement) {
                        setIcon(Icon3);

                        String fileName = ((PsiStatement) v).getContainingFile().getName();
                        String fileText = ((PsiStatement) v).getContainingFile().getText();
                        int offset = ((PsiStatement) v).getTextOffset();
                        int line = 1;
                        for (int i = 0; i < offset; i++) {
                            if (fileText.charAt(i) == '\n') line++;
                        }
                        append("[" + fileName + "]" + " Line " + String.valueOf(line));
                    }
                    else if (v instanceof PsiLiteralExpression){
                        setIcon(Icon3);
                        String fileName = ((PsiLiteralExpression) v).getContainingFile().getName();
                        append("[" + fileName + "] " + ((PsiLiteralExpression)v).getValue().toString());
                    }
                }

            }
        });

        // Set a mouse listener to handle double-click events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath treePath = getClosestPathForLocation(e.getX(), e.getY());
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    Object element = node.getUserObject();

                    if (element != null) {
                        if (element instanceof NavigatablePsiElement) {
                            ((NavigatablePsiElement) element).navigate(true);
                        }
                        else if (element instanceof PsiStatement) {
                            VirtualFile f = ((PsiStatement) element).getContainingFile().getVirtualFile();
                            int offset = ((PsiStatement) element).getTextOffset();
                            OpenFileDescriptor fd = new OpenFileDescriptor(project, f, offset);
                            fd.navigate(true);
                        }
                    }
                }

                if (SwingUtilities.isRightMouseButton(e)){
                    TreePath treePath = getClosestPathForLocation(e.getX(), e.getY());
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    Object element = node.getUserObject();

                    RefactorPopUp menu = new RefactorPopUp(element);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Set a Psi tree change listener to handle changes in the project. We provide code for obtaining an instance
        // of PsiField, PsiMethod, PsiClass, or PsiPackage. Implement the updateTree method below.
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }

            @Override
            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }

            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                getTargetElement(event).ifPresent(target -> updateTree(project, target));
            }
        }, ()->{});
    }

    /**
     * Updates a tree according to the change in the target element, and shows the corresponding node in the Project
     * Structure tree. The simplest way is to reset a model of the tree (using setModel) and then to traverse the tree
     * to find the corresponding node to the target element. Use the methods {@link JTree::setSelectionPath} and
     * {@link JTree::scrollPathToVisibles} to display the corresponding node in GUI.
     *
     * @param project a project
     * @param target  a target element
     */
    private void updateTree(@NotNull Project project, @NotNull PsiElement target) {
        TreeModel treeModel = ProjectTreeModelFactory.createProjectTreeModel(project);
        setModel(treeModel);

        TreePath treePath = null;

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration<TreeNode> en = rootNode.depthFirstEnumeration();

        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            if (node.getUserObject().equals(target)) {
                treePath = new TreePath(node.getPath());
                break;
            }
        }

        if (treePath != null) {
            setSelectionPath(treePath);
            scrollPathToVisible(treePath);
        }
    }

    /**
     * Returns an instance of PsiField, PsiMethod, PsiClass, or PsiPackage that is related to a change event
     *
     * @param event a change event
     * @return the corresponding Psi element
     */
    @NotNull
    private Optional<PsiElement> getTargetElement(@NotNull PsiTreeChangeEvent event) {
        for (PsiElement obj : List.of(event.getChild(), event.getParent())) {
            for (Class<? extends PsiElement> c :
                    List.of(PsiField.class, PsiMethod.class, PsiClass.class, PsiPackage.class)) {
                PsiElement elm = PsiTreeUtil.getParentOfType(obj, c, false);
                if (elm != null)
                    return Optional.of(elm);
            }
            if (obj instanceof PsiDirectory) {
                final PsiPackage pack = JavaDirectoryService.getInstance().getPackage((PsiDirectory) obj);
                if (pack != null)
                    return Optional.of(pack);
            }
        }
        return Optional.empty();
    }
}
