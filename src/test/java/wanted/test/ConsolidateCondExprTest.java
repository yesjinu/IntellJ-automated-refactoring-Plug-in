package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.ConsolidateCondExpr;

public class ConsolidateCondExprTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return new ConsolidateCondExpr();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/consolidateCondExpr";
    }

    public void testConsolidateCondExpr1() throws Exception {
        doTest();
    }
}
