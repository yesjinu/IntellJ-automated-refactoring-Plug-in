package wanted.ui;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * TODO: ProjectTreeModelFactory Explanation
 *
 * @author Mintae Kim
 * @author seungjae yoo
 * @author CSED332 2020 TAs
 */
class ProjectTreeModelFactory {

    /**
     * Create a tree model that describes the structure of a java project. This method use JavaElementVisitor to
     * traverse the Java hierarchy of each root package in the source directory, and to create a tree. Each node is an
     * instance of {@link DefaultMutableTreeNode} that can have a user object. The user object of root is the project
     * itself, and other nodes have corresponding instances of PsiPackage, PsiClass, PsiMethod, and PsiField.
     *
     * @param project a project
     * @return a tree model to describe the structure of project
     */
    public static TreeModel createProjectTreeModel(Project project) {
        // the root node of the tree
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(project);

        // The visitor to traverse the Java hierarchy and to construct the tree
        final JavaElementVisitor visitor = new JavaElementVisitor() {
            private DefaultMutableTreeNode parent = root;

            @Override
            public void visitPackage(PsiPackage pack) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(pack);
                parent.add(child);

                DefaultMutableTreeNode temp = parent;
                parent = child;
                for (PsiPackage subPack : pack.getSubPackages()) subPack.accept(this);
                for (PsiClass subClass : pack.getClasses()) subClass.accept(this);
                parent = temp;
            }

            @Override
            public void visitClass(PsiClass aClass) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(aClass);
                parent.add(child);

                DefaultMutableTreeNode temp = parent;
                parent = child;
                for (PsiMethod subMethod : aClass.getMethods()) subMethod.accept(this);
                for (PsiField subField : aClass.getFields()) subField.accept(this);
                parent = temp;
            }

            @Override
            public void visitMethod(PsiMethod method) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(method);
                parent.add(child);
            }

            @Override
            public void visitField(PsiField field) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(field);
                parent.add(child);
            }
        };

        // apply the visitor for each root package in the source directory
        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        return new DefaultTreeModel(root);
    }

    /**
     * Returns the root package(s) in the source directory of a project. The default package will not be considered, as
     * it includes all Java classes. Note that classes in the default package (i.e., having no package statement) will
     * be ignored for this assignment. To be completed, this case must be separately handled.
     *
     * @param project a project
     * @return a set of root packages
     */
    private static Set<PsiPackage> getRootPackages(Project project) {
        final Set<PsiPackage> rootPackages = new HashSet<>();
        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitDirectory(PsiDirectory dir) {
                final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(dir);
                if (psiPackage != null && !PackageUtil.isPackageDefault(psiPackage))
                    rootPackages.add(psiPackage);
                else
                    Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootPackages;
    }
}

