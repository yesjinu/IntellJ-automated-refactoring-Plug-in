package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.IntroduceLocalExtensionAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Introduce Foreign Method Action'
 *
 * @author Chanyoung Kim
 */
public class IntroduceLocalExtensionActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new IntroduceLocalExtensionAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/introduceLocalExtension";
    }

    public void testIntroduceLocalExtensionAction1() throws Exception {
        try {
            doTest();
        }
        catch(RuntimeException e) {
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    public void testIntroduceLocalExtensionAction2() throws Exception {
        doTest();
    }

    public void testIntroduceLocalExtensionAction3() throws Exception {
        doTest();
    }

    public void testIntroduceLocalExtensionAction4() throws Exception {
        doTest();
    }

    public void testIntroduceLocalExtensionAction5() throws Exception {
        doTest();
    }
}
