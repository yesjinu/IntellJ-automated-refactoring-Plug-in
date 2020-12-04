package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'AddPsi' util
 *
 * @author Jinu Noh
 */
public class AddPsiTest extends LightActionTestCase {
    @Override
    protected AnAction getAction() {
        return null;
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/addPsi";
    }

}
