package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import utils.FindPsi;
import utils.NavigatePsi;

import java.util.ArrayList;
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

    }

    /**
     * Method that performs refactoring.
     */
    @Override
    protected void refactor() {

        return;
    }

    /**
     * Method that fetches candidate methods to eliminate.
     * @param psiElements List of PsiElements
     * @return
     */
    public List<PsiMethod> fetchCandidateMethods(List<PsiElement> psiElements) {
        List<PsiMethod> candidates = new ArrayList<>();
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof PsiMethod) {
                PsiCodeBlock codeBlock = ((PsiMethod) psiElement).getBody();
                if (codeBlock != null && codeBlock.getStatementCount() == 1) {
                    return null;
                }
            }
        }
        return null;
    }
}