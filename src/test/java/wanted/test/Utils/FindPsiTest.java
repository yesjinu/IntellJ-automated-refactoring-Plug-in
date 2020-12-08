
package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.ide.navigationToolbar.NavBarActions;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.test.base.LightActionTestCase;
import wanted.utils.AddPsi;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for 'FindPsi' util
 *
 * @author Jinu Noh
 */

public class FindPsiTest extends AbstractLightCodeInsightTestCase {
    /* test FindPsi::findMemberReference  */
    public void testFindMemberReference1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Project project = navigator.findProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiClass targetClass = navigator.findClass();
        PsiField targetField = navigator.findField();

        PsiReferenceExpression expected = (PsiReferenceExpression) factory.createExpressionFromText("dummyValue", null);

        assertEquals(FindPsi.findMemberReference(targetClass, targetField).get(0).getText(), expected.getText());
    }

    //TODO: directory 에서 getFiles 안 되는 오류 해결하기
    public void testFindMemberReference2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        PsiFile file = navigator.findFile();
        Project project = navigator.findProject();
        PsiClass targetClass = navigator.findClass();
        PsiField targetField = navigator.findField();

        System.out.println("file : " + file);
        System.out.println("project : " + project);
        System.out.println("targetClass : " + targetClass);
        System.out.println("targetField : " + targetField);

        // directory path까지는 잘 찾아짐. 그런데 왜 디렉토리에서 getFiles하면 안 받아질까?
        System.out.println("file.getContainingDirectory() : " + file.getContainingDirectory());
        System.out.println("file.getContainingDirectory().getFiles() : " + Arrays.toString(file.getContainingDirectory().getFiles()));

        System.out.println(FindPsi.findMemberReference(file, targetField));

    }

    /* test FindPsi::findParametersOfMethod - case 1 : more than 2 parameters */
    public void testFindParametersOfMethod() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file3.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiMethod targetMethod = navigator.findMethod();

        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiParameter param_a = factory.createParameterFromText("int a", targetMethod);

        assertTrue(FindPsi.findParametersOfMethod(targetMethod).toString().contains(param_a.toString()));
    }

    /* test FindPsi::findParametersOfMethod - case 2 : only 1 parameters */
    public void testFindParametersOfMethod2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file2.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiMethod targetMethod = navigator.findMethod();

        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiParameter param_a = factory.createParameterFromText("int a", targetMethod);
        PsiParameter param_b = factory.createParameterFromText("boolean b", targetMethod);
        PsiParameter param_c = factory.createParameterFromText("char c", targetMethod);

        assertTrue(FindPsi.findParametersOfMethod(targetMethod).toString().contains(param_a.toString()));
        assertTrue(FindPsi.findParametersOfMethod(targetMethod).toString().contains(param_b.toString()));
        assertTrue(FindPsi.findParametersOfMethod(targetMethod).toString().contains(param_c.toString()));
    }

    /* test FindPsi::findParametersOfMethod - case 3 : no parameters */
    public void testFindParametersOfMethod3() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file4.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiMethod targetMethod = navigator.findMethod();

        assertTrue(FindPsi.findParametersOfMethod(targetMethod).isEmpty());
    }

    /* test FindPsi::findPsiMethodCallExpression - case 1 : more than 2 methodCalls */
    public void testFindPsiMethodCallExpression() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.INT);
        PsiMethodCallExpression dummyCall1 = CreatePsi.createMethodCall(project, dummyMethod1, null, null);

        PsiMethod dummyMethod2 = factory.createMethod("dummyMethod2", PsiType.BOOLEAN);
        PsiMethodCallExpression dummyCall2 = CreatePsi.createMethodCall(project, dummyMethod2, null, null);

        PsiMethod dummyMethod3 = factory.createMethod("dummyMethod3", PsiType.CHAR);
        PsiMethodCallExpression dummyCall3 = CreatePsi.createMethodCall(project, dummyMethod3, null, null);

        List<PsiMethodCallExpression> expected = new ArrayList<>();
        expected.add(dummyCall1);
        expected.add(dummyCall2);
        expected.add(dummyCall3);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            targetClass.addBefore(dummyCall1, targetClass.getRBrace());
            targetClass.addBefore(dummyCall2, targetClass.getRBrace());
            targetClass.addBefore(dummyCall3, targetClass.getRBrace());
        });

        assertEquals(expected.toString(), FindPsi.findPsiMethodCallExpressions(targetClass).toString());
    }

    /* test FindPsi::findPsiMethodCallExpression - case 2 : only 1 methodCall */
    public void testFindPsiMethodCallExpression2 () {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.INT);
        PsiMethodCallExpression dummyCall1 = CreatePsi.createMethodCall(project, dummyMethod1, null, null);

        List<PsiMethodCallExpression> expected = new ArrayList<>();
        expected.add(dummyCall1);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            targetClass.addBefore(dummyCall1, targetClass.getRBrace());
        });

        assertEquals(expected.toString(), FindPsi.findPsiMethodCallExpressions(targetClass).toString());
    }

    /* test FindPsi::findPsiMethodCallExpression - case 3 : no methodCall */
    public void testFindPsiMethodCallExpression3 () {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        assertTrue(FindPsi.findPsiMethodCallExpressions(targetClass).isEmpty());
    }

    /* test FindPsi::findIfStatement - case 1 : there is if statement */
    public void testFindIfStatement () throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file5.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass targetClass = navigator.findClass();
        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();

        String expected = "if (i == 1) {\n" +
                "            j = 1;\n" +
                "            k = 1;\n" +
                "        }\n" +
                "        else if (i == 2) {\n" +
                "            j = 1;\n" +
                "            k = 2;\n" +
                "        }\n" +
                "        else {\n" +
                "            j = 1;\n" +
                "            k = 3;\n" +
                "        }";

        assertEquals(FindPsi.findIfStatement(targetClass, offset).getText(), expected);
    }

    /* test FindPsi::findIfStatement - case 2 : no if statement. return null */
    public void testFindIfStatement2 () throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass targetClass = navigator.findClass();
        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();

        assertNull(FindPsi.findIfStatement(targetClass, offset));
    }


    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileName file to test
     * @return AnActionEvent with given file context
     */
    public AnActionEvent createAnActionEvent(String fileName) throws TimeoutException, ExecutionException {
        if(fileName!=null) {
            myFixture.configureByFile("testData/findPsi/" + fileName);
        }

        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // do nothing
            }
        };

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        return anActionEvent;
    }
}
