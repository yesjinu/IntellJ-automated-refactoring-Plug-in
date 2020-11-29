package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import wanted.utils.AddPsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.*;

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



/**
 * @author Jinu Noh
 */
public class ParameterizeWholeObjectAction extends BaseRefactorAction{
    public Project project;
    private PsiClass focusClass;
    private PsiMethod focusMethod;

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
     * Method that checks whether candidate method is refactorable
     * using 'Parameterize Whole Object'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
//        project = navigator.findProject();
        focusClass = navigator.findClass();
//        focusMethod = navigator.findMethod();

        return refactorValid(focusClass);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Parameterize Whole Object'.
     *
     * @param focusClass PsiClass
     * @return true if method is refactorable
     */
    public static boolean refactorValid(@NotNull PsiClass focusClass) {
        // TODO: 메소드 콜 파라미터 부분에 바로 p.getA() 호출된 경우 추가
//        int refactorCounter = 0;
        HashMap<String, Integer> paramCounter;

        // focusClass 내에 method call을 모두 검사한다.
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);
        for (PsiMethodCallExpression meth : methodCalls) {
            paramCounter = new HashMap<>();
//            refactorCounter = 0;
            Set<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());
            if (paramsOfMethod.size() < 2) continue;
            for (PsiReferenceExpression param : paramsOfMethod) {
                PsiElement resolvedParam = param.resolve(); // PsiField:bb 형태로 출력됨

                assert resolvedParam != null;
                PsiMethodCallExpression methodCallPart = FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0); // TestClass.testMethod(aa, bb) 형태
                PsiReferenceExpression tempPsiReference = FindPsi.findChildPsiReferenceExpressions(methodCallPart).get(0); // TestClass.testMethod 형태
                PsiReferenceExpression callerClass = FindPsi.findChildPsiReferenceExpressions(tempPsiReference).get(0); // TestClass 메소드를 호출한 클래스만 받아오기
//                System.out.println("methodCall part : " + methodCallPart); // PsiMethodCallExpression:p.getB()
//                System.out.println("tempPsiReference part : " + tempPsiReference); // PsiReferenceExpression:p.getB
//                System.out.println("callerClass part : " + callerClass); // PsiReferenceExpression:p
//                System.out.println("callerClass resolve to : " + callerClass.resolve()); // PsiField:p

                if (!paramCounter.containsKey(callerClass.toString())) paramCounter.put(callerClass.toString(), 0);
                paramCounter.put(callerClass.toString(), paramCounter.get(callerClass.toString()) + 1);
            }

            System.out.println("Param counter : " + paramCounter); // for debugging
            for (Map.Entry<String, Integer> entry : paramCounter.entrySet()) {
                if (entry.getValue() > 2) return true;
            }
        }
//        return refactorCounter >= 2;
        return true;
    }

    @Override
    protected void refactor(AnActionEvent e) {
        int refactorCounter = 0;

        // 메소드콜과 그 parameter를 resolve한 값들을 hashmap으로 매핑
        HashMap<PsiMethodCallExpression, List<PsiElement>> mapMethodToParam = new HashMap<>();

        // focusClass 내부의 refactoring이 필요한 method call을 methodsNeedRefactor에 넣음
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);
        for (PsiMethodCallExpression meth : methodCalls) {
            refactorCounter = 0;
            Set<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());
            if (paramsOfMethod.size() < 2) continue;
//            System.out.println("method resolve to " + FindPsi.findChildPsiReferenceExpressions(meth).get(0).resolve());
            List<PsiElement> listOfResolvedParam = new ArrayList<>();
            for (PsiReferenceExpression param : paramsOfMethod) {
                PsiElement resolvedParam = param.resolve();
                assert resolvedParam != null;
                // TODO : 같은 object에서 get한 값인지 확인해야 함
                if (FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0).toString().contains("get")) {
                    refactorCounter += 1;
                    listOfResolvedParam.add(resolvedParam);
                }
            }
            if (refactorCounter >= 2) mapMethodToParam.put(meth, listOfResolvedParam);
        }

        // System.out.println("Test Print : " + mapMethodToParam); // <- map 잘 들어가나 확인
        // {PsiMethodCallExpression:  TestClass.testMethod(aa, bb)
        //                              =[PsiField:aa, PsiField:bb]

        WriteCommandAction.runWriteCommandAction(project, ()->{
            for (Map.Entry<PsiMethodCallExpression, List<PsiElement>> entry : mapMethodToParam.entrySet()) {
                PsiMethodCallExpression focusMethodCall = entry.getKey();
                List<PsiElement> paramsResolved = entry.getValue();
                Set<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(focusMethodCall.getArgumentList());

                // method 콜 부분 : aa, bb 빼고 object 추가하기
                for (PsiReferenceExpression p : paramsOfMethod) {
                    p.delete();
                    // TODO: object를 PsiElement에서 추출해와야 함
                }
            }
        });

        // aa = p.getA() 부분 : PsiField delete하기

        // method 본체 :
        //      parameter 부분 -> aa, bb 빼고 object 추가하기
        //      본체 부분 -> object.getA(), object.getB() 추가하기

    }
}
