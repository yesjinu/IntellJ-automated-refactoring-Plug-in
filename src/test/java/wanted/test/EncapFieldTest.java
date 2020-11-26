package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.EncapField;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Encapsulate Field'
 *
 * @author seha Park
 */
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
        String[] beforeFiles = {"EF_owner.java", "EF_other.java"};
        doTestDirectory(beforeFiles);
    }

    /* Test 2: complicate case */
    public void testEncapField2() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_user1.java", "EF_user2.java", "EF_user3.java"};
        doTestDirectory(beforeFiles);
    }
}
