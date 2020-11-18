package utils;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.*;

// Class to find specific Psi element in given context
public class FindPsi {
    /**
     * Returns list of statements referring to given member from class
     * @param focusClass search scope
     * @param member
     * @return list of statements
     */
    public static List<PsiReferenceExpression> findMemberReference(PsiClass focusClass, PsiField member)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();

        for(PsiMethod m : focusClass.getMethods())
        {
            PsiCodeBlock c = m.getBody();
            if(c==null){ continue; } // no code block

            for(PsiStatement s : c.getStatements())
            {
                if(!s.getText().contains(member.getName())){ continue; } // check by text

                List<PsiReferenceExpression> refers = findReferenceExpression(s);
                for(PsiReferenceExpression r : refers)
                {
                    if(r.isReferenceTo(member))
                    {
                        ret.add(r);
                    }
                }

            }
        }

        return ret;
    }

    /**
     * Returns list of statements referring to given member from project
     * @param project search scope
     * @param member PsiElement to search for
     * @return list of reference statements
     */
    public static List<PsiReferenceExpression> findMemberReference(Project project, PsiFile file, PsiField member)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();

        List<PsiFile> files = getPsiFiles(project);
        List<PsiReferenceExpression> refs = new ArrayList<>();
        for(PsiFile f : files)
        {
            if(f.equals(file)){ continue; } // do not check itself
            else
            {
                PsiClass[] classes = ((PsiClassOwner)f).getClasses(); // ! only search for files having class
                for(PsiClass c : classes)
                {
                    ret.addAll(findMemberReference(c, member));
                }
            }
        }

        return ret;
    }

    /**
     * Collect reference expression from given element
     * from 2019 Team 1 example
     * @param element Psi element to check
     * @return PsiReferenceExpression in given statement
     */
    public static List<PsiReferenceExpression> findReferenceExpression(PsiElement element)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(PsiReferenceExpression expression)
                {
                    super.visitReferenceExpression(expression);
                    ret.add(expression);
                }
        });
        return ret;
    }

    /**
     * Collect root packages from project
     * from HW6 code
     * @param project context
     * @return set of PsiPackage
     */
    public static Set<PsiPackage> getRootPackages(Project project) {
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

    /**
     * get PsiFiles from project
     * collect project under user-defined packages(i.e. contained in user-defined package)
     * @param project context
     * @return List of PsiFiles
     */
    public static List<PsiFile> getPsiFiles(Project project)
    {
        Set<PsiPackage> rootPackages = getRootPackages(project);

        List<PsiFile> files = new ArrayList<>();
        for(PsiPackage r : rootPackages)
        {
            files.addAll(Arrays.asList(r.getFiles(GlobalSearchScope.allScope(project))));
        }

        return files;
    }
}
