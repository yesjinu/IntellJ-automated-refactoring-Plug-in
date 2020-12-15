package wanted.test.Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.CreatePsi;

import java.util.*;

/**
 * Test class for Utils.CreatePsi
 *
 * @author seha Park
 */
public class CreatePsiTest extends AbstractLightCodeInsightTestCase {

    /* createSetMethod test : create set method call */
    public void testCreateIdentifier() {
        Project project = getProject();

        String text = "test";
        PsiElement createElement = CreatePsi.createIdentifier(project, text);

        String expected = "test";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateStatement() {
        Project project = getProject();

        String text = "int x = 1;";
        PsiElement createElement = CreatePsi.createStatement(project, text);

        String expected = "int x = 1;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateSetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("abc", PsiType.INT);
        String accessModifier = PsiModifier.PUBLIC;

        PsiMethod createElement = CreatePsi.createSetMethod(project, member, accessModifier);

        String expected = "public void setAbc(int newValue) {\n" +
                "abc = newValue;\n}";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* createGetMethod test : create get method call */
    public void testCreateGetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("abc", PsiType.DOUBLE);
        String accessModifier = PsiModifier.PUBLIC;

        PsiMethod createElement = CreatePsi.createGetMethod(project, member, accessModifier);

        String expected = "public double getAbc() {\n" +
                "return abc;\n}";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateMethodCall test 1: create method call with no parameter and no qualifier */
    public void testCreateMethodCall1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiMethod method = factory.createMethod("testMethod", PsiType.BOOLEAN);

        PsiMethodCallExpression createElement = CreatePsi.createMethodCall(project, method, null, null);

        String expected = "testMethod()";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateMethodCall test 2: create method call with parameter */
    public void testCreateMethodCall2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiMethod method = factory.createMethod("testMethod", PsiType.BOOLEAN);
        PsiElement param = factory.createExpressionFromText("firstParam", null);

        PsiMethodCallExpression createElement = CreatePsi.createMethodCall(project, method, param, null);

        String expected = "testMethod(firstParam)";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateMethodCall test 3: create method call with qualifier */
    public void testCreateMethodCall3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiMethod method = factory.createMethod("testMethod", PsiType.BOOLEAN);
        PsiElement param = factory.createExpressionFromText("firstParam", null);
        PsiElement qualifier = factory.createExpressionFromText("caller", null);
        PsiMethodCallExpression createElement = CreatePsi.createMethodCall(project, method, param, qualifier);

        String expected = "caller.testMethod(firstParam)";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateMergeCondition test 1: createMergeCondition() hasn't been called before */
    public void testCreateMergeCondition1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression Left = factory.createExpressionFromText("x==1", null);
        PsiExpression Right = factory.createExpressionFromText("y==2", null);

        PsiExpression createElement = CreatePsi.createMergeCondition(project, Left, Right, true);

        String expected = "(x==1) || (y==2)";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateMergeCondition test 2: createMergeCondition() has been called before */
    public void testCreateMergeCondition2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression Left = factory.createExpressionFromText("(x==1) || (z==1)", null);
        PsiExpression Right = factory.createExpressionFromText("y==2", null);

        PsiExpression createElement = CreatePsi.createMergeCondition(project, Left, Right, false);

        String expected = "(x==1) || (z==1) || (y==2)";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateEmtpyBlockStatement() {
        Project project = getProject();

        PsiStatement createElement = CreatePsi.createEmptyBlockStatement(project);

        String expected = "{}";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateMethodParameterList() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiType paramType = PsiType.BOOLEAN;
        PsiIdentifier paramIdentifier = factory.createIdentifier("value");
        PsiParameterList createElement = CreatePsi.createMethodParameterList(project, paramType, paramIdentifier);

        String expected = "(boolean value)";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateGetDeclarationStatement() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField parent = factory.createField("value", PsiType.INT); // PsiField
        PsiTypeElement typeElem = parent.getTypeElement();
        String varName = "value";
        PsiMethodCallExpression methodCallExp = (PsiMethodCallExpression) factory.createExpressionFromText("method1(x, y)", null);
        PsiDeclarationStatement createElement = CreatePsi.createGetDeclarationStatement(project, typeElem, varName, methodCallExp);

        String expected = "int value = method1(x, y);";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateField test 1: when there's no initializer */
    public void testCreateField1() {
        Project project = getProject();

        String[] modifiers = {PsiModifier.STATIC};
        PsiType type = PsiType.DOUBLE;
        String name = "test1";

        PsiField createElement = CreatePsi.createField(project, modifiers, type, name, null);

        String expected = "private static double test1;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateField test 2: when initializer is provided */
    public void testCreateField2() {
        Project project = getProject();

        String[] modifiers = {PsiModifier.STATIC, PsiModifier.PUBLIC};
        PsiType type = PsiType.LONG;
        String name = "test1";
        String value = "2147483648L";

        PsiField createElement = CreatePsi.createField(project, modifiers, type, name, value);

        String expected = "public static long test1=2147483648L;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateAssertStatement test 1: when elseSet is empty */
    public void testCreateAssertStatement1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);

        List<PsiReferenceExpression> thenSet = new ArrayList<>();
        PsiReferenceExpression expression1 = (PsiReferenceExpression) factory.createExpressionFromText("a", null);
        thenSet.add(expression1);
        PsiReferenceExpression expression2 = (PsiReferenceExpression) factory.createExpressionFromText("b", null);
        thenSet.add(expression2);

        List<PsiReferenceExpression> elseSet = new ArrayList<>();

        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected1 = "assert (!(x==1) || ((a != null) && (b != null)));";
        String expected2 = "assert (!(x==1) || ((b != null) && (a != null)));";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertTrue(expected1.equals(createElement.getText()) || expected2.equals(createElement.getText()));
    }

    /* CreateAssertStatement test 2: thenSet is empty */
    public void testCreateAssertStatement2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);

        List<PsiReferenceExpression> elseSet = new ArrayList<>();
        PsiReferenceExpression expression1 = (PsiReferenceExpression) factory.createExpressionFromText("a", null);
        elseSet.add(expression1);
        PsiReferenceExpression expression2 = (PsiReferenceExpression) factory.createExpressionFromText("b", null);
        elseSet.add(expression2);

        List<PsiReferenceExpression> thenSet = new ArrayList<>();
        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected1 = "assert ((x==1) || ((a != null) && (b != null)));";
        String expected2 = "assert ((x==1) || ((b != null) && (a != null)));";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertTrue(expected1.equals(createElement.getText()) || expected2.equals(createElement.getText()));
    }

    /* CreateAssertStatement test 3: both elseSet and thenSet are not empty */
    public void testCreateAssertStatement3() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);

        List<PsiReferenceExpression> thenSet = new ArrayList<>();
        PsiReferenceExpression expression1 = (PsiReferenceExpression) factory.createExpressionFromText("a", null);
        thenSet.add(expression1);
        PsiReferenceExpression expression2 = (PsiReferenceExpression) factory.createExpressionFromText("b", null);
        thenSet.add(expression2);

        List<PsiReferenceExpression> elseSet = new ArrayList<>();
        PsiReferenceExpression expression3 = (PsiReferenceExpression) factory.createExpressionFromText("c", null);
        elseSet.add(expression3);
        PsiReferenceExpression expression4 = (PsiReferenceExpression) factory.createExpressionFromText("d", null);
        elseSet.add(expression4);

        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected1 = "assert (((x==1) && (a != null) && (b != null)) || (!(x==1) && (c != null) && (d != null)));";
        String expected2 = "assert (((x==1) && (b != null) && (a != null)) || (!(x==1) && (c != null) && (d != null)));";
        String expected3 = "assert (((x==1) && (a != null) && (b != null)) || (!(x==1) && (d != null) && (c != null)));";
        String expected4 = "assert (((x==1) && (b != null) && (a != null)) || (!(x==1) && (d != null) && (c != null)));";
        List<String> expectedResults = new ArrayList<>(Arrays.asList(expected1, expected2, expected3, expected4));

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertTrue(expectedResults.contains(createElement.getText()));
    }

    /* CreateAssertStatement test 4: size of thenSet is 1, elseSet is 0 */
    public void testCreateAssertStatement4() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);

        List<PsiReferenceExpression> thenSet = new ArrayList<>();
        PsiReferenceExpression expression1 = (PsiReferenceExpression) factory.createExpressionFromText("a", null);
        thenSet.add(expression1);

        List<PsiReferenceExpression> elseSet = new ArrayList<>();

        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected = "assert (!(x==1) || (a != null));";


        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateAssertStatement test 5: size of elseSet is 1, thenSet is 0 */
    public void testCreateAssertStatement5() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);

        List<PsiReferenceExpression> elseSet = new ArrayList<>();
        PsiReferenceExpression expression1 = (PsiReferenceExpression) factory.createExpressionFromText("a", null);
        elseSet.add(expression1);

        List<PsiReferenceExpression> thenSet = new ArrayList<>();

        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected = "assert ((x==1) || (a != null));";


        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateExtractVariable test 1: when exp.type is know */
    public void testCreateExtractVariableAssignStatement1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String varName = "value";
        PsiExpression exp = factory.createExpressionFromText("8", null);
        PsiStatement createElement = CreatePsi.createExtractVariableAssignStatement(project, varName, exp);

        String expected = "final int value = 8;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* CreateExtractVariable test 2: when exp.type is not known */
    public void testCreateExtractVariableAssignStatement2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String varName = "value";
        PsiExpression exp = factory.createExpressionFromText("x", null);
        PsiStatement createElement = CreatePsi.createExtractVariableAssignStatement(project, varName, exp);

        /* createExtractVariableAssignmentStatement fail to create statement(returns null)*/
        Assertions.assertNull(createElement);
    }

    public void testCreateExpression() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression expression = factory.createExpressionFromText("x = 1", null);
        Assertions.assertTrue(expression.isValid());

        String expAsString = expression.getText();
        PsiExpression createElement = CreatePsi.createExpression(project, expAsString);

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expression.getText(), createElement.getText());
        Assertions.assertEquals(expression.getType(), createElement.getType());
    }

    public void testCopyExpression() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression expression = factory.createExpressionFromText("x = 1", null);
        Assertions.assertTrue(expression.isValid());

        PsiExpression createElement = CreatePsi.copyExpression(project, expression);

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expression.getText(), createElement.getText());
        Assertions.assertEquals(expression.getType(), createElement.getType());
    }

    public void testCopyStatement() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement statement = factory.createStatementFromText("int x = 1;", null);
        PsiStatement createElement = CreatePsi.copyStatement(project, statement);

        String expected = "int x = 1;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCapitalize() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("test", PsiType.INT);
        String createElement = CreatePsi.capitalize(member);

        String expected = "Test";

        Assertions.assertEquals(expected, createElement);
    }
}
