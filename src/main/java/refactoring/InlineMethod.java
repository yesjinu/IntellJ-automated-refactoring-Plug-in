/**
 * Composing Methods: Inline Method
 *
 * @author Mintae Kim
 */
package refactoring;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiBreakStatementImpl;
import com.intellij.refactoring.changeSignature.PsiCallReference;
import org.jetbrains.annotations.NotNull;
import utils.FindPsi;
import utils.NavigatePsi;
import utils.ReplacePsi;
import utils.TraverseProjectPsi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineMethod extends RefactoringAlgorithm {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private PsiMethod method;
    private List<PsiReferenceExpression> statements;

    /**
     * Returns the story name as a string format, for message.
     * @return story name "Inline Method"
     */
    @Override
    public String storyName() {
        return "Inline Method";
    }

    /**
     * Returns the possibility of refactoring for current project with particular strategy.
     *
     * @param e An Actionevent
     * @return true if refactoring selected method is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();

        targetClass = navigator.findClass();
        if (targetClass == null) return false;

        method = navigator.findFocusMethod();
        if (method == null) return false;

        return isCandidate(method);
    }

    /**
     * Method that performs refactoring.
     */
    @Override
    protected void refactor() {
        assert isCandidate (method);

        List<PsiReference> references = Arrays.asList(method.getReference());

        // Fetching element to replace
        PsiStatement removeMethodStatement = method.getBody().getStatements()[0];
        PsiElement replaceElement;
        boolean insert = false;

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
                PsiElement refElement = reference.getElement();
                PsiExpressionList paramRefList = ((PsiCall) refElement).getArgumentList();

                // Replace & Delete
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Replace statement
                    refElement.replace(replaceElement);
                    // replace vars in replaceElement with Map paramList -> paramRefList
                    ReplacePsi.replaceParamToArgs(refElement, paramList, paramRefList);
                });
            }
        }
        else {
            // Remove
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement();
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
     */
    private boolean isInsertStatement(PsiStatement statement) {
        if (statement instanceof PsiAssertStatement ||
                statement instanceof PsiThrowStatement ||
                statement instanceof PsiExpressionStatement ||
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
                if (!isInsertStatement(innerStatement)) return false;
            }
            return true;
        }
        else if (statement instanceof PsiBlockStatement){
            for (PsiStatement innerStatement : ((PsiBlockStatement) statement).getCodeBlock().getStatements()) {
                if (!isInsertStatement(innerStatement)) return false;
            }
            return true;
        }
    }

    /**
     * Helper method that checks whether candidate method is refactorable using 'Inline Method'.
     *
     * Every candidate methods should follow these two requisites:
     * 1. Methods which is not defined in subclasses
     * 2. Methods with 1 statement.
     *
     * @return true if method is refactorable
     */
    private boolean isCandidate(@NotNull PsiMethod method) {
        List<PsiClass> classList = TraverseProjectPsi.getMethodsFromProject(project);
        List<PsiClass> subclassList = FindPsi.findEverySubClass(targetClass, classList);

        List<PsiMethod> candidates = new ArrayList<>();
        for (PsiClass subclass : subclassList) {
            if (Arrays.asList(subclass.getMethods()).contains(method))
                return false;

            PsiCodeBlock body = method.getBody();
            if (body == null) return false;

            // Choosing Methods with One Statement
            if (body.getStatementCount() > 1) return false;
        }

        return true;
    }
}