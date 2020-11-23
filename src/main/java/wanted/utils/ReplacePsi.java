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

    // Edited by YSJ
    public static void pulloutFirstCondExpr(Project project, PsiIfStatement ifStatement, List<PsiStatement> statementList) {

        PsiStatement standardStatement = statementList.get(0);
        if (standardStatement instanceof PsiBlockStatement) {
            standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[0];
        }
        PsiStatement newStatement = CreatePsi.copyStatement(project, standardStatement);
        ifStatement.getParent().addBefore(newStatement,ifStatement);

        for (PsiStatement s : statementList) {
            if (s instanceof PsiBlockStatement) {
                ((PsiBlockStatement) s).getCodeBlock().getStatements()[0].delete();
            }
            else {
                PsiStatement newEmptyStatement = CreatePsi.createEmptyBlockStatement(project);
                s.replace(newEmptyStatement);
            }
        }
    }

    // Edited by YSJ
    public static void pulloutLastCondExpr(Project project, PsiIfStatement ifStatement, List<PsiStatement> statementList) {
        PsiStatement standardStatement = statementList.get(0);
        if (standardStatement instanceof PsiBlockStatement) {
            int size = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatementCount();
            standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[size-1];
        }
        PsiStatement newStatement = CreatePsi.copyStatement(project, standardStatement);
        ifStatement.getParent().addAfter(newStatement,ifStatement);

        for (PsiStatement s : statementList) {
            if (s instanceof PsiBlockStatement) {
                int size = ((PsiBlockStatement) s).getCodeBlock().getStatementCount();
                ((PsiBlockStatement) s).getCodeBlock().getStatements()[size-1].delete();
            }
            else {
                PsiStatement newEmptyStatement = CreatePsi.createEmptyBlockStatement(project);
                s.replace(newEmptyStatement);
            }
        }
    }

    // Edited by YSJ
    public static void removeUselessCondition(Project project, PsiIfStatement ifStatement) {
        PsiStatement statement = ifStatement;
        PsiElement parentStatement;
        while (statement instanceof PsiIfStatement) statement = ((PsiIfStatement) statement).getElseBranch();
        parentStatement = (PsiStatement) statement.getParent();

        if (!(statement instanceof PsiBlockStatement)) return;
        if (((PsiBlockStatement) statement).getCodeBlock().getStatementCount() > 0) return;
        statement.delete();

        PsiElement parent = ifStatement.getParent();
        while (parentStatement instanceof PsiIfStatement) {
            statement = (PsiStatement) parentStatement;
            parentStatement = parentStatement.getParent();
            if (!(((PsiIfStatement) statement).getThenBranch() instanceof PsiBlockStatement)) return;
            if (((PsiBlockStatement) ((PsiIfStatement) statement).getThenBranch()).getCodeBlock().getStatementCount() > 0) return;
            statement.delete();

            if (parent == parentStatement) break;
        }
    }
}
