/**
 * Class to Search Psi Class, Methods, ... for whole Project.
 *
 * @author Mintae Kim
 * @author seha park
 * @author POSTECH CSED332 TAs
 */
package utils;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;

import com.intellij.psi.*;

import java.util.*;

public class TraverseProjectPsi {
    private static TraverseProjectPsi observer;
    private static Project focusProject;

    /**
     * Collect project.
     * @param e AnActionEvent
     */
    private TraverseProjectPsi(AnActionEvent e) {
        focusProject = e.getData(PlatformDataKeys.PROJECT);
    }

    /**
     * Factory for TraverseProjectPsi
     * @param e AnActionEvent
     * @return observer Factory object for TraverseProjectPsi
     */
    public static TraverseProjectPsi TraverseProjectPsiFactory (AnActionEvent e) {
        if (observer == null)
            observer = new TraverseProjectPsi(e);

        return observer;
    }

    /**
     * Returns List of every methods in project
     * @return classList List of all methods
     */
    public List<PsiClass> getMethodsFromProject () {
        final List<PsiClass> classList = new ArrayList<>();

        final JavaElementVisitor visitor = new JavaElementVisitor() {
            final private List<PsiClass> classList_ptr = classList;

            @Override
            public void visitClass(PsiClass aClass) {
                classList_ptr.add(aClass);
                super.visitClass(aClass);
            }
        };

        getRootPackages().forEach(aPackage -> aPackage.accept(visitor));
        return classList;
    }

    /**
     * Returns the root package(s) in the source directory of a project. The default package will not be considered, as
     * it includes all Java classes. Note that classes in the default package (i.e., having no package statement) will
     * be ignored for this assignment. To be completed, this case must be separately handled.
     *
     * @return a set of root packages
     */
    private static Set<PsiPackage> getRootPackages() {
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

        ProjectRootManager rootManager = ProjectRootManager.getInstance(focusProject);
        PsiManager psiManager = PsiManager.getInstance(focusProject);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootPackages;
    }
}
