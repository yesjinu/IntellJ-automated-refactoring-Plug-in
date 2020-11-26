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

}
