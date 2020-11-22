package wanted.test;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * A tree GUI for our Project Structure plugin. It displays the corresponding name and icon for the nodes in our tree
 * model using a custom cell renderer. Note that each node is an instance of DefaultMutableTreeNode, and its user data
 * is an instance of Project, PsiPackage, PsiClass, PsiMethod, or PsiField. The tree GUI detect detects double-click
 * mouse events for Method and Field nodes, and shows the corresponding methods or fields in the editor. Finally,
 * whenever the underlying project changes, the corresponding node of the tree GUI is automatically chosen.
 */

// GUI를 제공하는 클래스 : cell renderer 클래스를 이용해 노드(패키지, 클래스, 필드, 메소드)의 이름과 아이콘을 표시함
    // 모든 노드는 DefaultMutableTreeNode 의 인스턴스
    // 노드의 user data는 Project, PsiPackage, PsiClass, PsiMethod, PsiField 의 인스턴스 (TODO: node와 user data의 차이는?)
    // treeGUI를 더블클릭 -> 해당 메소드나 필드를 에디터로 보여준다.
    // 메소드, 필드 수정 -> treeGUI가 선택되어야 한다.
class ProjectStructureTree extends Tree {

    private static final Icon projectIcon = MetalIconFactory.getTreeHardDriveIcon();
    private static final Icon packageIcon = MetalIconFactory.getTreeFolderIcon();
    private static final Icon classIcon = MetalIconFactory.getTreeComputerIcon();
    private static final Icon methodIcon = MetalIconFactory.getFileChooserDetailViewIcon();
    private static final Icon fieldIcon = MetalIconFactory.getVerticalSliderThumbIcon();
    private static final Icon defaultIcon = MetalIconFactory.getTreeLeafIcon();

    /**
        생성자가 하는 일
     1. 인자로 넘겨받은 projcet를 바탕으로 treeModel을 만든다.
     2. 그 treeModel의 렌더러를 수정해 우리가 원하는 아이콘이 나오도록 수정.
     3. 마우스 더블클릭 감지 리스너, 트리 변경 감지 리스너를 붙임
    */
    ProjectStructureTree(@NotNull Project project) {
        // setModel 메소드를 호출 -> JTree에 정의된 전역변수 treeModel이 수정됨
        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));

        // Set a cell renderer to display the name and icon of each node
        // 이미 구현되어있는 ColoredTreeCellRenderer 함수의 메소드를 오버라이딩해서 렌더링
        // 그 인스턴스를 받은 setCellRenderer에서 알아서 처리함 -> 신경쓰지말고 오버라이딩된 메소드만 구현
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                              boolean expanded, boolean leaf, int row, boolean hasFocus) {
                // TODO: implement the renderer behavior here
                // hint: use the setIcon method to assign icons, and the append method to add text
                // package,class,field, method를 가리키는 오브젝트 ->PsiPackage, PsiClass, PsiField, PsiMethod


                if (((DefaultMutableTreeNode) value).getUserObject() instanceof Project) {
                    Project project = (Project) ((DefaultMutableTreeNode) value).getUserObject();
                    setIcon(projectIcon);
                    append(project.getName());
                }
                else if (((DefaultMutableTreeNode) value).getUserObject() instanceof PsiPackage) {
                    PsiPackage pkg = (PsiPackage) ((DefaultMutableTreeNode) value).getUserObject();
                    setIcon(packageIcon);
                    append(pkg.getName());
                }
                else if (((DefaultMutableTreeNode) value).getUserObject() instanceof PsiClass) {
                    PsiClass cls = (PsiClass) ((DefaultMutableTreeNode) value).getUserObject();
                    setIcon(classIcon);
                    append(cls.getName());
                }
                else if (((DefaultMutableTreeNode) value).getUserObject() instanceof PsiMethod) {
                    PsiMethod mtd = (PsiMethod) ((DefaultMutableTreeNode) value).getUserObject();
                    setIcon(methodIcon);
                    append(mtd.getName());
                }
                else if (((DefaultMutableTreeNode) value).getUserObject() instanceof PsiField) {
                    PsiField fld = (PsiField) ((DefaultMutableTreeNode) value).getUserObject();
                    setIcon(fieldIcon);
                    append(fld.getName());
                }
                else {
                    setIcon(defaultIcon);
                    append(value.toString());
                }
            }
        });

        // Set a mouse listener to handle double-click events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // TODO: implement the double-click behavior here
                    // hint: use the navigate method of the classes PsiMethod and PsiField
                    Tree tree = (Tree) e.getSource();
                    TreePath path = null;
                    if (tree.getPathForLocation(e.getX(), e.getY()) == null) return;
                    else path = tree.getPathForLocation(e.getX(), e.getY());
                    Object node = path.getLastPathComponent();
                    PsiElement elem = null;
                    if (node instanceof DefaultMutableTreeNode)
                        elem = (PsiElement) ((DefaultMutableTreeNode) node).getUserObject();

                    if(elem instanceof PsiMethod){
                        ((PsiMethod) elem).navigate(true);
                    }
                    else if(elem instanceof PsiField){
                        ((PsiField) elem).navigate(true);
                    }
                }
            }
        });

        // Set a Psi tree change listener to handle changes in the project. We provide code for obtaining an instance
        // of PsiField, PsiMethod, PsiClass, or PsiPackage. Implement the updateTree method below.
        //
        // 트리 변경 '감지'는 이미 다 구현되어 있다. 우리는 실제로 update하는 것만 구현하면 된다.
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
     * to find the corresponding node to the target element. Use the methods {@link JTree ::setSelectionPath} and
     * {@link JTree ::scrollPathToVisibles} to display the corresponding node in GUI
     *
     * @param project a project
     * @param target  a target element
     */
    // 파라미터로 넘겨지는 PsiElement에 따라 업데이트를 진행. 
    private void updateTree(@NotNull Project project, @NotNull PsiElement target) {
        // TODO: implement this method
        // 1. 인자로 넘겨받은 project로 새 model을 만들기 - setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        // 2. JTree의 메소드를 사용해 해당 노드를 GUI로 보여주기 - setSelectionPath, scrollPathToVisibles
        System.out.println("***************************");
        System.out.println("Update Tree Function called");
        System.out.println(target.toString());

        setModel(ProjectTreeModelFactory.createProjectTreeModel(project));
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel().getRoot();
        // System.out.println(root.getUserObject().toString());


        Enumeration enumeration = root.depthFirstEnumeration();
        TreePath path = null;
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject().equals(target)) {
                path = new TreePath(node.getPath());
                break;
            }
        }
        setSelectionPath(path);
        scrollPathToVisible(path);
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
