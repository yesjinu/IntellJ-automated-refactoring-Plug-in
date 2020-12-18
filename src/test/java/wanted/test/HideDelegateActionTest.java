package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.HideDelegateAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Hide Delegate Action'
 *
 * @author Chanyoung Kim
 */
public class HideDelegateActionTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new HideDelegateAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/hideDelegate";
    }


    public void testHideDelegate1() throws Exception {
        String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
        doTestFoldersMulti(beforeFiles, 1);
    }

    public void testHideDelegate2() throws Exception {
        String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
        doTestFoldersMulti(beforeFiles, 2);
    }

    public void testHideDelegate3() throws Exception {
        String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
        doTestFoldersMulti(beforeFiles, 3);
    }

    public void testHideDelegate4() throws Exception {
        try {
            String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
            doTestFoldersMulti(beforeFiles, 4);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    public void testHideDelegate5() throws Exception {
        try {
            String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
            doTestFoldersMulti(beforeFiles, 5);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    public void testHideDelegate6() throws Exception {
        try {
            String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
            doTestFoldersMulti(beforeFiles, 6);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
