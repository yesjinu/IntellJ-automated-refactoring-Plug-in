package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.AddPsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jinu Noh
 */

public class ParameterizeWholeObjectAction extends BaseRefactorAction{
    public Project project;
    private PsiClass focusClass;

    List<PsiMethodCallExpression> methodCallsInClass;
    List<PsiIdentifier> varFromGetter;

    /**
     * Returns the story name as a string format, for message.
     * @return story name "Parameterize Whole Object"
     */
    @Override
    public String storyName() {
        return "Parameterize Whole Object";
    }

    /**
     * 구현 순서
     * 1. method의 > parameter 부분을 검사
     * 2. 하나의 object의 get method로부터 받아온 값이 2개 이상인 경우 체크
     * 3. parameter 제거 후, parameter로 object를 넣기
     * 4. method 내부에 해당 parameter의 getMethod()넣기.
     * 5. method 호출부 찾아서 변경하기
     *
     * 내가 지금 모르는 것
     * 1. parameter가 어떻게 get method로 받아온 것인지 알 수 있는가?
     * 2. 어떻게 특정 parameter를 제거할 것인가?
     * 3. 어떻게 object를 parameter에 넣을 것인가?
     * 4. 어떻게 호출부를 찾을 것인가?
    */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        project = navigator.findProject();
        focusClass = navigator.findClass();

        methodCallsInClass = FindPsi.findPsiMethodCallExpression(focusClass);
        // 클래스 안의 모든 method 콜을 검사
        for (PsiMethodCallExpression m : methodCallsInClass) {
            // getter 함수이면
            if (m.toString().contains("get")) {
//                varFromGetter.add()
            }
        }



        return true;
    }

    @Override
    protected void refactor(AnActionEvent e) {


    }
}
