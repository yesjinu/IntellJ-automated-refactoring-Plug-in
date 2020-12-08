package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ExtractVariable;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Inline Method'
 *
 * @author seha Park
 */
public class ExtractVariableTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new ExtractVariable();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/ExtractVariable";
    }

    public void testExtractVariable1() throws Exception {
        doTest_io(1);
    }

    public void testExtractVariable2() throws Exception {
        doTest_io(2);
    }

    public void testExtractVariable3() throws Exception {
        doTest_io(3);
    }

    public void testExtractVariable4() throws Exception {
        doTest_io(4);
    }

    public void testExtractVariable5() throws Exception {
        doTest_io(5);
    }

    public void testExtractVariable6() throws Exception {
        doTest_io(6);
    }
}
