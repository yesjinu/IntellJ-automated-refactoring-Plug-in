/**
 * Composing Methods: Inline Method
 *
 * @author Mintae Kim
 */
package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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

        return;
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