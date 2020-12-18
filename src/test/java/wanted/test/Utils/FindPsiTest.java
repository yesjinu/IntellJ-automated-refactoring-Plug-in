
package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
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
 * @author SeungjaeYoo
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

    /* test FindPsi::findCheckDuplicateName - case 1: all elems are duplplicated */
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

    /* test FindPsi::findCheckDuplicateName - case 2: some elems are duplplicated */
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

        String[] elems = {"dummyMethod1", "dummyMethod2", "dummyMethod4"};
        List<String> queries = new ArrayList<>(Arrays.asList(elems));

        String[] lastElem = {"dummyMethod4"};
        List<String> expected = new ArrayList<>(Arrays.asList(lastElem));

        assertEquals(expected, FindPsi.checkDuplicateName(targetClass, queries));
    }

    /* test FindPsi::findCheckDuplicateName - case 3: no elem is duplplicated */
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

        assertEquals(queries, FindPsi.checkDuplicateName(targetClass, queries));
    }

    /* test FindPsi::findChildPsiTypeElements */
    public void testFindChildPsiTypeElements() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField dummyField = factory.createFieldFromText("int a = 3", null);
        PsiMethod dummyMethod = factory.createMethodFromText("void tempMethod() {}", null);

        List<PsiTypeElement> typeList1 = FindPsi.findChildPsiTypeElements(dummyField);
        List<PsiTypeElement> typeList2 = FindPsi.findChildPsiTypeElements(dummyMethod);
        assertEquals(typeList1.size(), 1);
        assertEquals(typeList1.get(0).getType(), PsiType.INT);
        assertEquals(typeList2.size(), 1);
        assertEquals(typeList2.get(0).getType(), PsiType.VOID);
    }
///
    /* test FindPsi::findPsiNewExpressions - two new-expressions */
    public void testFindPsiNewExpressions() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file10.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();

        List<PsiNewExpression> actual = FindPsi.findPsiNewExpressions(focusClass);
        List<String> expected = new ArrayList<>();
        expected.add("new Dummy1()");
        expected.add("new Dummy2()");

        assertEquals(actual.size(), 2);
        for (PsiNewExpression psiNewExpression : actual) {
            assertTrue(expected.contains(psiNewExpression.getText()));
        }
    }

    /* test FindPsi::findPsiNewExpressions - one new-expression */
    public void testFindPsiNewExpressions2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file11.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();

        List<PsiNewExpression> actual = FindPsi.findPsiNewExpressions(focusClass);
        List<String> expected = new ArrayList<>();
        expected.add("new Dummy2()");

        assertEquals(actual.size(), 1);
        for (PsiNewExpression psiNewExpression : actual) {
            assertTrue(expected.contains(psiNewExpression.getText()));
        }
    }

    /* test FindPsi::findPsiNewExpressions - no new-expression  */
    public void testFindPsiNewExpressions3() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file12.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        PsiClass focusClass = navigator.findClass();

        assertTrue(FindPsi.findPsiNewExpressions(focusClass).isEmpty());
    }

    /* test FindPsi::findChildPsiReferenceExpressions - with depth 1 */
    public void testFindChildPsiReferenceExpressions() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("a + b", null);

        List<PsiReferenceExpression> expList = FindPsi.findChildPsiReferenceExpressions(dummyExpression1);

        assertEquals(expList.size(), 2);
        assertEquals(expList.get(0).getQualifiedName(), "a");
        assertEquals(expList.get(1).getQualifiedName(), "b");
    }

    /* test FindPsi::findChildPsiReferenceExpressions - with depth more than 1 */
    public void testFindChildPsiReferenceExpressions2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("a + (c * d)", null);

        List<PsiReferenceExpression> expList = FindPsi.findChildPsiReferenceExpressions(dummyExpression1);

        assertEquals(expList.size(), 1);
        assertEquals(expList.get(0).getQualifiedName(), "a");
    }

    /* test FindPsi::findChildPsiReferenceExpressions - with no reference expression*/
    public void testFindChildPsiReferenceExpressions3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("1 + 1", null);

        List<PsiReferenceExpression> expList = FindPsi.findChildPsiReferenceExpressions(dummyExpression1);

        assertEquals(expList.size(), 0);
    }

    /* test FindPsi::findPsiLiteralExpressions - more than 2 literal expression */
    public void testFindPsiLiteralExpressions() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("1 + (2 + 3)", null);

        List<PsiLiteralExpression> expList = FindPsi.findPsiLiteralExpressions(dummyExpression1);

        assertEquals(expList.size(), 3);
        assertEquals(expList.get(0).getValue(), 1);
        assertEquals(expList.get(1).getType(), PsiType.INT);
        assertEquals(expList.get(2).getValue(), 3);
    }

    /* test FindPsi::findPsiLiteralExpressions - only one literal expression */
    public void testFindPsiLiteralExpressions2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement dummyStatement1 = factory.createStatementFromText("char a = 'k';", null);

        List<PsiLiteralExpression> expList = FindPsi.findPsiLiteralExpressions(dummyStatement1);

        assertEquals(expList.size(), 1);
        assertEquals(expList.get(0).getValue(), 'k');
        assertEquals(expList.get(0).getType(), PsiType.CHAR);
    }

    /* test FindPsi::findPsiLiteralExpressions - no literal expression */
    public void testFindPsiLiteralExpressions3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement dummyStatement1 = factory.createStatementFromText("char a = b;", null);

        List<PsiLiteralExpression> expList = FindPsi.findPsiLiteralExpressions(dummyStatement1);

        assertEquals(expList.size(), 0);
    }

    /* test FindPsi::findChildPsiLiteralExpressions - depth more than 1 */
    public void testFindChildPsiLiteralExpressions() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("1 + (2 + 3)", null);

        List<PsiLiteralExpression> expList = FindPsi.findChildPsiLiteralExpressions(dummyExpression1);

        assertEquals(expList.size(), 1);
        assertEquals(expList.get(0).getType(), PsiType.INT);
        assertEquals(expList.get(0).getValue(), 1);
    }

    /* test FindPsi::findChildPsiLiteralExpressions - depth 1 */
    public void testFindChildPsiLiteralExpressions2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("1 + 2", null);

        List<PsiLiteralExpression> expList = FindPsi.findChildPsiLiteralExpressions(dummyExpression1);

        assertEquals(expList.size(), 2);
        assertEquals(expList.get(0).getType(), PsiType.INT);
        assertEquals(expList.get(1).getValue(), 2);
    }

    /* test FindPsi::findChildPsiLiteralExpressions - no literal expression */
    public void testFindChildPsiLiteralExpressions3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression dummyExpression1 = factory.createExpressionFromText("a + b", null);

        List<PsiLiteralExpression> expList = FindPsi.findChildPsiLiteralExpressions(dummyExpression1);

        assertEquals(expList.size(), 0);
    }

    /* test FindPsi::findChildPsiIdentifiers - PsiField */
    public void testFindChildPsiIdentifiers() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField dummyField1 = factory.createFieldFromText("int a = b + 1;", null);

        List<PsiIdentifier> identifierList = FindPsi.findChildPsiIdentifiers(dummyField1);

        assertEquals(identifierList.size(), 1);
        assertEquals(identifierList.get(0).getText(), "a");
    }

    /* test FindPsi::findChildPsiIdentifiers - PsiStatement */
    public void testFindChildPsiIdentifiers2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement dummyStatement1 = factory.createStatementFromText("{}", null);

        List<PsiIdentifier> identifierList = FindPsi.findChildPsiIdentifiers(dummyStatement1);

        assertEquals(identifierList.size(), 0);
    }

    /* test FindPsi::findChildPsiJavaTokens - PsiField */
    public void testFindChildPsiJavaTokens() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField dummyField1 = factory.createFieldFromText("int a = 1;", null);

        List<PsiJavaToken> tokenList = FindPsi.findChildPsiJavaTokens(dummyField1);

        assertEquals(tokenList.size(), 3);
        assertEquals(tokenList.get(0).getTokenType(), JavaTokenType.IDENTIFIER);
        assertEquals(tokenList.get(1).getTokenType(), JavaTokenType.EQ);
        assertEquals(tokenList.get(2).getTokenType(), JavaTokenType.SEMICOLON);
    }

    /* test FindPsi::findChildPsiJavaTokens - PsiStatement */
    public void testFindChildPsiJavaTokens2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement dummyStatement1 = factory.createStatementFromText("int a = 1;", null);

        List<PsiJavaToken> tokenList = FindPsi.findChildPsiJavaTokens(dummyStatement1);

        assertEquals(tokenList.size(), 0);
    }

    /* test FindPsi::findLiteralUsage - appear only once */
    public void testFindLiteralUsage() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.VOID);
        PsiStatement dummyStatement1 = factory.createStatementFromText("int a = 1;\n", null);
        PsiStatement dummyStatement2 = factory.createStatementFromText("int b = 2;\n", null);
        dummyMethod1.getBody().addBefore(dummyStatement1, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement2, dummyMethod1.getBody().getRBrace());
        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());

        PsiLiteralExpression dummyLiteral1 = (PsiLiteralExpression) factory.createExpressionFromText("1", null);

        List<PsiLiteralExpression> expList = FindPsi.findLiteralUsage(dummyMethod1, dummyLiteral1);

        assertEquals(expList.size(), 1);
        assertEquals(expList.get(0).getType(), PsiType.INT);
        assertEquals(expList.get(0).getValue(), 1);
    }

    /* test FindPsi::findLiteralUsage - appear twice */
    public void testFindLiteralUsage2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.VOID);
        PsiStatement dummyStatement1 = factory.createStatementFromText("int a = 1;\n", null);
        PsiStatement dummyStatement2 = factory.createStatementFromText("double b = 0.2;\n", null);
        PsiStatement dummyStatement3 = factory.createStatementFromText("double c = 0.2;\n", null);
        PsiStatement dummyStatement4 = factory.createStatementFromText("double d = 0.1;\n", null);
        dummyMethod1.getBody().addBefore(dummyStatement1, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement2, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement3, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement4, dummyMethod1.getBody().getRBrace());
        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());

        PsiLiteralExpression dummyLiteral1 = (PsiLiteralExpression) factory.createExpressionFromText("0.2", null);

        List<PsiLiteralExpression> expList = FindPsi.findLiteralUsage(dummyMethod1, dummyLiteral1);

        assertEquals(expList.size(), 2);
        assertEquals(expList.get(0).getType(), PsiType.DOUBLE);
        assertEquals(expList.get(1).getValue(), 0.2);
    }

    /* test FindPsi::findLiteralUsage - do not appear */
    public void testFindLiteralUsage3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiClass targetClass = factory.createClass("Temp");

        PsiMethod dummyMethod1 = factory.createMethod("dummyMethod1", PsiType.VOID);
        PsiStatement dummyStatement1 = factory.createStatementFromText("int a = 1;\n", null);
        PsiStatement dummyStatement2 = factory.createStatementFromText("double b = 0.2;\n", null);
        PsiStatement dummyStatement3 = factory.createStatementFromText("double c = 0.2;\n", null);
        PsiStatement dummyStatement4 = factory.createStatementFromText("double d = 0.1;\n", null);
        dummyMethod1.getBody().addBefore(dummyStatement1, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement2, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement3, dummyMethod1.getBody().getRBrace());
        dummyMethod1.getBody().addBefore(dummyStatement4, dummyMethod1.getBody().getRBrace());
        targetClass.addBefore(dummyMethod1, targetClass.getRBrace());

        PsiLiteralExpression dummyLiteral1 = (PsiLiteralExpression) factory.createExpressionFromText("0.3", null);

        List<PsiLiteralExpression> expList = FindPsi.findLiteralUsage(dummyMethod1, dummyLiteral1);

        assertEquals(expList.size(), 0);
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
