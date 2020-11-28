package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ConsolidateDupCondFrag;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Consolidate Duplicate Conditional Fragments'
 *
 * @author seungjae yoo
 */
public class ConsolidateDupCondFragTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return new ConsolidateDupCondFrag();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/consolidateDupCondFrag";
    }

    // Test when cursor is in ifStatement & first statement is duplicated
    public void testConsolidateDupCondFrag1() throws Exception {
        doTest();
    }

    // Test when cursor is in elseifStatement & last statement is duplicated
    public void testConsolidateDupCondFrag2() throws Exception {
        doTest();
    }

    // Test when multiple statement from first are duplicated, including function calling statement
    public void testConsolidateDupCondFrag3() throws Exception {
        doTest();
    }

    // Test when first and last statement are duplicated, including function calling statement and loop statement
    public void testConsolidateDupCondFrag4() throws Exception {
        doTest();
    }

    // Test when multiple statement from last are duplicated, we should eliminate some conditions
    public void testConsolidateDupCondFrag5() throws Exception {
        doTest();
    }

    // Test when multiple statements are duplicated, and we have no statement at first condition
    public void testConsolidateDupCondFrag6() throws Exception {
        doTest();
    }

    // Test when all statements are duplicated
    public void testConsolidateDupCondFrag7() throws Exception {
        doTest();
    }

    // test when no statements from first or last are duplicated
    public void testConsolidateDupCondFrag8() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // test when no statements from first or last are duplicated
    public void testConsolidateDupCondFrag9() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // test when cursor isn't in ifstatement
    public void testConsolidateDupCondFragA() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

}
