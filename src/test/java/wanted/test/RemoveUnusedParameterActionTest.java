package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.RemoveUnusedParameterAction;
import wanted.test.base.LightActionTestCase;

public class RemoveUnusedParameterActionTest extends LightActionTestCase {

    public RemoveUnusedParameterActionTest() {};

    protected AnAction getAction() {
        return new RemoveUnusedParameterAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/removeUnusedParameter";
    }

    public void testRemoveUnusedParameterAction() throws Exception {
        doTest();
    }
}
