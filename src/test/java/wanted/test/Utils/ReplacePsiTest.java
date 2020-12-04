package wanted.test.Utils;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.test.base.LightActionTestCase;
import wanted.utils.AddPsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;

/* style 1 */
public class ReplacePsiTest extends LightActionTestCase {
    PsiUtilsTest testRunner;

    protected AnAction getAction() {
        return testRunner;
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/PsiUtilsTest/ReplacePsiTest";
    }

    /* Test 4: changeModifier */
    public void testReplacePsi4() throws Exception {
        testRunner = new PsiUtilsTest();

        List<String> add = new ArrayList<>(); add.add("private");
        List<String> del = new ArrayList<>(); del.add("public");

        Class[] params = new Class[3];
        params[0] = PsiField.class;
        params[1] = List.class;
        params[2] = List.class;

        Object[] parameters = new Object[3];

        parameters[1] = del;
        parameters[2] = add;

        testRunner.testClass = new ReplacePsi();
        testRunner.testParams = parameters;

        testRunner.testMethod = ReplacePsi.class.getMethod("changeModifier", params);
        doTest();
    }

    public void testReplacePsi4inString() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        // Create dummy class
        // public class Temp {
        //     public int value;
        // }
        final PsiClass targetClass = factory.createClass("Temp");
        PsiField field = factory.createFieldFromText("public int value;", null);
        PsiField newField = factory.createFieldFromText("private int value;", null);
        targetClass.addAfter(field, targetClass.getLBrace());

        System.out.println(targetClass.getText());

        PsiField targetField = null;
        for (PsiElement e : targetClass.getChildren()) {
            if (e instanceof PsiField) {
                targetField = (PsiField) e;
                break;
            }
        }

        List<String> add = new ArrayList<>(); add.add("private");
        List<String> del = new ArrayList<>(); del.add("public");

        PsiField finalTargetField = targetField;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            ReplacePsi.changeModifier(finalTargetField, del, add);
        });

        assertEquals(finalTargetField.getText(), newField.getText());
    }
}
