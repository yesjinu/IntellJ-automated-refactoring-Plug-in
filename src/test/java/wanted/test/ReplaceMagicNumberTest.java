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

    /* Test 3: refactor only if it is worth it */
    public void testReplaceMagicNumber3() throws Exception {
        doTest();
    }

    /* Test 4: Replace string */
    public void testReplaceMagicNumber4() throws Exception {
        doTest();
    }

    /* Test 5: caret points to invalid area */
    public void testReplaceMagicNumber5() throws Exception {
        doTest();
    }

    /* Test 6: name constant properly */
    public void testReplaceMagicNumber6() throws Exception {
        doTest();
    }

    /* Test 7: replace with pre-defined constant */
    public void testReplaceMagicNumber7() throws Exception {
        doTest();
    }
}
