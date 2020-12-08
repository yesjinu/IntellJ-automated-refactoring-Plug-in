package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.HideDelegateAction;
import wanted.refactoring.IntroduceLocalExtensionAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Introduce Foreign Method Action'
 *
 * @author Chanyoung Kim
 */
public class HideDelegateActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new HideDelegateAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/hideDelegate";
    }

    public void testHideDelegateAction1() throws Exception {
        try {
            doTest();
        }
        catch(RuntimeException e) {
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
