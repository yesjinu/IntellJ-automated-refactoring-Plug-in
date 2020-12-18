package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ReplaceMagicNumber;
import wanted.test.base.LightActionTestCase;

public class ReplaceMagicNumberTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new ReplaceMagicNumber();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/replaceMagicNumber";
    }

    /* Test 1: simple case */
    public void testReplaceMagicNumber1() throws Exception {
        doTest();
    }

    /* Test 2: advance case */
    public void testReplaceMagicNumber2() throws Exception {
        doTest();
    }

    /* Test 3: Replace string */
    public void testReplaceMagicNumber3() throws Exception {
        doTest();
    }

    /* Test 4: add constant proper name */
    public void testReplaceMagicNumber4() throws Exception {
        doTest();
    }

    /* Test 5: replace with pre-defined constant */
    public void testReplaceMagicNumber5() throws Exception {
        doTest();
    }

    /* Test 6: caret points to invalid element */
    public void testReplaceMagicNumber6() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 7: do not refactor when value is 0 */
    public void testReplaceMagicNumber7() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 8: do not refactor when value is 2 */
    public void testReplaceMagicNumber8() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 9: do not refactor when string is white space */
    public void testReplaceMagicNumber9() throws Exception {
        try {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 10: replace with pre-defined constant(string case) */
    public void testReplaceMagicNumber10() throws Exception {
        doTest();
    }

    /* Test 11: refactor when char is not blank */
    public void testReplaceMagicNumber11() throws Exception {
        doTest();
    }

    /* Test 12: short, byte, long, char literal expressions which are representable by int are treated as int" */
    public void testReplaceMagicNumber12() throws Exception {
        doTest();
    }

    /* Test 13: when selected literal expression is used on field -> need to add constant before field declaration */
    public void testReplaceMagicNumber13() throws Exception {
        doTest();
    }

    /* Test 14: replace with pre-defined constant(general case) */
    public void testReplaceMagicNumber14() throws Exception {
        doTest();
    }
}
