package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.IntroduceAssertion;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Introduce Assertion'
 *
 * @author seungjae yoo
 */
public class IntroduceAssertionTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return new IntroduceAssertion();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/introduceAssertion";
    }

    // Test when we should null check for a reference in then branch
    public void testIntroduceAssertion1() throws Exception {
        doTest();
    }

    // Test when we should null check for multiple references in else branch
    public void testIntroduceAssertion2() throws Exception {
        doTest();
    }

    // Test when we should null check for multiple references in then branch and one reference in else branch
    public void testIntroduceAssertion3() throws Exception {
        doTest();
    }

    // Test when if statement is consist of if-elseif
    public void testIntroduceAssertion4() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Test when if statement is consist of if-elseif-else
    public void testIntroduceAssertion5() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Test when cursor is out of if statement
    public void testIntroduceAssertion6() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Test when we don't have to do null check in if statement
    public void testIntroduceAssertion7() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // Test when else statement doesn't exist
    public void testIntroduceAssertion8() throws Exception {
        doTest();
    }
}
