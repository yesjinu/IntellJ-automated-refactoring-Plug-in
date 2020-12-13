package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.InlineMethodStrengthen;

/**
 * Test class for 'Inline Method Strengthen'
 *
 * @author seha Park
 * @author Mintae Kim
 */
public class InlineMethodStrengthenTest extends InlineMethodTest {

    @Override
    protected AnAction getAction() {
        return new InlineMethodStrengthen();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "Strengthen";
    }

    /** Test Inherited from InlineMethodTest */
    /* [Test 2] RefactorValid: Subclass Test */
    /* [Test 9] RefactorValid: Constructor Test */
    /* [Test 16] RefactorValid: Loop: More than Inner 2 Statements */
    /* [Test 17] RefactorValid: Conditional Loop: More than Inner 2 Statements */


    /* [Test 1] Refactor: Basic Case */
    /* [Test 3] Refactor: Boolean */
    /* [Test 4] Refactor: Boolean w/ 3 parameters */
    /* [Test 5] Refactor: Void w/ 1 parameter */
    /* [Test 6] Refactor: Void + If Statement */
    /* [Test 7] Refactor: Void + For Statement & Function Call  */
    /* [Test 8] Refactor: Void + For Statement & Assignment Statement */
    /* [Test 18] Refactor: Void + Conditional Loop & Assignment Statement */
    /* [Test 10] Refactor: Void + Qualifier */
    /* [Test 11] Refactor: Void + Qualifier Array */
    /* [Test 12] Refactor: Void + Qualifier Class */
    /* [Test 13] Refactor: Non Used Method */
    /* [Test 14] Refactor: Void Qualifier Method */
    /* [Test 15] Refactor: Void + Class Member */
    /* [Test 19] Refactor: 2 References */

    /* [Test 20] Refactor: 2 Conditional For Loop with Local Variable */
    public void testInlineMethodStrengthen20() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 20);
    }
}
