package wanted.ui;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import wanted.refactoring.ConsolidateCondExpr;

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
        // TODO

        // the root node of the tree
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(project);

        final DefaultMutableTreeNode rootCCE = new DefaultMutableTreeNode("Consolidate Conditional Expression");
        root.add(rootCCE);

        // traverse CCE
        final JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                super.visitIfStatement(ifStatement);
                if (ConsolidateCondExpr.refactorValid(ifStatement)) {
                    System.out.println(1111);
                    rootCCE.add(new DefaultMutableTreeNode(ifStatement));
                }
            }
        };

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

