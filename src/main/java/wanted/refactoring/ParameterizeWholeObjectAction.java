package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.*;

import java.util.*;

/**
 * Class to provide refactoring: 'Parameterize Whole Object'
 *
 * @author Jinu Noh
 */
public class ParameterizeWholeObjectAction extends BaseRefactorAction {
    public Project project;
    private PsiClass focusClass;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "PWO";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Parameterize Whole Object";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When there are more than two parameters originated from same object<br/>" +
                "in a method, replace them in parameter list with source object.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>There exist more than two parameters that are originated from same object</html>";

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

        return refactorValid(project, focusClass);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Parameterize Whole Object'.
     *
     * @param focusClass PsiClass
     * @return true if method is refactorable
     */
    public static boolean refactorValid(Project project, PsiClass focusClass) {
        if (focusClass == null) return false;
        HashMap<String, Integer> paramCounter;

        // ????????? ??? ?????? ????????? ?????? ??????
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);
        for (PsiMethodCallExpression meth : methodCalls) {
            paramCounter = new HashMap<>();
            List<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());
            if (paramsOfMethod.size() < 2) continue;

            // ????????? ?????? ???????????? ????????? ??????
            for (PsiReferenceExpression param : paramsOfMethod) {

                // resolve??? ??????????????? ??????
                PsiElement resolvedParam = param.resolve(); // PsiField:bb ????????? ??????
                assert resolvedParam != null;

                // resolvedParam??? ????????? ?????? ????????? ????????? ???????????? ??????
                if (FindPsi.findPsiMethodCallExpressions(resolvedParam).isEmpty()) continue;

                // resolvedParam?????? ?????? ????????? ??????
                PsiMethodCallExpression methodCallPart = FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0); // TestClass.testMethod(aa, bb) ??????
                PsiReferenceExpression tempPsiReference = FindPsi.findChildPsiReferenceExpressions(methodCallPart).get(0); // TestClass.testMethod ??????
                PsiReferenceExpression callerObject = FindPsi.findChildPsiReferenceExpressions(tempPsiReference).get(0); // TestClass, ???????????? ????????? ???????????? ????????????
                if (tempPsiReference.toString().contains("get")) {
                    if (!paramCounter.containsKey(callerObject.toString())) {
                        paramCounter.put(callerObject.toString(), 0);
                    }
                    paramCounter.put(callerObject.toString(), paramCounter.get(callerObject.toString()) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : paramCounter.entrySet()) {
                if (entry.getValue() >= 2) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Method that performs refactoring: 'Parameterize Whole Object'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        PsiReferenceExpression callerObject = null;
        HashMap<PsiMethodCallExpression, List<PsiReferenceExpression>> mapMethodToParam = new HashMap();

        HashMap<String, Integer> paramCounter;
        List<PsiReferenceExpression> paramsNeedRefactor;

        // ????????? ??? ?????? ????????? ?????? ??????
        List<PsiMethodCallExpression> methodCalls = FindPsi.findPsiMethodCallExpressions(focusClass);
        for (PsiMethodCallExpression meth : methodCalls) {

            paramCounter = new HashMap<>();
            paramsNeedRefactor = new ArrayList<>();

            List<PsiReferenceExpression> paramsOfMethod = FindPsi.findReferenceExpression(meth.getArgumentList());

            if (paramsOfMethod.size() < 2) continue;

            // ????????? ?????? ???????????? ????????? ??????
            for (PsiReferenceExpression param : paramsOfMethod) {

                // resolve??? ??????????????? ????????? ?????? ????????? ????????? ??????
                PsiElement resolvedParam = param.resolve();
                assert resolvedParam != null;
                if (FindPsi.findPsiMethodCallExpressions(resolvedParam).isEmpty()) continue;

                // resolvedParam?????? ?????? ?????? (????????????,?????? ????????????)
                PsiMethodCallExpression methodCallPart = FindPsi.findPsiMethodCallExpressions(resolvedParam).get(0); // TestClass.testMethod(aa, bb) ??????
                PsiReferenceExpression tempPsiReference = methodCallPart.getMethodExpression(); // TestClass.testMethod ??????
                callerObject = FindPsi.findChildPsiReferenceExpressions(tempPsiReference).get(0); // TestClass ???????????? ????????? ???????????? ????????????

                // getter ????????? ????????? ??????????????? ?????? count ??????
                if (tempPsiReference.toString().contains("get")) {
                    paramsNeedRefactor.add(param); // get??? ????????? ??????????????? ?????? ??????
                    if (!paramCounter.containsKey(callerObject.toString())) {
                        paramCounter.put(callerObject.toString(), 0);
                    }
                    paramCounter.put(callerObject.toString(), paramCounter.get(callerObject.toString()) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : paramCounter.entrySet()) {
                if (entry.getValue() >= 2) {
                    mapMethodToParam.put(meth, paramsNeedRefactor);
                }
            }
        }

        PsiType callerObjectType = callerObject.getType();
        PsiIdentifier callerObjectIdentifier = FindPsi.findChildPsiIdentifiers(callerObject).get(0);

        PsiReferenceExpression finalCallerObject = callerObject;
        List<PsiField> psiFieldTobeDeleted = new ArrayList<>();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (Map.Entry<PsiMethodCallExpression, List<PsiReferenceExpression>> entry : mapMethodToParam.entrySet()) {
                PsiMethodCallExpression focusMethodCall = entry.getKey();
                List<PsiReferenceExpression> focusParams = entry.getValue();
                PsiMethod originalPsiMethod = focusMethodCall.resolveMethod();
                List<PsiField> getterField = new ArrayList<>();

                // 1. parameter?????? resolve??? PsiField??? ?????? ???????????????, ????????? ?????? ??????
                for (PsiReferenceExpression p : focusParams) {
                    psiFieldTobeDeleted.add((PsiField) p.resolve());
                    getterField.add((PsiField) p.resolve());
                }

                // 2. method call ????????? ?????????
                PsiMethodCallExpression replacingMethodCall =
                        CreatePsi.createMethodCall(project, (PsiMethod) focusMethodCall.getMethodExpression().resolve(),
                                finalCallerObject, focusMethodCall.getMethodExpression().getQualifier());
                focusMethodCall.replace(replacingMethodCall);

                // 3. method ?????? parameter ?????? :
                //      1) method(int p_a, int p_b)?????? int p_a, int p_b ?????? ??????
                //      2) method(Class obj) ?????? ??????
                PsiParameterList newlyMadeParameterList = CreatePsi.createMethodParameterList(project, callerObjectType, callerObjectIdentifier);
                assert originalPsiMethod != null;
                PsiParameterList oldParameterList = originalPsiMethod.getParameterList(); // <- PsiParameterList:(int aa, boolean bb) ??????
                originalPsiMethod.getParameterList().replace(newlyMadeParameterList);

                // 4. method ?????? code block ?????? :
                //      1) ????????? aa, bb getter ??????
                PsiJavaToken leftBracketOfOriginalMethod = originalPsiMethod.getBody().getLBrace();

                for (int i = 0; i < getterField.size(); i++) {
                    PsiField pField = getterField.get(getterField.size() - i - 1);
                    PsiParameter oldParamName = oldParameterList.getParameter(getterField.size() - i - 1);
                    PsiDeclarationStatement paramGetterStatement = CreatePsi.createGetDeclarationStatement(project, Objects.requireNonNull(pField.getTypeElement()), oldParamName.getName(), FindPsi.findPsiMethodCallExpressions(pField).get(0));
                    originalPsiMethod.getBody().addAfter(paramGetterStatement, leftBracketOfOriginalMethod);
                }
            }
            for (PsiField p : psiFieldTobeDeleted) {
                p.delete();
            }
        });
    }
}
