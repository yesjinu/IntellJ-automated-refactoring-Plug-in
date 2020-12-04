package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.IntroduceForeignMethodAction;
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
        doTest();
    }
}
