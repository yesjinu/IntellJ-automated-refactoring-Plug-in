package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.SelfEncapField;

public class SelfEncapsulateFieldTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new SelfEncapField();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/selfEncapField";
    }

    /* simple case */
    public void testSelfEncapField1() throws Exception {
        doTest();
    }

    /* complicate case */
    public void testSelfEncapField2() throws Exception {
        doTest();
    }

    /* variable shadowing */
    public void testSelfEncapField3() throws Exception {
        doTest();
    }

    /* do not refactor when setter or getter already exist */
    public void testSelfEncapField4() throws Exception {
        try
        {
            doTest(); // This cause error refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }
}
