package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ParameterizeWholeObjectAction;

public class ParameterizeWholeObjectActionTest extends LightActionTestCase {

    public ParameterizeWholeObjectActionTest() {};

    protected AnAction getAction() {
        return new ParameterizeWholeObjectAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/parameterizeWholeObject";
    }

    public void testParameterizeWholeObjectAction() throws Exception {
        doTest();
    }
}
