package utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import java.util.List;

// Class to replace specific Psi Elements
public class ReplacePsi {
    /**
     * Replace getter() and setter()
     * @param project
     * @param getter getter PsiMethod
     * @param setter setter PsiMethod
     * @param expressions Statements that refers 'member'
     * @param member field to encapsulate
     */
    public static void encapFied(Project project, PsiMethod getter, PsiMethod setter, List<PsiReferenceExpression> expressions, PsiField member)
    {
        PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null);

        for(PsiReferenceExpression old :expressions)
        {
            if(old.getParent() instanceof PsiAssignmentExpression)
            {
                PsiAssignmentExpression assignment = (PsiAssignmentExpression) old.getParent();
                if(assignment.getLExpression().isEquivalentTo(old)) // assignment to member
                {
                    PsiElement newValue = assignment.getRExpression();
                    PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue);
                    (old.getParent()).replace(callSetter);
                }
                else
                {
                    old.replace(callGetter);
                }
            }
            else
            {
                old.replace(callGetter);
            }
        }
    }

    // Edited by YSJ
    public static void mergeCondExpr (Project project, PsiIfStatement ifStatement) {
        PsiStatement elseStatement = ifStatement.getElseBranch();
        PsiStatement elseElseStatement = ((PsiIfStatement) elseStatement).getElseBranch();

        if (elseElseStatement != null) {
            PsiStatement newElseStatement = CreatePsi.copyStatement(project, elseElseStatement);
            elseStatement.replace(newElseStatement);
        }
        else {
            elseStatement.delete();
        }
    }

    // Edited by YSJ
    public static void removeCondExpr (Project project, PsiIfStatement ifStatement) {
        PsiStatement thenStatement = ifStatement.getThenBranch();

        if (thenStatement != null) {
            PsiStatement newThenStatement = CreatePsi.copyStatement(project, thenStatement);
            ifStatement.replace(newThenStatement);
        }
        else {
            ifStatement.delete();
        }
    }
}
