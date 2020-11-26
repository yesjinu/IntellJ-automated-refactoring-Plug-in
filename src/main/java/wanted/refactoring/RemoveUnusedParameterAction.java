package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to remove unused parameters
 *
 * @author Jinu Noh
 */
public class RemoveUnusedParameterAction extends BaseRefactorAction {
    public Project project;
    private PsiMethod focusMethod;
    Set<PsiParameter> parametersOfMethod;
    Set<PsiReferenceExpression> referenceUsedInMethod;
    // TODO: Make Container of 'not used parameters'
    //  -> In what datatype?
    Set<PsiParameter> unusedParameter = new HashSet<>();

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name "Remove Unused Parameter"
     */
    @Override
    public String storyName() {
        return "Remove Unused Parameter";
    }

    /**
     * Returns the possibility of refactoring for current project with particular strategy.
     *
     * @param e An Actionevent
     * @return true if refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        project = navigator.findProject();
        focusMethod = navigator.findMethod();
        parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        referenceUsedInMethod = FindPsi.findReferenceExpression(focusMethod);

        if (parametersOfMethod.isEmpty()) return false;
        if (referenceUsedInMethod.isEmpty()) {
            unusedParameter.addAll(parametersOfMethod);
            return true;
        }
        for (PsiParameter p : parametersOfMethod) {
            boolean appearFlag = false;
            for (PsiReferenceExpression r : referenceUsedInMethod) {
//                if (p.getName().equals(r.getQualifiedName())) {
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
     * Do refactoring action
     *
     * @param e An Actionevent
     */
    @Override
    protected void refactor(AnActionEvent e) {
//        System.out.println("***************************************");
//        System.out.println("parameter of method" + parametersOfMethod);
//        System.out.println("used in method" + referenceUsedInMethod);
//        System.out.println(unusedParameter);
//        System.out.println("***************************************");

        WriteCommandAction.runWriteCommandAction(project, ()->{
            for (PsiParameter p : unusedParameter){
                p.delete();
            }
        });
    }

}
