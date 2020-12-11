package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.InlineMethod;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Inline Method'
 *
 * @author seha Park
 * @author Mintae Kim
 */
public class InlineMethodTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new InlineMethod();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/InlineMethod";
    }

    /* [Test 2] RefactorValid: Subclass Test */
    public void testInlineMethod2() throws Exception {
        String[] files = {"PizzaDelivery.java", "HawaiianPizzaDelivery.java"};
        try {
            doTestFoldersMulti(files, 2);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    /* [Test 9] RefactorValid: Constructor Test */
    public void testInlineMethod9() throws Exception {
        String[] files = {"EF_owner.java"};
        try {
            doTestFoldersMulti(files, 9);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    /* [Test 16] RefactorValid: Loop: More than Inner 2 Statements */
    public void testInlineMethod16() throws Exception {
        String[] files = {"A.java"};
        try {
            doTestFoldersMulti(files, 16);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    /* [Test 17] RefactorValid: Conditional Loop: More than Inner 2 Statements */
    public void testInlineMethod17() throws Exception {
        String[] files = {"A.java"};
        try {
            doTestFoldersMulti(files, 17);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }



    /* [Test 1] Refactor: Basic Case */
    public void testInlineMethod1() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 1);
    }

    /* [Test 3] Refactor: Boolean */
    public void testInlineMethod3() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 3);
    }

    /* [Test 4] Refactor: Boolean w/ 3 parameters */
    public void testInlineMethod4() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 4);
    }

    /* [Test 5] Refactor: Void w/ 1 parameter */
    public void testInlineMethod5() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 5);
    }

    /* [Test 6] Refactor: Void + If Statement */
    public void testInlineMethod6() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 6);
    }

    /* [Test 7] Refactor: Void + For Statement & Function Call  */
    public void testInlineMethod7() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 7);
    }

    /* [Test 8] Refactor: Void + For Statement & Assignment Statement */
    public void testInlineMethod8() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 8);
    }

    /* [Test 18] Refactor: Void + Conditional Loop & Assignment Statement */
    public void testInlineMethod18() throws Exception {
        String[] files = {"A.java"};
        doTestFoldersMulti(files, 18);
    }

    /* [Test 10] Refactor: Void + Qualifier */
    public void testInlineMethod10() throws Exception {
        String[] files = {"A.java", "B.java", "C.java"};
        doTestFoldersMulti(files, 10);
    }

    /* [Test 11] Refactor: Void + Qualifier Array */
    public void testInlineMethod11() throws Exception {
        String[] files = {"A.java", "B.java", "C.java"};
        doTestFoldersMulti(files, 11);
    }

    /* [Test 12] Refactor: Void + Qualifier Class */
    public void testInlineMethod12() throws Exception {
        String[] files = {"A.java", "B.java", "C.java"};
        doTestFoldersMulti(files, 12);
    }

    /* [Test 13] Refactor: Non Used Method */
    public void testInlineMethod13() throws Exception {
        String[] files = {"A.java", "B.java", "C.java"};
        doTestFoldersMulti(files, 13);
    }

    /* [Test 14] Refactor: Void Qualifier Method */
    public void testInlineMethod14() throws Exception {
        String[] files = {"B.java", "A.java", "C.java"};
        doTestFoldersMulti(files, 14);
    }

    /* [Test 15] Refactor: Void + Class Member */
    public void testInlineMethod15() throws Exception {
        String[] files = {"B.java", "A.java", "C.java"};
        doTestFoldersMulti(files, 15);
    }

    /* [Test 19] Refactor: 2 References */
    public void testInlineMethod19() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 19);
    }
}
