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
import com.intellij.refactoring.changeSignature.PsiCallReference;
import utils.FindPsi;
import utils.NavigatePsi;
import utils.TraverseProjectPsi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineMethod extends RefactoringAlgorithm {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
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
     * @param e An Actionevent
     * @return true if refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();

        targetClass = navigator.findClass();
        if (targetClass == null) return false;

        return fetchCandidateMethods().size() > 0;
    }

    /**
     * Method that performs refactoring.
     */
    @Override
    protected void refactor() {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        for (PsiMethod removeMethod : fetchCandidateMethods()) {
            List<PsiReference> references = Arrays.asList(removeMethod.getReference());

            assert removeMethod.getBody() != null;
            assert removeMethod.getBody().getStatementCount() > 0;

            // Fetching element to replace
            PsiStatement removeMethodStatement = removeMethod.getBody().getStatements()[0];
            PsiElement replaceElement;
            if (PsiType.VOID.equals(removeMethod.getReturnType()))
                replaceElement = removeMethodStatement;
            else
                replaceElement = ((PsiReturnStatement) removeMethodStatement).getReturnValue();

            assert replaceElement != null;

            // Fetching Method Parameter: Replace
            PsiParameterList paramList = removeMethod.getParameterList();
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement();
                PsiExpressionList paramRefList = ((PsiCall) refElement).getArgumentList();

                // TODO: replace vars in replaceElement with Map paramList -> paramRefList
                // Replace & Delete
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // TODO: Write Runnable Function
                    removeMethod.delete();
                });
            }

            // Delete Original Method
            WriteCommandAction.runWriteCommandAction(project, () -> {
                removeMethod.delete();
            });
        }
    }

    /**
     * Helper method that fetches candidate methods to eliminate.
     *
     * Every candidate methods should follow these two requisites:
     * 1. Methods which is not defined in subclasses
     * 2. Methods with 1 statement.
     *
     * @return List of Candidate Methods for Refactoring
     */
    private List<PsiMethod> fetchCandidateMethods() {
        List<PsiClass> classList = TraverseProjectPsi.getMethodsFromProject(project);
        List<PsiClass> subclassList = FindPsi.findEverySubClass(targetClass, classList);

        List<PsiMethod> psiMethods = Arrays.asList(targetClass.getMethods());

        List<PsiMethod> candidates = new ArrayList<>();
        for (PsiMethod psiMethod : psiMethods) {
            for (PsiClass subclass : subclassList) {
                if (Arrays.asList(subclass.getMethods()).contains(psiMethod))
                    continue;

                PsiCodeBlock body = psiMethod.getBody();
                if (body == null) continue;

                // Choosing Methods with One
                if (body.getStatementCount() == 1) candidates.add(psiMethod);
            }
        }

        return candidates;
    }
}