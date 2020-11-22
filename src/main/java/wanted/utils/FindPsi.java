package wanted.utils;

import com.intellij.psi.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to find specific Psi element in given context
 * @author seha Park
 */
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
     * Collect reference expression from given element
     * @author CSED332 2019 Team 1
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
     * @author chanyoung Kim
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

    // Edited by YSJ
    public static PsiIfStatement findIfStatement(PsiClass psiClass, int offset)
    {
        List<PsiIfStatement> ifStatementList = new ArrayList<>();

        JavaRecursiveElementVisitor v = new JavaRecursiveElementVisitor(){
                @Override
                public void visitIfStatement(PsiIfStatement statement)
                {
                    if(statement.getTextRange().contains(offset)) ifStatementList.add(statement);
                    super.visitIfStatement(statement);
                }
        };
        psiClass.accept(v);

        if (ifStatementList.isEmpty()) return null;
        else return ifStatementList.get(ifStatementList.size()-1);
    }
}
