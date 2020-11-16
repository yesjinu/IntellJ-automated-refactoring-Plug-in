package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReferenceExpression;
import utils.NavigatePsi;
import utils.FindPsi;
import utils.NavigatePsi;

import java.util.List;
import java.util.Set;

public class RemoveUnusedParameter extends RefactoringAlgorithm {
    private Project project;
    private PsiClass targetClass;
    private PsiMethod method;
    private List<PsiReferenceExpression> statements;

    /**
     * Returns the story name as a string format, for message.
     * @return story name "Remove Unused Parameter"
     */
    @Override
    public String storyName() {
        return "Remove Unused Parameter";
    }

    /**
     * Returns the possibility of refactoring for current project with particular strategy.
     * @param e An Actionevent
     * @return true if refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        method = navigator.getMethod();
        Set<String> parametersOfMethod = FindPsi.findParametersOfMethod(method);
        Set<String> referenceUsedInMethod = FindPsi.findReferenceUsedInMethod(method);

        for (String s : parametersOfMethod) {
            if (!referenceUsedInMethod.contains(s))
                return false;
        }

        return true;
    }

    @Override
    protected void refactor() {

    }
}
