package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.EncapField;
import wanted.refactoring.HideDelegateAction;
import wanted.refactoring.IntroduceLocalExtensionAction;
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
        doTestDirectory(beforeFiles);
    }

    public void testHideDelegate2() throws Exception {
        String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
        doTestDirectory(beforeFiles);
    }

    public void testHideDelegate3() throws Exception {
        String[] beforeFiles = {"HD_test.java", "HD_person.java", "HD_department.java"};
        doTestDirectory(beforeFiles);
    }
}
