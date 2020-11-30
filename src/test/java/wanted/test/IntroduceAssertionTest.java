package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.IntroduceAssertion;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Introduce Assertion'
 *
 * @author seungjae yoo
 */
public class IntroduceAssertionTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return new IntroduceAssertion();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/introduceAssertion";
    }

    //
    public void testIntroduceAssertion1() throws Exception {
        doTest();
    }
}
