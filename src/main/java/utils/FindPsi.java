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
     * Find reference expression which refers given member
     * search scope: directory of file. i.e, only check files in same package
     * @param file the file which own class with member field
     * @param member PsiField to find reference
     * @return
     */
    public static List<PsiReferenceExpression> findMemberReference(PsiFile file, PsiField member)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();

        List<PsiFile> files = Arrays.asList(file.getContainingDirectory().getFiles());

        for(PsiFile f : files)
        {
            if(f.equals(file)){ continue; } // do not check itself
            else
            {
                PsiClass[] classes;
                if(f instanceof PsiClassOwner)
                {
                    classes = ((PsiClassOwner)f).getClasses();
                    for(PsiClass c : classes)
                    {
                        ret.addAll(findMemberReference(c, member));
                    }
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
}
