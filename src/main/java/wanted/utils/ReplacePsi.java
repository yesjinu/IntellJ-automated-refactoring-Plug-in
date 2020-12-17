package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to replace specific Psi Elements.
 *
 * @author seha Park
 * @author seungjae yoo
 * @author Mintae Kim
 */
public class ReplacePsi {
    /**
     * Replace direct access to certain variable with method call for getter() and setter()
     * if reference expression to variable is left operand of assignment expression(i.e, var=x), replace assignment expression by setter(x)
     * else replace reference expression by getter()
     *
     * @param project     target project
     * @param getter      PsiMethod such that return type is variable.getType()
     * @param setter      PsiMethod such that parameter is variable.getType()
     * @param expressions target statements that refers variable
     */
    public static void encapField(@NotNull Project project, @NotNull PsiMethod getter, @NotNull PsiMethod setter, @NotNull List<PsiReferenceExpression> expressions) {
        for (PsiReferenceExpression old : expressions) {
            if (old.getParent() instanceof PsiAssignmentExpression) {
                PsiAssignmentExpression assignment = (PsiAssignmentExpression) old.getParent();
                if (assignment.getLExpression().isEquivalentTo(old)) // for the case var = x
                {
                    PsiElement newValue = assignment.getRExpression();
                    PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue, old.getQualifier()); //add qualifier if necessary
                    (old.getParent()).replace(callSetter);
                } else {
                    PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                    old.replace(callGetter);
                }
            } else {
                PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                old.replace(callGetter);
            }
        }
    }

    /**
     * Remove elseStatement and bring elseElseStatement of elseStatement out
     * i.e, remove second conditional branch
     *
     * @param project     target project
     * @param ifStatement target ifStatement that has else branch
     */
    public static void mergeCondStatement(@NotNull Project project, @NotNull PsiIfStatement ifStatement) {
        PsiStatement elseStatement = ifStatement.getElseBranch();
        PsiStatement elseElseStatement = ((PsiIfStatement) elseStatement).getElseBranch();

        if (elseElseStatement != null) {
            PsiStatement newElseStatement = CreatePsi.copyStatement(project, elseElseStatement);
            elseStatement.replace(newElseStatement);
        } else {
            elseStatement.delete();
        }
    }

    /**
     * Remove ifStatement and bring thenStatement of ifStatement out
     * i.e, remove first conditional branch, erase entire ifStatement if there's only one branch
     *
     * @param project     target project
     * @param ifStatement target ifStatement
     */
    public static void removeCondStatement(@NotNull Project project, @NotNull PsiIfStatement ifStatement) {
        PsiStatement thenStatement = ifStatement.getThenBranch();

        if (thenStatement == null) ifStatement.delete();
        else if (thenStatement instanceof PsiBlockStatement) {
            for (PsiStatement s : ((PsiBlockStatement) thenStatement).getCodeBlock().getStatements()) {
                PsiStatement newStatement = CreatePsi.copyStatement(project, s);
                ifStatement.getParent().addBefore(s, ifStatement);
            }
            ifStatement.delete();
        }
        else {
            PsiStatement newThenStatement = CreatePsi.copyStatement(project, thenStatement);
            ifStatement.replace(newThenStatement);
        }
    }

    /**
     * merge Condition of ifStatement and elseifStatement with || symbol
     *
     * @param project     target project
     * @param ifStatement target ifStatement that has else branch
     * @param isFirstTime parameter to check this function was used before for this ifStatement
     */
    public static void mergeCondExpr(@NotNull Project project, @NotNull PsiIfStatement ifStatement, boolean isFirstTime) {
        PsiExpression ifCondition = ifStatement.getCondition();
        PsiExpression elseifCondition = ((PsiIfStatement) (ifStatement.getElseBranch())).getCondition();

        PsiExpression newCondition = CreatePsi.createMergeCondition(project, ifCondition, elseifCondition, isFirstTime);
        ifCondition.replace(newCondition);
    }

    /**
     * Replace variables in paramList by variables in paramRefList for element.
     *
     * @param project Project
     * @param element PsiElement
     * @param paramList PsiParameterList
     * @param paramRefList PsiExpressionList
     * */
    public static PsiElement replaceParamToArgs(@NotNull Project project, @NotNull PsiElement element,
                                                @NotNull PsiParameterList paramList, @NotNull PsiExpressionList paramRefList) {
        assert paramList.getParametersCount() == paramRefList.getExpressionCount();
        PsiParameter[] paramArray = paramList.getParameters();
        PsiExpression[] paramRefArray = paramRefList.getExpressions();

        return replaceParamToArgs(project, element, paramArray, paramRefArray);
    }

    /**
     * Replace variables in paramList by variables in paramRefList for element.
     * nth variable in paramList is replaced by nth variable in paramRefList
     *
     * @param element        target PsiElement to replace variables
     * @param paramArray     Array of target PsiMethod parameters
     * @param paramRefArray  Array of expressions that invokes target PsiMethod
     *                       # of parameter in paramList and # of expression in expression list are same
     * @return PsiElement with altered PsiTree
     */
    public static PsiElement replaceParamToArgs(@NotNull Project project, @NotNull PsiElement element,
                                                @NotNull PsiParameter[] paramArray, @NotNull PsiExpression[] paramRefArray) {

        assert paramArray.length == paramRefArray.length;
        Set<PsiElement> resolveList = new HashSet<>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            final Set<PsiElement> resolveList_inner = resolveList;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                for (int i = 0; i < paramArray.length; i++) {
                    if (element.getText().equals(paramArray[i].getName())) {
                        resolveList_inner.add(element);
                        return;
                    }
                }
                super.visitElement(element);
            }
        };

        element.accept(visitor);

        for (PsiElement resolveEntry : resolveList) {
            for (int i = 0; i < paramArray.length; i++) {
                if (resolveEntry.getText().equals(paramArray[i].getName())) {
                    PsiExpression newExp = CreatePsi.copyExpression(project, paramRefArray[i]);
                    resolveEntry.replace(newExp);
                    break;
                }
            }
        }
        return element;
    }

    /**
     * Replace variables in paramArray by variables in paramRefList for element.
     * nth variable in paramArray is replaced by nth variable in paramRefArray
     *
     * @param element        target PsiElement to replace variables
     * @param paramArray     Array of current target parameter names
     * @param paramRefArray  Array of target parameter names to be changed
     *                       lengths of paramList and paramRefList need to be same
     * @return PsiElement with altered PsiTree
     */
    public static PsiElement replaceVariable(@NotNull Project project, @NotNull PsiElement element,
                                                @NotNull String[] paramArray, @NotNull String[] paramRefArray) {

        assert paramArray.length == paramRefArray.length;
        Set<PsiElement> resolveList = new HashSet<>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            final Set<PsiElement> resolveList_inner = resolveList;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                for (int i = 0; i < paramArray.length; i++) {
                    if (element.getText().equals(paramArray[i])) {
                        resolveList_inner.add(element);
                        return;
                    }
                }
                super.visitElement(element);
            }
        };

        element.accept(visitor);

        for (PsiElement resolveEntry : resolveList) {
            for (int i = 0; i < paramArray.length; i++) {
                if (resolveEntry.getText().equals(paramArray[i])) {
                    if (resolveEntry instanceof PsiIdentifier) {
                        PsiIdentifier newVarIden = CreatePsi.createIdentifier(project, paramRefArray[i]);
                        resolveEntry.replace(newVarIden);
                    }
                    if (resolveEntry instanceof PsiExpression) {
                        PsiExpression newVarExp = CreatePsi.createExpression(project, paramRefArray[i]);
                        resolveEntry.replace(newVarExp);
                    }
                    break;
                }
            }
        }
        return element;
    }

    /**
     * Edit modifier of member
     *
     * @param member       target member
     * @param removeValues modifiers to be removed
     * @param addValues    modifiers to be added
     *                     if removeValues and addValues have common elements, those modifiers will be added
     */
    public static void changeModifier(@NotNull PsiField member, @NotNull List<String> removeValues, @NotNull List<String> addValues) {
        for (String removeValue : removeValues) {
            member.getModifierList().setModifierProperty(removeValue, false);
        }

        for (String addValue : addValues) {
            member.getModifierList().setModifierProperty(addValue, true);
        }
    }

    /**
     * pull out first statement in each condition
     *
     * @param project       target project
     * @param ifStatement   target ifStatement
     * @param statementList list of Statements that first inner statement should be removed
     *                      statementList.size() must be bigger than 0
     */
    public static void pulloutFirstCondExpr(@NotNull Project project, @NotNull PsiIfStatement ifStatement, @NotNull List<PsiStatement> statementList) {
        PsiStatement standardStatement = statementList.get(0);
        if (standardStatement instanceof PsiBlockStatement) {
            standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[0];
        }
        PsiStatement newStatement = CreatePsi.copyStatement(project, standardStatement);
        ifStatement.getParent().addBefore(newStatement, ifStatement);

        for (PsiStatement s : statementList) {
            if (s instanceof PsiBlockStatement) {
                ((PsiBlockStatement) s).getCodeBlock().getStatements()[0].delete();
            } else {
                PsiStatement newEmptyStatement = CreatePsi.createEmptyBlockStatement(project);
                s.replace(newEmptyStatement);
            }
        }
    }

    /**
     * pull out last statement in each condition
     *
     * @param project       target project
     * @param ifStatement   target ifStatement
     * @param statementList list of Statements that last inner statement should be removed
     *                      statementList.size() must be bigger than 0
     */
    public static void pulloutLastCondExpr(@NotNull Project project, @NotNull PsiIfStatement ifStatement, @NotNull List<PsiStatement> statementList) {
        PsiStatement standardStatement = statementList.get(0);
        if (standardStatement instanceof PsiBlockStatement) {
            int size = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatementCount();
            standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[size - 1];
        }
        PsiStatement newStatement = CreatePsi.copyStatement(project, standardStatement);
        ifStatement.getParent().addAfter(newStatement, ifStatement);

        for (PsiStatement s : statementList) {
            if (s instanceof PsiBlockStatement) {
                int size = ((PsiBlockStatement) s).getCodeBlock().getStatementCount();
                ((PsiBlockStatement) s).getCodeBlock().getStatements()[size - 1].delete();
            } else {
                PsiStatement newEmptyStatement = CreatePsi.createEmptyBlockStatement(project);
                s.replace(newEmptyStatement);
            }
        }
    }

    /**
     * remove condition that don't have any statements inside
     *
     * @param project     target project
     * @param ifStatement target ifStatement with else branch
     */
    public static void removeUselessCondition(@NotNull Project project, @NotNull PsiIfStatement ifStatement) {
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
            if (((PsiBlockStatement) ((PsiIfStatement) statement).getThenBranch()).getCodeBlock().getStatementCount() > 0)
                return;
            statement.delete();

            if (parent == parentStatement) break;
        }
    }
}
