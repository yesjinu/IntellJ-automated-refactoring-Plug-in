package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import utils.NavigatePsi;
import utils.FindPsi;
import utils.NavigatePsi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoveUnusedParameter extends RefactoringAlgorithm {
    private PsiMethod focusMethod;
    Set<String> parametersOfMethod;
    Set<String> referenceUsedInMethod;
    // TODO: Make Container of 'not used parameters'
    //  -> In what datatype?
    Set<String> unusedParameter = new HashSet<>();



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
        Set<String> parametersOfMethod = FindPsi.findParametersOfMethod(focusMethod);
        Set<String> referenceUsedInMethod = FindPsi.findReferenceUsedInMethod(focusMethod);
        boolean refactorFlag = true;

        for (String s : parametersOfMethod) {
            if (!referenceUsedInMethod.contains(s)) {
                unusedParameter.add(s);
                refactorFlag = false;
            }
        }
        return refactorFlag;
    }

    @Override
    protected void refactor() {
        //TODO : 실제로 사용된 parameter만 남기기
        //방법 1 : 사용하지 않은 parameter를 지운다.
        //        PsiElement::deleteChildRange 함수를 이용. PsiJavaToken:COMMA부터 PsiParameter:c까지 지우기
        //방법 2 : parameter list를 새로운 것으로 교체한다.
        //        replace 함수를 이용. PsiParameterList 자체를 새로 만든 것으로 교체한다.

        //        PsiFileFactory::createFileFromText() 메소드를 이용하거나
        //        psiJavaParserFacade::createParameterFromText() 이용해도 좋을듯
        // PsiFileFactory
    }
}
