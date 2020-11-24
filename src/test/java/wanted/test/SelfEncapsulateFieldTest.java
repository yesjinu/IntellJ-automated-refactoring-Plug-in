package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.SelfEncapField;

/**
 * Test class for self encapsulate field
 * @author seha Park
 */
public class SelfEncapsulateFieldTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new SelfEncapField();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/selfEncapField";
    }

    /* Test 1: simple case */
    public void testSelfEncapField1() throws Exception {
        doTest();
    }

    /* Test 2: complicate case */
    public void testSelfEncapField2() throws Exception {
        doTest();
    }

    /* Test 3: variable shadowing */
    public void testSelfEncapField3() throws Exception {
        doTest();
    }

    /* Test 4: do not refactor when setter or getter already exist */
    public void testSelfEncapField4() throws Exception {
        try
        {
            doTest(); // This cause error refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }

    /* Test 5: test using caret */
    //public void testSelfEncapField5() throws Exception {
    //    doTest();
    //}
}
