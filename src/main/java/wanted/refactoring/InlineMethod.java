package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class InlineMethod extends wanted.refactoring.RefactoringAlgorithm {

    /**
     * Returns the story name as a string format, for message.
     * @return story name "Inline Method"
     */
    @Override
    public String storyName() {
        return "Inline Method";
    }

    /**
     * Returns the possibility of wanted.refactoring for current project with particular strategy.
     * @param e An Actionevent
     * @return true if wanted.refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        return true;
    }

    /**
     * Method that performs wanted.refactoring.
     */
    @Override
    protected void refactor(AnActionEvent e) {

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