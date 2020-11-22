package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ReplaceMagicNumber;

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
}
