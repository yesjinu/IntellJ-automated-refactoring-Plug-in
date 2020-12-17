package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ParameterizeWholeObjectAction;
import wanted.test.base.LightActionTestCase;

public class ParameterizeWholeObjectActionTest extends LightActionTestCase {

    public ParameterizeWholeObjectActionTest() {}

    protected AnAction getAction() {
        return new ParameterizeWholeObjectAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/parameterizeWholeObject";
    }

    // case 1: One method call and there are only getter methods for parameters
    public void testParameterizeWholeObjectAction() throws Exception {
        doTest();
    }

    // case 2: One method call and there are getter methods not for parameters
    public void testParameterizeWholeObjectAction2() throws Exception {
        doTest();
    }

    // case 3 : two or more method calls, only one needs refactor. NOT sharing parameters
    public void testParameterizeWholeObjectAction3() throws Exception {
        doTest();
    }

    // case 4 : two or more method calls, more than two need refactor. NOT sharing parameters
    public void testParameterizeWholeObjectAction4() throws Exception {
        doTest();
    }

    // case 5 : two or more method calls share same parameters
    public void testParameterizeWholeObjectAction5() throws Exception {
        doTest();
    }
}
