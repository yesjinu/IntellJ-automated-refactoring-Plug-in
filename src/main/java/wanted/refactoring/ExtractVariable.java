package wanted.refactoring;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to provide refactoring: 'Extract Variable'
 *
 * @author Mintae Kim
 */
public class ExtractVariable extends BaseRefactorAction {
    private static int extVarNum = 0;
    private PsiStatement psiStatement;
    private static Set<PsiExpression> expRefactorSet;

    public static final int EXP_THRESHOLD = 30;

    @VisibleForTesting
    public static void initVarNum() { extVarNum = 0; }

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "EV";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Extract Variable";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>If you have an expression that's hard to understand, <br/>" +
                "You can apply this technique by placing the result of the expression <br/>" +
                "or its parts in separate variables that are self-explanatory.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>1. Condition of the if() operator or a part of the ?: operator <br/>" +
                "2. A long arithmetic expression without intermediate results <br/>" +
                "3. Long multipart lines <br/><br/>" +
                "(for threshold: 30 chars)</html>";
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

        if (e.getData(PlatformDataKeys.EDITOR) == null) return false;
        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        psiStatement = FindPsi.findStatement(targetClass, offset);
        if (psiStatement == null) return false;

        ExtractVariable.initVarNum();
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
        // 0. Check whether statement is null
        if (statement == null) return false;

        expRefactorSet = new HashSet<>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
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
                    expRefactorSet.add(expression);
            }
            @Override
            public void visitPolyadicExpression(PsiPolyadicExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorSet.add(expression);
            }

            // Type 2: Array Access
            @Override
            public void visitArrayAccessExpression(PsiArrayAccessExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorSet.add(expression);
            }

            // Type 2: Instanceof
            @Override
            public void visitInstanceOfExpression(PsiInstanceOfExpression expression) {
                if (expression.getTextLength() > EXP_THRESHOLD)
                    expRefactorSet.add(expression);
            }

            // Type 3: Unary: Find Deeper
            @Override
            public void visitUnaryExpression(PsiUnaryExpression expression) {
                super.visitExpression(expression.getOperand());
            }

            // Type 3: Assignment Expression: Check if Operands are Arrays?
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitExpression(expression.getLExpression());

                if (expression.getRExpression() != null) {
                    if (expression.getRExpression().getTextLength() > EXP_THRESHOLD)
                        expRefactorSet.add(expression.getRExpression());
                }

            }

            // Type 4: Structures -> Inherited from JavaRecursiveElementVisitor
        };

        statement.accept(visitor);
        return !expRefactorSet.isEmpty();
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

        for (PsiExpression psiExpression : expRefactorSet) {
            String extVarName = "extVar" + Integer.toString(++extVarNum);
            PsiElement anchor = psiStatement;

            // Delete Original Method
            WriteCommandAction.runWriteCommandAction(project, () -> {
                final PsiElement newLineNode =
                        PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n");
                PsiExpression varExp = CreatePsi.createExpression(project, extVarName);
                PsiStatement assignStatement =
                        CreatePsi.createExtractVariableAssignStatement(
                                project, extVarName,  psiExpression
                        );

                if (psiStatement != null && assignStatement != null) {
                    // Insert AssignExp
                    psiStatement.getParent().addBefore(assignStatement, anchor);

                    // New Line
                    psiStatement.getParent().addBefore(newLineNode, anchor);

                    // Exchange Exp into Var
                    psiExpression.replace(varExp);
                }
            });
        }
    }
}
