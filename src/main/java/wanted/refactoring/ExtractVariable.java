package wanted.refactoring;

import com.intellij.codeInsight.hint.PsiImplementationSessionViewFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.jetbrains.annotations.NotNull;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Extract Variable'
 *
 * @author Mintae Kim
 */
public class ExtractVariable extends BaseRefactorAction {
    private static int extVarNum = 0;
    private PsiStatement psiStatement;
    private static List<PsiExpression> expRefactorList;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "Extract Variable";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Extract Variable'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Project project = navigator.findProject();

        PsiClass targetClass = navigator.findClass();
        if(targetClass==null) return false;

        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        psiStatement = FindPsi.findStatement(targetClass, offset);
        if (psiStatement == null) return false;

        return refactorValid(psiStatement);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Extract Variables'.
     *
     * Candidate methods must have at least one refactorable statement;
     * 1.
     *
     * @param statement Target Statement
     * @return true if method is refactorable
     */
    public static boolean refactorValid(PsiStatement statement) {
        expRefactorList = new ArrayList<>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitAssertStatement(PsiAssertStatement statement) {
                super.visitAssertStatement(statement);
            }

            @Override
            public void visitBlockStatement(PsiBlockStatement statement) {
                super.visitBlockStatement(statement);
            }

            @Override
            public void visitBreakStatement(PsiBreakStatement statement) {
                super.visitBreakStatement(statement);
            }

            @Override
            public void visitYieldStatement(PsiYieldStatement statement) {
                super.visitYieldStatement(statement);
            }

            @Override
            public void visitContinueStatement(PsiContinueStatement statement) {
                super.visitContinueStatement(statement);
            }

            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                super.visitDeclarationStatement(statement);
            }

            @Override
            public void visitDoWhileStatement(PsiDoWhileStatement statement) {
                super.visitDoWhileStatement(statement);
            }

            @Override
            public void visitEmptyStatement(PsiEmptyStatement statement) {
                super.visitEmptyStatement(statement);
            }

            @Override
            public void visitExpression(PsiExpression expression) {
                super.visitExpression(expression);
            }

            @Override
            public void visitExpressionListStatement(PsiExpressionListStatement statement) {
                super.visitExpressionListStatement(statement);
            }

            @Override
            public void visitExpressionStatement(PsiExpressionStatement statement) {
                super.visitExpressionStatement(statement);
            }

            @Override
            public void visitForStatement(PsiForStatement statement) {
                super.visitForStatement(statement);
            }

            @Override
            public void visitForeachStatement(PsiForeachStatement statement) {
                super.visitForeachStatement(statement);
            }

            @Override
            public void visitIfStatement(PsiIfStatement statement) {
                super.visitIfStatement(statement);
            }

            @Override
            public void visitLabeledStatement(PsiLabeledStatement statement) {
                super.visitLabeledStatement(statement);
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);
            }

            @Override
            public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
                super.visitSwitchLabelStatement(statement);
            }

            @Override
            public void visitSwitchLabeledRuleStatement(PsiSwitchLabeledRuleStatement statement) {
                super.visitSwitchLabeledRuleStatement(statement);
            }

            @Override
            public void visitSwitchStatement(PsiSwitchStatement statement) {
                super.visitSwitchStatement(statement);
            }

            @Override
            public void visitTryStatement(PsiTryStatement statement) {
                super.visitTryStatement(statement);
            }

            @Override
            public void visitCatchSection(PsiCatchSection section) {
                super.visitCatchSection(section);
            }

            @Override
            public void visitWhileStatement(PsiWhileStatement statement) {
                super.visitWhileStatement(statement);
                PsiWhileStatement
            }
        };

        statement.accept(visitor);
        return !expRefactorList.isEmpty();
    }

    /**
     * Method that performs refactoring: 'Extract Variable'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    protected void refactor(AnActionEvent e) {
        assert refactorValid(e);

        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        Project project = navigator.findProject();

        String extVarName = "extVar" + Integer.toString(extVarNum++);

        for (PsiExpression psiExpression : expRefactorList) {
            // Delete Original Method
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiExpression varExp = CreatePsi.createExpression(project, extVarName);
                PsiStatement assignStatement = CreatePsi.createExtractVariableAssignStatement(
                        project, extVarName, psiExpression);

                if (assignStatement != null && psiStatement != null) {
                    // Insert AssignExp
                    psiStatement.addBefore(assignStatement, psiStatement.getLastChild());

                    // Exchange Exp into Var
                    psiStatement.replace(varExp);
                }
            });
        }
    }
}
