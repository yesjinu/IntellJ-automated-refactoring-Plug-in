package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import java.util.List;

/**
 * Class to replace specific Psi Elements
 * @author seha Park
 */
public class ReplacePsi {
    /**
     * Replace getter() and setter()
     * @param project context
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

    /**
     * Remove elseStatement and bring elseElseStatement of elseStatement out
     * @author seungjae yoo
     * @param project
     * @param ifStatement
     */
    public static void mergeCondStatement(Project project, PsiIfStatement ifStatement) {
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

    /**
     * Remove ifStatement and bring thenStatement of ifStatement out
     * @author seungjae yoo
     * @param project
     * @param ifStatement
     *
     */
    public static void removeCondStatement(Project project, PsiIfStatement ifStatement) {
        PsiStatement thenStatement = ifStatement.getThenBranch();

        if (thenStatement != null) {
            PsiStatement newThenStatement = CreatePsi.copyStatement(project, thenStatement);
            ifStatement.replace(newThenStatement);
        }
        else {
            ifStatement.delete();
        }
    }

    /**
     * merge Condition of ifStatement and elseifStatement with || symbol
     * @author seungjae yoo
     * @param project
     * @param ifStatement
     * @param isFirstTime
     */
    public static void mergeCondExpr(Project project, PsiIfStatement ifStatement, boolean isFirstTime) {
        PsiExpression ifCondition = ifStatement.getCondition();
        PsiExpression elseifCondition = ((PsiIfStatement)(ifStatement.getElseBranch())).getCondition();

        PsiExpression newCondition = CreatePsi.createMergeCondition(project, ifCondition, elseifCondition, isFirstTime);
        ifCondition.replace(newCondition);
    }
}
