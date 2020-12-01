package wanted.refactoring;

import com.intellij.codeInsight.hint.PsiImplementationSessionViewFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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
    private PsiExpression psiExpression;
    private PsiStatement psiStatement;

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
        psiExpression = FindPsi.findExpression(targetClass, offset);
        psiStatement = FindPsi.findStatement(targetClass, offset);

        if (psiExpression == null) return false;

        return refactorValid(psiExpression);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Extract Variables'.
     *
     * Candidate methods must have at least one refactorable statement;
     * 1.
     *
     * @param exp Target Expression
     * @return true if method is refactorable
     */
    public static boolean refactorValid(PsiExpression exp) {
        if (!(exp instanceof PsiAssignmentExpression)) {
            if (exp instanceof )

        }
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

        // Delete Original Method
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiExpression varExp = CreatePsi.createExpression(project, extVarName);
            PsiStatement assignStatement = CreatePsi.createExtractVariableAssignStatement(
                    project, extVarName, psiExpression);

            if (assignStatement != null && psiStatement != null) {
                // TODO: Insert AssignExp
                psiStatement.addBefore(assignStatement, psiStatement.getLastChild());

                // TODO: Exchange Var Exp
                psiStatement.replace(varExp);
            }
        });
    }
}
