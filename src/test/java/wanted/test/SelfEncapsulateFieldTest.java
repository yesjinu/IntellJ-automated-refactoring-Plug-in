package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.SelfEncapField;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Self Encapsulate Field'
 *
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
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 5: when caret doesn't point member */
    public void testSelfEncapField5() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 6: do not refactor when caret points to non-private element */
    public void testSelfEncapField6() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 7: Self Encapsulation for user-defined class */
    public void testSelfEncapField7() throws Exception {
        doTest();
    }

    /* Test 8: Self Encapsulation for private field of inner class */
    public void testSelfEncapField8() throws Exception {
        doTest();
    }

    /* Test 9: checkDuplicateName shouldn't consider method of other class */
    public void testSelfEncapField9() throws Exception {
        doTest();
    }
}
