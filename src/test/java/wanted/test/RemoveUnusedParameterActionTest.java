package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.RemoveUnusedParameterAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Remove Unused Parameter Action'
 *
 * @author Jinu Noh
 */
public class RemoveUnusedParameterActionTest extends LightActionTestCase {

    public RemoveUnusedParameterActionTest() {}

    protected AnAction getAction() {
        return new RemoveUnusedParameterAction();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/removeUnusedParameter";
    }

    // Case 1 : parameter 3개, used reference 2개
    public void testRemoveUnusedParameterAction1() throws Exception {
        doTest();
    }

    // Case 2 : parameter 2개, used reference 2개
    public void testRemoveUnusedParameterAction2() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Case 3 : parameter 0개, used reference 2개
    public void testRemoveUnusedParameterAction3() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Case 4 : parameter 2개, used reference 0개
    public void testRemoveUnusedParameterAction4() throws Exception {
        doTest();
    }

    // Case 5 : parameter 0개, used reference 0개
    public void testRemoveUnusedParameterAction5() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
