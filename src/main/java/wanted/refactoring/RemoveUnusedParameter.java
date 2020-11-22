package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;
import wanted.utils.NavigatePsi;
import utils.FindPsi;

import java.util.HashSet;
import java.util.Set;

public class RemoveUnusedParameter extends BaseRefactorAction {
    private PsiMethod focusMethod;
    Set<String> parametersOfMethod;
    Set<String> referenceUsedInMethod;
    // TODO: Make Container of 'not used parameters'
    //  -> In what datatype?
    Set<PsiParameter> unusedParameter = new HashSet<>();



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
        focusMethod = navigator.getMethod();
        Set<PsiParameter> parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        Set<PsiReference> referenceUsedInMethod = FindPsi.findReferenceUsedInMethod(focusMethod);

        for (PsiParameter p : parametersOfMethod) {
            boolean appearFlag = false;
            for (PsiReference r : referenceUsedInMethod) {
                if (p.toString().equals(r.toString())) {
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

    @Override
    protected void refactor(AnActionEvent e) {
        //TODO : 실제로 사용된 parameter만 남기기
        for (PsiParameter p : unusedParameter){
            p.delete();
        }
    }

}
