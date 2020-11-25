package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.HideMethodAction;
import wanted.refactoring.IntroduceForeignMethodAction;


public class IntroduceForeignMethodActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new IntroduceForeignMethodAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/introduceForeignMethod";
    }

    public void testIntroduceForeignMethodAction() throws Exception {
        doTest();
    }
}
