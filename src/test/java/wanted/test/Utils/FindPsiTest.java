
package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.ide.navigationToolbar.NavBarActions;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.PsiExpression;
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

    /* test FindPsi::getContainingClass - a class includes a method */
    public void testGetContainingClass() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file7.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass expectedClass = navigator.findClass();
        PsiMethod targetMethod = navigator.findMethod();

        assertEquals(FindPsi.getContainingClass(targetMethod), expectedClass);
    }

    /* test FindPsi::findPsiFields - 3 fields in one class */
    public void testFindPsiFields() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiField dummyField1 = CreatePsi.createField(project, new String[]{"private"}, PsiType.INT, "nameA", "10");
        PsiField dummyField2 = CreatePsi.createField(project, new String[]{"public"}, PsiType.BOOLEAN, "nameB", "true");
        PsiField dummyField3 = CreatePsi.createField(project, new String[]{"protected"}, PsiType.CHAR, "nameC", "c");

        List<PsiField> expected = new ArrayList<>();
        expected.add(dummyField1);
        expected.add(dummyField2);
        expected.add(dummyField3);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            targetClass.addBefore(dummyField1, targetClass.getRBrace());
            targetClass.addBefore(dummyField2, targetClass.getRBrace());
            targetClass.addBefore(dummyField3, targetClass.getRBrace());
        });

        assertEquals(expected.toString(), FindPsi.findPsiFields(targetClass).toString());
    }

    /* test FindPsi::findPsiFields - no fields in class */
    public void testFindPsiFields2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        assertTrue(FindPsi.findPsiFields(targetClass).isEmpty());
    }

    /* test FindPsi::findPsiDeclarationStatements - more than two psi declaration statements in class */
    public void testFindPsiDeclarationStatements() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file8.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();

        List<String> expected = new ArrayList<>();
        expected.add("Date today = new Date(2020, 12, 9);");
        expected.add("Date tomorrow = new Date(2020, 12, 10);");

        List<PsiDeclarationStatement> actual = FindPsi.findPsiDeclarationStatements(focusClass);
        assertEquals(actual.size(), expected.size());
        for (PsiDeclarationStatement p : actual) {
            assertTrue(expected.contains(p.getText()));
        }
    }

    /* test FindPsi::findPsiDeclarationStatements - no declaration statement in class */
    public void testFindPsiDeclarationStatements2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file7.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();

        List<PsiDeclarationStatement> actual = FindPsi.findPsiDeclarationStatements(focusClass);
        assertTrue(actual.isEmpty());
    }

    /* test FindPsi::findChildPsiExpressions - one child, one grand child PsiExp */
    public void testFindChildPsiExpressions() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");
        PsiClass subClass = factory.createClass("Sub");

        PsiExpression targetExp = factory.createExpressionFromText("x = targetField", targetClass);
        PsiExpression subExp = factory.createExpressionFromText("System.out.println(targetField)", targetClass);

        targetClass.addBefore(targetExp, targetClass.getRBrace());
        subClass.addBefore(subExp, subClass.getRBrace());
        targetClass.addBefore(subClass, targetClass.getRBrace());

        assertEquals(FindPsi.findChildPsiExpressions(targetClass).get(0).getText(), targetExp.getText());
    }

    /* test FindPsi::findChildPsiExpressions - no child, two grand child PsiExp */
    public void testFindChildPsiExpressions2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");
        PsiClass subClass = factory.createClass("Sub");

        PsiExpression subExp1 = factory.createExpressionFromText("x = targetField", subClass);
        PsiExpression subExp2 = factory.createExpressionFromText("System.out.println(targetField)", subClass);

        subClass.addBefore(subExp1, subClass.getRBrace());
        subClass.addBefore(subExp2, subClass.getRBrace());
        targetClass.addBefore(subClass, targetClass.getRBrace());

        assertTrue(FindPsi.findChildPsiExpressions(targetClass).isEmpty());
    }


    /* test FindPsi::findChildPsiLocalVariables */
    public void testFindChildPsiLocalVariables() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file9.java");
        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        PsiClass focusClass = navigator.findClass();
        PsiStatement focusStatements = FindPsi.findStatement(focusClass, offset);

        String expected = "PsiLocalVariable:c";

        assertEquals(FindPsi.findChildPsiLocalVariables(focusStatements).get(0).toString(), expected);
    }

    public void testFindChildPsiTypeElements() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file10.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();
        PsiMethod[] focusMethods = focusClass.getMethods();

    }


    public void testCheckDuplicateName() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.INT);
        PsiMethod dummyMethod2 = factory.createMethod("dummyMethod2", PsiType.INT);
        PsiMethod dummyMethod3 = factory.createMethod("dummyMethod3", PsiType.INT);

        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod2, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod3, targetClass.getRBrace());

        List<String> queries = new ArrayList<>();
        String[] elems = {"dummyMethod1", "dummyMethod2", "dummyMethod3"};
        queries.addAll(Arrays.asList(elems));

        assertTrue(FindPsi.checkDuplicateName(targetClass, queries).isEmpty());
    }

    public void testCheckDuplicateName2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.INT);
        PsiMethod dummyMethod2 = factory.createMethod("dummyMethod2", PsiType.INT);
        PsiMethod dummyMethod3 = factory.createMethod("dummyMethod3", PsiType.INT);

        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod2, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod3, targetClass.getRBrace());

        List<String> queries = new ArrayList<>();
        String[] elems = {"dummyMethod1", "dummyMethod2", "dummyMethod4"};
        queries.addAll(Arrays.asList(elems));

        String[] expected = {"dummyMethod3"};

        assertEquals(FindPsi.checkDuplicateName(targetClass, queries), expected);
    }

    public void testCheckDuplicateName3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.INT);
        PsiMethod dummyMethod2 = factory.createMethod("dummyMethod2", PsiType.INT);
        PsiMethod dummyMethod3 = factory.createMethod("dummyMethod3", PsiType.INT);

        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod2, targetClass.getRBrace());
        targetClass.addBefore(dummyMethod3, targetClass.getRBrace());

        List<String> queries = new ArrayList<>();
        String[] elems = {"dummyMethod4", "dummyMethod5", "dummyMethod6"};
        queries.addAll(Arrays.asList(elems));

        assertEquals(FindPsi.checkDuplicateName(targetClass, queries), queries);
    }

//    public void testFindPsiNewExpressions()
//    public void testFindChildPsiNewExpressions()
//    public void testFindChildPsiJavaCodeReferenceElements()
//    public void testFindChildPsiExpressionLists()
//    public void testFindChildPsiReferenceExpressions()
//    public void testFindPsiLiteralExpressions()
//    public void testFindChildPsiLiteralExpressions()
//    public void testFindChildPsiIdentifiers()
//    public void testFindChildPsiJavaTokens()
//    public void testFindLiteralUsage()


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
