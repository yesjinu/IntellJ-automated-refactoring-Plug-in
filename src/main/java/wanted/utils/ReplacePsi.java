package wanted.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to replace specific Psi Elements.
 *
 * @author seha Park
 * @author seungjae yoo
 * @author Mintae Kim
 */
public class ReplacePsi {
    /**
     * Replace getter() and setter()
     *
     * @param project context
     * @param getter getter PsiMethod
     * @param setter setter PsiMethod
     * @param expressions Statements that refers 'member'
     */
    public static void encapFied(Project project, PsiMethod getter, PsiMethod setter, List<PsiReferenceExpression> expressions)
    {
        for(PsiReferenceExpression old :expressions)
        {
            if(old.getParent() instanceof PsiAssignmentExpression)
            {
                PsiAssignmentExpression assignment = (PsiAssignmentExpression) old.getParent();
                if(assignment.getLExpression().isEquivalentTo(old)) // assignment to member
                {
                    PsiElement newValue = assignment.getRExpression();
                    PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue, old.getQualifier());
                    (old.getParent()).replace(callSetter);
                }
                else
                {
                    PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                    old.replace(callGetter);
                }
            }
            else
            {
                PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                old.replace(callGetter);
            }
        }
    }

    /**
     * Remove elseStatement and bring elseElseStatement of elseStatement out
     *
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
     *
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
     *
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

    /**
     * Replace vars in paramList to vars in paramRefList for PsiElement element.
     *
     * @param element Target PsiElement to refactor
     * @param paramList List of PsiMethod parameters
     * @param paramRefList List of expressions for calling target PsiMethod
     * @return PsiElement with altered PsiTree
     */
    public static PsiElement replaceParamToArgs(Project project, PsiElement element, PsiParameterList paramList, PsiExpressionList paramRefList) {
        assert paramList.getParametersCount() == paramRefList.getExpressionCount();
        PsiParameter[] paramArray = paramList.getParameters();
        PsiExpression[] paramRefArray = paramRefList.getExpressions();

        List<PsiElement> resolveList = new ArrayList<>();
        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            final List<PsiElement> resolveList_inner = resolveList;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PsiIdentifier) return;
                for (int i = 0; i < paramList.getParametersCount(); i++) {
                    if (element.getText().equals(paramArray[i].getName())) {
                        resolveList_inner.add(element);
                        break;
                    }
                }
                super.visitElement(element);
            }
        };

        element.accept(visitor);

        for (PsiElement resolveEntry : resolveList) {
            for (int i = 0; i < paramList.getParametersCount(); i++) {
                if (resolveEntry.getText().equals(paramArray[i].getName())) {
                    PsiExpression newExp = CreatePsi.createDuplicateExpression(project, paramRefArray[i]);
                    resolveEntry.replace(newExp);
                    break;
                }
            }
        }
        return element;
    }


    /** Edit modifier of member
     * @param member selected member
     * @param removeValues modifier to be removed
     * @param addValues modifier to be added
     */
    public static void changeModifier(PsiField member, List<String> removeValues, List<String> addValues)
    {
        for(String removeValue :removeValues)
        {
            member.getModifierList().setModifierProperty(removeValue, false);
        }

        for(String addValue :addValues)
        {
            member.getModifierList().setModifierProperty(addValue, true);
        }
    }

    /**
     * pull out first statement in each conditions
     *
     * @param project
     * @param ifStatement
     * @param statementList
     */
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

    /**
     * pull out last statement in each conditions
     *
     * @param project
     * @param ifStatement
     * @param statementList
     */
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

    /**
     * remove conditions that don't have any statements inside
     *
     * @param project
     * @param ifStatement
     */
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
