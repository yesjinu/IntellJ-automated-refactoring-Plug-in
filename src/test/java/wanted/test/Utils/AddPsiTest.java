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
import java.util.Objects;

/**
 * Test class for 'AddPsi' util
 *
 * @author Jinu Noh
 */
public class AddPsiTest extends LightActionTestCase {
    PsiUtilsTest testRunner;

    protected AnAction getAction() {
        return testRunner;
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/PsiUtilsTest/AddPsiTest";
    }

    public void testAddMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
//        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");

        List<PsiElement> methodList = new ArrayList<>();
        PsiMethod method = factory.createMethodFromText("void sendReport() { }", null);
        methodList.add(method);


        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addMethod(targetClass, methodList);
        });

        assertFalse(targetClass.findMethodsByName("sendReport", false).length == 2);
        assertTrue(targetClass.findMethodsByName("sendReport", false).length == 1);
        assertFalse(targetClass.findMethodsByName("sendReport", false).length == 0);

        assertFalse(targetClass.findMethodsByName("anotherName", false).length == 2);
        assertFalse(targetClass.findMethodsByName("anotherName", false).length == 1);
        assertTrue(targetClass.findMethodsByName("anotherName", false).length == 0);
    }

    public void testAddMethod2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        final PsiClass targetClass = factory.createClass("Temp");

        List<PsiElement> methodList = new ArrayList<>();
        PsiMethod method1 = factory.createMethodFromText("void sendReport1() { }", null);
        PsiMethod method2 = factory.createMethodFromText("void sendReport2() { }", null);
        methodList.add(method1);
        methodList.add(method2);


        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addMethod(targetClass, methodList);
        });

        assertTrue(targetClass.findMethodsByName("sendReport1", false).length == 1);
        assertTrue(targetClass.findMethodsByName("sendReport2", false).length == 1);
    }

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
}
