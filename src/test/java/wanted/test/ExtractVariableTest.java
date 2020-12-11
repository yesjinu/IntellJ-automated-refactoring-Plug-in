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
        doTestFoldersSingle(1);
    }

    public void testExtractVariable2() throws Exception {
        doTestFoldersSingle(2);
    }

    public void testExtractVariable3() throws Exception {
        doTestFoldersSingle(3);
    }

    public void testExtractVariable4() throws Exception {
        doTestFoldersSingle(4);
    }

    public void testExtractVariable5() throws Exception {
        doTestFoldersSingle(5);
    }

    public void testExtractVariable6() throws Exception {
        doTestFoldersSingle(6);
    }
}
