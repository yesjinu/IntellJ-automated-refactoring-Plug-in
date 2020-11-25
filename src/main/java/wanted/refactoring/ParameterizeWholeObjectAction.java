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
     * 1. focusClass의 method call 부분을 전부 검사
     *      1) get method를 사용해서 값을 얻어낸 경우를 전부 저장
     * 2. parameter 리스트를 순회하며 하나의 object의 getter로부터 받아온 값이 2개 이상인 경우 체크
     *      1) o.getA(), o.getB()
     *      2) a, b
     *      3) o.getA(), b
     * 3. parameter 2개 (혹은 그 이상) 제거 후, parameter로 object를 넣기
     * 4. method에 해당 parameter의 getMethod() 넣기. (getMethod가 있는 경우만 생각)
     * 5. method 호출부 찾아서 변경하기
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
