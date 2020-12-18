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
        doTestFoldersMulti(beforeFiles, 1);
    }

    /* Test 2: complicate case - complicate usage & multiple files */
    public void testEncapField2() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_user1.java", "EF_user2.java"};
        doTestFoldersMulti(beforeFiles, 2);
    }

    /* Test 3: Apply encapsulate for inherited class */
    public void testEncapField3() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_user1.java"};
        doTestFoldersMulti(beforeFiles, 3);
    }

    /* Test 4: Do not refactor private field */
    public void testEncapField4() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_other.java"};
        try {
            doTestFoldersMulti(beforeFiles, 4); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 5: Do not refactor when caret points to wrong location */
    public void testEncapField5() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_other.java"};
        try {
            doTestFoldersMulti(beforeFiles, 5); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 6: Do not refactor when either setter or getter already exist */
    public void testEncapField6() throws Exception {
        String[] beforeFiles = {"EF_owner.java", "EF_other.java"};
        try {
            doTestFoldersMulti(beforeFiles, 6); // This cause error since refactorValid() returns false and Message dialog appears
        } catch (RuntimeException e) {
            // nothing
        }
    }

    /* Test 7: when there's no other files in directory */
    public void testEncapField7() throws Exception {
        String[] beforeFiles = {"EF_owner.java"};
        doTestFoldersMulti(beforeFiles, 7);
    }
}
