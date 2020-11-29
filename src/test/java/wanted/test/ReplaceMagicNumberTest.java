package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ReplaceMagicNumber;
import wanted.test.base.LightActionTestCase;

public class ReplaceMagicNumberTest extends LightActionTestCase  {

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

    /* Test 3: do not refactor when value is 0 */
    public void testReplaceMagicNumber3() throws Exception {
        try
        {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }

    /* Test 4: Replace string */
    public void testReplaceMagicNumber4() throws Exception {
        doTest();
    }

    /* Test 5: caret points to invalid element */
    public void testReplaceMagicNumber5() throws Exception {
        try
        {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }

    /* Test 6: add constant proper name */
    public void testReplaceMagicNumber6() throws Exception {
        doTest();
    }

    /* Test 7: replace with pre-defined constant */
    public void testReplaceMagicNumber7() throws Exception {
        doTest();
    }

    /* Test 8: do not refactor when value is 2 */
    public void testReplaceMagicNumber8() throws Exception {
        try
        {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }

    /* Test 9: do not refactor when string is empty or white space */
    public void testReplaceMagicNumber9() throws Exception {
        try
        {
            doTest(); // This cause error since refactorValid() returns false and Message dialog appears
        }catch(RuntimeException e) {
            // nothing
        }
    }
}
