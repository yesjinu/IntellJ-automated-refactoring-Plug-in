package wanted.test.Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.CreatePsi;

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

        Assertions.assertEquals(expected, createElement.getText());
    }

    /* create method call with no parameter */
    public void testCreateMethodCall1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiMethod method = factory.createMethod("testMethod", PsiType.BOOLEAN);
        PsiMethodCallExpression createElement = CreatePsi.createMethodCall(project, method, null, null);

        String expected = "testMethod()";

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

        Assertions.assertEquals(expected, createElement.getText());

    }

    public void testCopyStatement() {

    }

    public void testCreateMergeCondition() {

    }

    public void testCreateDuplicateExpression() {

    }

    public void testCapitalize() {

    }

    public void testCreateEmtpyBlockStatement() {

    }

    public void testCreateField() {

    }

    public void testCreatePsiElement() {

    }

    public void testCreateAssertStatement() {

    }
}
