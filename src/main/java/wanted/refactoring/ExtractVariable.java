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

    public static final int EXP_THRESHOLD = 30;

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
            // Type 1: Unary: Do not Refactor
            @Override
            public void visitUnaryExpression(PsiUnaryExpression expression) {
            }

            // Type 1: New: Do not Refactor
            @Override
            public void visitNewExpression(PsiNewExpression expression) {
            }

            // Type 1: Lambda Expression: Do not Refactor
            @Override
            public void visitLambdaExpression(PsiLambdaExpression expression) {
            }

            // Type 2: Binary/Polyadic
            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorList.add(expression);
            }
            @Override
            public void visitPolyadicExpression(PsiPolyadicExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorList.add(expression);
            }

            // Type 2: Array Access
            @Override
            public void visitArrayAccessExpression(PsiArrayAccessExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorList.add(expression);
            }

            // Type 2: Instanceof
            @Override
            public void visitInstanceOfExpression(PsiInstanceOfExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorList.add(expression);
            }

            // Type 3: Structures -> Inherited from JavaRecursiveElementVisitor
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
    public void refactor(AnActionEvent e) {
        assert refactorValid(e);

        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        Project project = navigator.findProject();

        String extVarName;

        extVarName = "extVar" + Integer.toString(extVarNum++);

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
