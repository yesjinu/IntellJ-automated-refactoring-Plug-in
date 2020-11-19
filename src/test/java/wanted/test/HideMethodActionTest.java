package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.HideMethodAction;


public class HideMethodActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new HideMethodAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/hideMethod";
    }

    public void testHideMethodAction() throws Exception {
        doTest();
    }
}
