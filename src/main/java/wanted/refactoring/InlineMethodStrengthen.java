package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.MethodSignatureUtil;
import org.jetbrains.annotations.NotNull;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Inline Method (Strengthen)'
 *
 * @author Mintae Kim
 */
public class InlineMethodStrengthen extends InlineMethod {
    private Project project;
    private PsiMethod method;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "IMS";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Inline Method (Strengthen)";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String descripton() {
        // TODO: ADD
        return "<html>When a method body is more obvious than the method itself, <br/>" +
                "Replace calls to the method with the method's content and delete the method itself.</html>";
    }

    /**
     * Method that performs refactoring: 'Inline Method (Strengthen)'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        assert refactorValid (project, method);

        List<PsiReference> references = new ArrayList<>(ReferencesSearch.search(method).findAll());

        // Fetching element to replace
        PsiStatement removeMethodStatement = method.getBody().getStatements()[0];
        PsiElement replaceElement;
        boolean insert;

        if (PsiType.VOID.equals(method.getReturnType())) {
            replaceElement = removeMethodStatement;
            insert = isInsertStatement(removeMethodStatement);
        }
        else {
            replaceElement = ((PsiReturnStatement) removeMethodStatement).getReturnValue();
            insert = true;
        }

        assert replaceElement != null;

        if (insert) {
            // Fetching Method Parameter: Replace
            PsiParameterList paramList = method.getParameterList();
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement().getParent();

                assert refElement instanceof PsiMethodCallExpression;
                PsiExpressionList paramRefList = ((PsiMethodCallExpression)refElement).getArgumentList();

                // Replace Statement
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Checking Method Calls ending with semicolons
                    if (refElement.getNextSibling() != null) {
                        if (refElement.getNextSibling().getText().equals(";"))
                            refElement.getNextSibling().delete();
                    }

                    PsiElement expAppliedElement = refElement.replace(replaceElement);

                    // replace vars in replaceElement with Map paramList -> paramRefList
                    expAppliedElement.replace(
                            ReplacePsi.replaceParamToArgs(project, expAppliedElement, paramList, paramRefList)
                    );
                });
            }
        }
        else {
            // Remove
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement().getParent();
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Replace statement
                    refElement.delete();
                });
            }
        }
        // Delete Original Method
        WriteCommandAction.runWriteCommandAction(project, () -> {
            method.delete();
        });
    }


    /**
     * Helper method that checks whether statement in method needs to be inserted while refactoring.
     *
     * @return true if statement needs insertion.
     * @see InlineMethodStrengthen#refactorValid(Project, PsiMethod)
     */
    private boolean isInsertStatement(PsiStatement statement) {
        if (statement instanceof PsiAssertStatement ||
                statement instanceof PsiThrowStatement ||
                statement instanceof PsiYieldStatement ||
                statement instanceof PsiSynchronizedStatement)
            return true;
        else if (statement instanceof PsiIfStatement)
            return isInsertStatement(((PsiIfStatement) statement).getThenBranch()) &&
                    isInsertStatement(((PsiIfStatement) statement).getElseBranch());
        else if (statement instanceof PsiLabeledStatement)
            return isInsertStatement(((PsiLabeledStatement) statement).getStatement());
        else if (statement instanceof PsiLoopStatement)
            return isInsertStatement(((PsiLoopStatement) statement).getBody());
        else if (statement instanceof PsiSwitchStatement){
            if (((PsiSwitchStatement) statement).getBody() == null) return false;
            for (PsiStatement innerStatement : ((PsiSwitchStatement) statement).getBody().getStatements()) {
                if (!(isInsertStatement(innerStatement) ||
                        innerStatement instanceof PsiBreakStatement ||
                        innerStatement instanceof PsiContinueStatement))
                    return false;
            }
            return true;
        }
        else if (statement instanceof PsiBlockStatement){
            for (PsiStatement innerStatement : ((PsiBlockStatement) statement).getCodeBlock().getStatements()) {
                if (!(isInsertStatement(innerStatement) ||
                        innerStatement instanceof PsiBreakStatement ||
                        innerStatement instanceof PsiContinueStatement))
                    return false;
            }
            return true;
        }
        else if (statement instanceof PsiExpressionStatement){
            return isInsertExpression(((PsiExpressionStatement) statement).getExpression());
        }
        return false;
    }

    /**
     * Helper method that checks whether expression in statement needs to be inserted while wanted.refactoring.
     *
     * @precond No Non-insertable parameters in PsiCallExpression
     * @precond PsiCallExpression & PsiSwitchExpression returns always true
     * @return false if Assign, Prefix, Postfix Expression is used. (TBD in later steps)
     * @return true if expression needs insertion.
     * @see InlineMethodStrengthen#refactorValid(Project, PsiMethod)
     */
    private boolean isInsertExpression(PsiExpression expression) {
        if (expression instanceof PsiConditionalExpression)
            return isInsertExpression(((PsiConditionalExpression) expression).getCondition()) &&
                    isInsertExpression(((PsiConditionalExpression) expression).getThenExpression()) &&
                    isInsertExpression(((PsiConditionalExpression) expression).getElseExpression());
        else if (expression instanceof PsiAssignmentExpression)
            return false;
        else if (expression instanceof PsiBinaryExpression)
            return isInsertExpression(((PsiBinaryExpression) expression).getLOperand()) &&
                    isInsertExpression(((PsiBinaryExpression) expression).getROperand());
        else if (expression instanceof PsiParenthesizedExpression)
            return isInsertExpression(((PsiParenthesizedExpression) expression).getExpression());
        else if (expression instanceof PsiPolyadicExpression) {
            for (PsiExpression exp : ((PsiPolyadicExpression) expression).getOperands()) {
                if (!isInsertExpression(exp)) return false;
            }
            return true;
        }
        else if (expression instanceof PsiPostfixExpression)
            return false;
        else if (expression instanceof PsiPrefixExpression)
            return false;
        else if (expression instanceof PsiUnaryExpression)
            return isInsertExpression(((PsiUnaryExpression) expression).getOperand());
        else return true;
    }
}
