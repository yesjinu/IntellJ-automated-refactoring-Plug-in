package utils;

import com.intellij.psi.*;
import java.util.ArrayList;
import java.util.List;

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
                if(!s.getText().contains(member.getName())){ continue; }

                List<PsiReferenceExpression> refers = findReference(s);
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
     * Collect reference expression from given statement
     * from 2019 Team 1 example
     * @param statement
     * @return PsiReferenceExpression in given statement
     */
    public static List<PsiReferenceExpression> findReference(PsiStatement statement)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();
        statement.accept(new JavaRecursiveElementVisitor() {
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
