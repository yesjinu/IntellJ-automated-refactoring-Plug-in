package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.InlineMethodAction;

public class InlineMethodActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new InlineMethodAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/InlineMethod";
    }

    public void testInlineMethodAction() throws Exception {
        doTest();
    }
}
