package wanted.utils;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;

import com.intellij.psi.*;

import java.util.*;

/**
 * Class to Search Psi Class, Methods, ... for whole Project.
 *
 * @author Mintae Kim
 * @author seungjae yoo
 * @author CSED332 2020 TAs
 */
public class TraverseProjectPsi {

    /**
     * Returns List of every methods in project
     *
     * @return classList List of all methods
     */
    public static List<PsiClass> getMethodsFromProject (Project focusProject) {
        final List<PsiClass> classList = new ArrayList<>();

        final JavaElementVisitor visitor = new JavaElementVisitor() {
            final private List<PsiClass> classList_ptr = classList;

            @Override
            public void visitPackage(PsiPackage pack) {
                for (PsiPackage subPack : pack.getSubPackages()) subPack.accept(this);
                for (PsiClass subClass : pack.getClasses()) subClass.accept(this);

                super.visitPackage(pack);
            }

            @Override
            public void visitClass(PsiClass aClass) {
                classList_ptr.add(aClass);

                super.visitClass(aClass);
            }
        };

        getRootPackages(focusProject).forEach(aPackage -> aPackage.accept(visitor));
        return classList;
    }

    /**
     * Returns the root package(s) in the source directory of a project. The default package will not be considered, as
     * it includes all Java classes. Note that classes in the default package (i.e., having no package statement) will
     * be ignored for this assignment. To be completed, this case must be separately handled.
     *
     * @return a set of root packages
     */
    public static Set<PsiPackage> getRootPackages(Project focusProject) {
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

    /**
     * Returns set of classes in the default package
     * (which is not considered in {@link TraverseProjectPsi#getRootPackages(Project)}
     *
     * @param project A Project
     * @return Set of PsiClass containing default package classes
     */
    public static Set<PsiClass> getRootClasses(Project project) {
        final Set<PsiClass> rootClasses = new HashSet<>();

        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    rootClasses.addAll(Arrays.asList(((PsiJavaFile) file).getClasses()));
                }
            }
            @Override
            public void visitDirectory(PsiDirectory dir) {
                final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(dir);
                if (psiPackage != null && !PackageUtil.isPackageDefault(psiPackage)) {

                }
                else {
                    Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
                    Arrays.stream(dir.getFiles()).forEach(f -> f.accept(this));
                }
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootClasses;
    }

    public static List<PsiFile> findFile(Project project) {
        List<PsiFile> fileList = new ArrayList<>();

        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                if (file != null) fileList.add(file);
            }
            @Override
            public void visitDirectory(PsiDirectory dir) {
                Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
                Arrays.stream(dir.getFiles()).forEach(f -> f.accept(this));
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return fileList;
    }
}