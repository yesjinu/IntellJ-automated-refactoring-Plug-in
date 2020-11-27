package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.IntroduceForeignMethodAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Introduce Foreign Method Action'
 *
 * @author Chanyoung Kim
 */
public class IntroduceForeignMethodActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new IntroduceForeignMethodAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/introduceForeignMethod";
    }

    public void testIntroduceForeignMethodAction1() throws Exception {
        doTest();
    }

    public void testIntroduceForeignMethodAction2() throws Exception {
        doTest();
    }

    public void testIntroduceForeignMethodAction3() throws Exception {
        doTest();
    }

    public void testIntroduceForeignMethodAction4() throws Exception {
        doTest();
    }

    public void testIntroduceForeignMethodAction5() throws Exception {
        doTest();
    }

    public void testIntroduceForeignMethodAction6() throws Exception {
        try {
            doTest();
        }
        catch(RuntimeException e) {
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
