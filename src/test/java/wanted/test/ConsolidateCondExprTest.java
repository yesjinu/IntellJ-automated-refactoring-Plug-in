package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ConsolidateCondExpr;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Consolidate Conditional Expression'
 *
 * @author seungjae yoo
 */
public class ConsolidateCondExprTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return new ConsolidateCondExpr();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/consolidateCondExpr";
    }

    // test when we should merge condition
    public void testConsolidateCondExpr1() throws Exception {
        doTest();
    }

    // test when we should remove conditional expression
    public void testConsolidateCondExpr2() throws Exception {
        doTest();
    }

    // test when we should remove conditional expression inside
    public void testConsolidateCondExpr3() throws Exception {
        doTest();
    }

    // test when we cannot refactor this ifStatement
    public void testConsolidateCondExpr4() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // test when cursor is not in the ifStatement
    public void testConsolidateCondExpr5() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    // test when cursor is out of Class
    public void testConsolidateCondExpr6() throws Exception {
        try {doTest();}
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
