package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to provide refactoring: 'Remove Unused Parameters'
 *
 * @author Jinu Noh
 */
public class RemoveUnusedParameterAction extends BaseRefactorAction {
    public Project project;
    private PsiMethod focusMethod;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "RPA";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Remove Unused Parameter";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When there are parameters that are not used in the method<br/>" +
                "refactor codes by removing unused parameters in the parameter list.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>There exist parameters that are not used in the method</html>";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Remove Unused Parameter'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        project = navigator.findProject();
        focusMethod = navigator.findMethod();

        return refactorValid(project, focusMethod);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Remove Unused Parameter'.
     *
     * @param project Project
     * @param focusMethod PsiMethod
     * @return true if method is refactorable
     */
    public static boolean refactorValid(Project project, PsiMethod focusMethod) {
        if (focusMethod == null) return false;
        Set<PsiParameter> parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        List<PsiReferenceExpression> referenceUsedInMethod = FindPsi.findReferenceExpression(focusMethod);
        Set<PsiParameter> unusedParameter = new HashSet<>();

        if (parametersOfMethod.isEmpty()) return false;
        if (referenceUsedInMethod.isEmpty()) {
            unusedParameter.addAll(parametersOfMethod);
            return true;
        }
        for (PsiParameter p : parametersOfMethod) {
            boolean appearFlag = false;
            for (PsiReferenceExpression r : referenceUsedInMethod) {
                if (r.isReferenceTo(p)) {
                    appearFlag = true;
                    break;
                }
            }
            if (!appearFlag) {
                unusedParameter.add(p);
            }
        }

        return !unusedParameter.isEmpty();
    }


    /**
     * Method that performs refactoring: 'Remove Unused Parameter'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        Set<PsiParameter> parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        List<PsiReferenceExpression> referenceUsedInMethod = FindPsi.findReferenceExpression(focusMethod);
        Set<PsiParameter> unusedParameter = new HashSet<>();

        for (PsiParameter p : parametersOfMethod) {
            boolean appearFlag = false;
            for (PsiReferenceExpression r : referenceUsedInMethod) {
                if (r.isReferenceTo(p)) {
                    appearFlag = true;
                    break;
                }
            }
            if (!appearFlag) {
                unusedParameter.add(p);
            }
        }

        WriteCommandAction.runWriteCommandAction(project, ()->{
            for (PsiParameter p : unusedParameter){
                p.delete();
            }
        });
    }
}
