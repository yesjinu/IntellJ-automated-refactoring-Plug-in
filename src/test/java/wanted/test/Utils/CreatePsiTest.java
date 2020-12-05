package wanted.test.Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.CreatePsi;

import java.util.HashSet;
import java.util.Set;


/**
 * Test class for Utils.CreatePsi
 *
 * @author seha Park
 */
public class CreatePsiTest extends AbstractLightCodeInsightTestCase {

    public void testCreateSetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("abc", PsiType.INT);
        String accessModifier = "public";
        PsiMethod createElement = CreatePsi.createSetMethod(project, member, accessModifier);

        String expected = "public void setAbc(int newValue) {\n" +
                "abc = newValue;\n}";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreateGetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("abc", PsiType.DOUBLE);
        String accessModifier = "public";
        PsiMethod createElement = CreatePsi.createGetMethod(project, member, accessModifier);

        String expected = "public double getAbc() {\n" +
                "return abc;\n}";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* create method call with no parameter */
    public void testCreateMethodCall1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiMethod method = factory.createMethod("testMethod", PsiType.BOOLEAN);
        PsiMethodCallExpression createElement = CreatePsi.createMethodCall(project, method, null, null);

        String expected = "testMethod()";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    /* create method call with parameter */
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

    /* create method call with qualifier */
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

    /* when CreateMergeCondition first invoked */
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

    /* when CreateMergeCondition was invoked before */
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

    /* no initializer for field */
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

    /* initializer for field provided */
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

    public void testCreateAssertStatement() { // TODO
       /* Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression condition = factory.createExpressionFromText("x==1", null);
        Set<PsiReferenceExpression> thenSet = new HashSet<>();
        Set<PsiReferenceExpression> elseSet = new HashSet<>();
        PsiStatement createElement = CreatePsi.createAssertStatement(project, condition, thenSet, elseSet);

        String expected = "public static long test1=2147483648L;";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText()); */
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

    public void testCreateDuplicateExpression() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression expression = factory.createExpressionFromText("x", null);
        PsiExpression createElement = CreatePsi.createDuplicateExpression(project, expression);

        String expected = "x";

        Assertions.assertTrue(createElement.isValid());
        Assertions.assertEquals(expected, createElement.getText());
    }

    public void testCreatePsiElement() {
        Project project = getProject();

        String content = "newElement";
        PsiElement createElement = CreatePsi.createPsiElement(project, content);

        String expected = "newElement";

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
