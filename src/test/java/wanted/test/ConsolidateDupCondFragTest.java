package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ConsolidateDupCondFrag;

/**
 * Test class for consolidate duplicate conditional fragments
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


    public void testConsolidateDupCondFrag1() throws Exception {
        doTest();
    }
}
