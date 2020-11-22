package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.EncapField;

public class EncapFieldTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new EncapField();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/encapField";
    }

    /* Test 1: simple case */
    public void testEncapField1() throws Exception {
        doDirTest();
    }
}
