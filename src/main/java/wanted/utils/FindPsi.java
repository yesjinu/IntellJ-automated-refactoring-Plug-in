package wanted.utils;

import com.intellij.psi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to find specific Psi element in given context.
 *
 * @author seha park
 * @author Mintae Kim
 */
public class FindPsi {
    /**
     * Returns list of statements referring to given member
     *
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
     * Collect reference expression from given element
     * from 2019 Team 1 example
     *
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
     * Return the List containing PsiMethodCallExpression Object in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiMethodCallExpression> if element has MethodCallExpressions, empty() otherwise
     */
    public static List<PsiMethodCallExpression> findPsiMethodCallExpression(PsiElement element) {
        List<PsiMethodCallExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Searching for every subclasses
     *
     * @param superclass Superclass
     * @param classList List of all classes in project
     * @return List of all subclasses extends superclass
     */
    public static List<PsiClass> findEverySubClass (PsiClass superclass, List<PsiClass> classList) {
        List<PsiClass> subclassList = new ArrayList<>();
        for (PsiClass psiClass : classList)
            if (Arrays.asList(psiClass.getSupers()).contains(superclass))
                subclassList.add(psiClass);
        return subclassList;
    }
}
