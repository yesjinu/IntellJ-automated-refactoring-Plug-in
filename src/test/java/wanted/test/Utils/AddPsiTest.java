package wanted.test.Utils;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.LightActionTestCase;
import wanted.utils.AddPsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for 'AddPsi' util
 *
 * @author Jinu Noh
 */

public class AddPsiTest extends LightActionTestCase {

    protected AnAction getAction() {
        return null;
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/PsiUtilsTest/AddPsiTest";
    }

    // test AddPsi::addMethod - case 1 : Add one method to a class
    public void testAddMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");

        List<PsiElement> methodList = new ArrayList<>();
        PsiMethod method = factory.createMethodFromText("void tempMethod() { }", null);
        methodList.add(method);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addMethod(targetClass, methodList);
        });

        assertFalse(targetClass.findMethodsByName("tempMethod", false).length == 2);
        assertEquals(1, targetClass.findMethodsByName("tempMethod", false).length);
        assertFalse(targetClass.findMethodsByName("tempMethod", false).length == 0);

        assertFalse(targetClass.findMethodsByName("anotherName", false).length == 2);
        assertFalse(targetClass.findMethodsByName("anotherName", false).length == 1);
        assertEquals(0, targetClass.findMethodsByName("anotherName", false).length);
    }

    // test AddPsi::addMethod - case 2 : Add multiple methods to a class
    public void testAddMethod2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");

        List<PsiElement> methodList = new ArrayList<>();

        PsiMethod method1 = factory.createMethodFromText("void tempMethod1() { }", null);
        PsiMethod method2 = factory.createMethodFromText("void tempMethod2() { }", null);

        methodList.add(method1);
        methodList.add(method2);


        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addMethod(targetClass, methodList);
        });

        assertTrue(targetClass.findMethodsByName("tempMethod1", false).length == 1);
        assertTrue(targetClass.findMethodsByName("tempMethod2", false).length == 1);
    }

    // test AddPsi::addField - case 1 : Add two fields to an empty class
    public void testAddField() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");

        List<PsiField> fieldList = new ArrayList<>();
        PsiField field1 = factory.createFieldFromText("int a = 0;", null);
        PsiField field2 = factory.createFieldFromText("boolean b = true;", null);
        fieldList.add(field1);
        fieldList.add(field2);

        String expected =
                "public class Temp {int a = 0;boolean b = true; }";

        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addField(targetClass, fieldList);
        });

        Assertions.assertEquals(expected, targetClass.getText());

    }

    // test AddPsi::addField - case 2 : Add two fields to an filled class
    // addFields method should add fields before Psifield that already exists
    public void testAddField2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");
        PsiMethod preMethod = factory.createMethodFromText("void doNothing() {}", null);
        PsiField preField = factory.createFieldFromText("char c = 'c';", null);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            targetClass.add(preMethod);
            targetClass.add(preField);
        });

        List<PsiField> fieldList = new ArrayList<>();
        PsiField field1 = factory.createFieldFromText("int a = 0;", null);
        PsiField field2 = factory.createFieldFromText("boolean b = true;", null);
        fieldList.add(field1);
        fieldList.add(field2);

        String expected =
                "public class Temp {int a = 0;boolean b = true; char c = 'c';void doNothing() {}}";

        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addField(targetClass, fieldList);
        });

        Assertions.assertEquals(expected, targetClass.getText());

    }
}
