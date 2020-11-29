package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import wanted.utils.*;

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
        project = navigator.findProject();
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

        // 클래스 내 모든 메소드 콜을 검사
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);
        for (PsiMethodCallExpression meth : methodCalls) {
            paramCounter = new HashMap<>();
            Set<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());
            if (paramsOfMethod.size() < 2) continue;

            // 메소드 콜의 파라미터 부분을 검사
            for (PsiReferenceExpression param : paramsOfMethod) {

                // resolve한 파라미터를 검사
                PsiElement resolvedParam = param.resolve(); // PsiField:bb 형태로 출력
                assert resolvedParam != null;

                // resolvedParam에 메소드 호출 파트가 없으면 검사하지 않음
                if (FindPsi.findPsiMethodCallExpressions(resolvedParam).isEmpty()) continue;

                // resolvedParam에서 특정 부분만 추출
                PsiMethodCallExpression methodCallPart = FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0); // TestClass.testMethod(aa, bb) 형태
                PsiReferenceExpression tempPsiReference = FindPsi.findChildPsiReferenceExpressions(methodCallPart).get(0); // TestClass.testMethod 형태
                PsiReferenceExpression callerObject = FindPsi.findChildPsiReferenceExpressions(tempPsiReference).get(0); // TestClass 메소드를 호출한 클래스만 받아오기
//                System.out.println("methodCall part : " + methodCallPart); // PsiMethodCallExpression:p.getB()
//                System.out.println("tempPsiReference part : " + tempPsiReference); // PsiReferenceExpression:p.getB
//                System.out.println("callerClass part : " + callerClass); // PsiReferenceExpression:p
//                System.out.println("callerClass resolve to : " + callerClass.resolve()); // PsiField:p
                if (tempPsiReference.toString().contains("get")) {
                    if (!paramCounter.containsKey(callerObject.toString())) {
                        paramCounter.put(callerObject.toString(), 0);
                    }
                    paramCounter.put(callerObject.toString(), paramCounter.get(callerObject.toString()) + 1);
                }
            }

            System.out.println("Param counter : " + paramCounter); // for debugging
            for (Map.Entry<String, Integer> entry : paramCounter.entrySet()) {
                if (entry.getValue() >= 2) {
                    System.out.println("FLAG : refactor valid");
                    return true;
                }
            }
        }
        System.out.println("FLAG : refactor NOT valid");
        return false;
    }

    @Override
    protected void refactor(AnActionEvent e) {
        System.out.println("refactor line 127. Reach Here");
        PsiReferenceExpression callerObject = null;

        HashMap<String, Integer> paramCounter;
        HashMap<PsiMethodCallExpression, List<PsiReferenceExpression>> mapMethodToParam = new HashMap();
        List<PsiReferenceExpression> paramsNeedRefactor;

        // 클래스 내 모든 메소드 콜을 검사
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);

        for (PsiMethodCallExpression meth : methodCalls) {
            paramCounter = new HashMap<>();
            paramsNeedRefactor = new ArrayList<>();

            Set<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());
            if (paramsOfMethod.size() < 2) continue;

            // 메소드 콜의 파라미터 부분을 검사
            for (PsiReferenceExpression param : paramsOfMethod) {

                // resolve한 파라미터를 검사
                PsiElement resolvedParam = param.resolve(); // PsiField:bb 형태로 출력 int bb = TestClass.testMethod(aa, bb); 전체가 resolvedParam
                assert resolvedParam != null;

                // resolvedParam에 메소드 호출 파트가 없으면 검사하지 않음
                if (FindPsi.findPsiMethodCallExpressions(resolvedParam).isEmpty()) continue;

                // resolvedParam에서 특정 부분만 추출
                PsiMethodCallExpression methodCallPart = FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0); // TestClass.testMethod(aa, bb) 형태
                PsiReferenceExpression tempPsiReference = FindPsi.findChildPsiReferenceExpressions(methodCallPart).get(0); // TestClass.testMethod 형태
                callerObject = FindPsi.findChildPsiReferenceExpressions(tempPsiReference).get(0); // TestClass 메소드를 호출한 클래스만 받아오기
//                System.out.println("methodCall part : " + methodCallPart); // PsiMethodCallExpression:p.getB()
//                System.out.println("tempPsiReference part : " + tempPsiReference); // PsiReferenceExpression:p.getB
//                System.out.println("callerClass part : " + callerClass); // PsiReferenceExpression:p
//                System.out.println("callerClass resolve to : " + callerClass.resolve()); // PsiField:p
                // paramCounter라는 map을 통해 같은 object에서 가져온 것을 카운트
                if (tempPsiReference.toString().contains("get")) {
                    paramsNeedRefactor.add(param); // get이 포함된 파라미터를 모두 담음
                    if (!paramCounter.containsKey(callerObject.toString())) {
                        paramCounter.put(callerObject.toString(), 0);
                    }
                    paramCounter.put(callerObject.toString(), paramCounter.get(callerObject.toString()) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : paramCounter.entrySet()) {
                if (entry.getValue() >= 2) {
                    System.out.println("paramCounter : " + paramCounter); // for debugging
                    mapMethodToParam.put(meth, paramsNeedRefactor);
                }
            }
        }

        // System.out.println("mapMethodToParam : " + mapMethodToParam);
        // 출력값 mapMethodToParam :
        // {
        //      PsiMethodCallExpression:TestClass.testMethod(aa, bb, cc)
        //          =[PsiReferenceExpression:aa, PsiReferenceExpression:bb]
        // }

        PsiReferenceExpression finalCallerObject = callerObject;
        WriteCommandAction.runWriteCommandAction(project, ()->{
            for (Map.Entry<PsiMethodCallExpression, List<PsiReferenceExpression>> entry : mapMethodToParam.entrySet()) {
                PsiMethodCallExpression focusMethodCall = entry.getKey();
                List<PsiReferenceExpression> focusParams = entry.getValue();

                // 1. parameter에서 resolve한 PsiField 삭제 : int aa = obj.getA() 부분
                for (PsiReferenceExpression p : focusParams) {
                    Objects.requireNonNull(p.resolve()).delete();
                }

                System.out.println("focusMethodCall : " + focusMethodCall);
                System.out.println("focusMethodCall.getMethodExpression() : " + focusMethodCall.getMethodExpression());
                System.out.println("focusMethodCall.getMethodExpression().resolve() : " + focusMethodCall.getMethodExpression().resolve());


                // 2. 메소드 호출 parameter 수정 :
                //      1) obj.method(aa, bb)에서 aa, bb 부분 삭제 <- 잘 지워짐 확인 focusMethodCall이 PsiMethodCallExpression:TestClass.testMethod( )가 출력됨
                //      2) obj.method(obj) 로 obj 삽입 <-  TODO text로 만들어야 할까?
                for (PsiReferenceExpression p : focusParams) {
                    p.delete();
                };
                PsiMethodCallExpression newlyMadeMethodCall = CreatePsi.createMethodCall(project, (PsiMethod) focusMethodCall.getMethodExpression().resolve(), finalCallerObject, focusMethodCall.getMethodExpression().getQualifier());
                focusMethodCall.replace(newlyMadeMethodCall);
                System.out.println("focusMethodCall : " + focusMethodCall);
                System.out.println("newlyMadeMethodCall : " + newlyMadeMethodCall);


                // 3. method 본체 parameter 수정 :
                //      1) method(int p_a, int p_b)에서 int p_a, int p_b 부분 삭제
                //      2) method(Class obj) 부분 삽입


                // 4. method 본체 code block 수정 :
                //      1) 삭제한 aa, bb getter 추가




            }
        });
    }
}
