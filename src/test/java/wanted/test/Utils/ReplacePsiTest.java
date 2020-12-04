package wanted.test.Utils;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiField;
import wanted.test.base.LightActionTestCase;
import wanted.utils.PsiUtilsTest;
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
}
