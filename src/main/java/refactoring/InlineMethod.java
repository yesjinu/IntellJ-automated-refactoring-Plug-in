package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;
import model.refactoring.RefactoringAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class InlineMethod implements RefactoringAlgorithm {

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
        FindPsi
    }

    /**
     * Method that performs refactoring.
     */
    @Override
    protected void refactor() {

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