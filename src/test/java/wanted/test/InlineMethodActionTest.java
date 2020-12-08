package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.InlineMethod;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Inline Method'
 *
 * @author seha Park
 */
public class InlineMethodActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new InlineMethod();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/InlineMethod";
    }

    public void testInlineMethodAction1() throws Exception {
        doTest_io(1);
    }

    public void testInlineMethodAction2() throws Exception {
        try {
            doTest_io(2);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    public void testInlineMethodAction3() throws Exception {
        doTest_io(3);
    }

    public void testInlineMethodAction4() throws Exception {
        doTest_io(4);
    }

    public void testInlineMethodAction5() throws Exception {
        doTest_io(5);
    }

    public void testInlineMethodAction6() throws Exception {
        doTest_io(6);
    }

    public void testInlineMethodAction7() throws Exception {
        doTest_io(7);
    }

    public void testInlineMethodAction8() throws Exception {
        doTest_io(8);
    }

    public void testInlineMethodAction9() throws Exception {
        try {
            doTest_io(9);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
