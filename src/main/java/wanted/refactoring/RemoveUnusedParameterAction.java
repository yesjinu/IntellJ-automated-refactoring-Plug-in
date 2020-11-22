package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import wanted.utils.AddPsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.HashSet;
import java.util.Set;

public class RemoveUnusedParameterAction extends BaseRefactorAction {
    private PsiMethod focusMethod;
    Set<PsiParameter> parametersOfMethod;
    Set<PsiReference> referenceUsedInMethod;
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
        parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        referenceUsedInMethod = FindPsi.findReferenceUsedInMethod(focusMethod);

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
//        WriteCommandAction.runWriteCommandAction(project, ()->{
//            AddPsi.addMethod(targetClass, addList); // add method in addList to targetClass
//            ReplacePsi.encapFied(project, (PsiMethod)addList.get(0), (PsiMethod)addList.get(1), references, member); // encapsulate with getter and setter
//        });


        //TODO : 실제로 사용된 parameter만 남기기
        for (PsiParameter p : unusedParameter){
            p.delete(); // <- IncorrectOperationException occurs!
        }
    }

}
