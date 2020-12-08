package wanted.test;

import com.intellij.openapi.actionSystem.AnAction;
import wanted.refactoring.InlineMethod;
import wanted.test.base.LightActionTestCase;

/**
 * Test class for 'Inline Method'
 *
 * @author seha Park
 */
public class InlineMethodStrengthenTest extends LightActionTestCase {

    protected AnAction getAction() {
        return new InlineMethod();
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "/InlineMethodStrengthen";
    }

    /* [Test 1] Refactor: Basic Case */
    public void testInlineMethodStrengthen1() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 1);
    }

    /* [Test 2] RefactorValid: Subclass Test */
    public void testInlineMethodStrengthen2() throws Exception {
        String[] files = {"PizzaDelivery.java", "HawaiianPizzaDelivery.java"};
        try {
            doTestFoldersMulti(files, 2);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    /* [Test 3] */
    public void testInlineMethodStrengthen3() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 3);
    }

    public void testInlineMethodStrengthen4() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 4);
    }

    public void testInlineMethodStrengthen5() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 5);
    }

    public void testInlineMethodStrengthen6() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 6);
    }

    public void testInlineMethodStrengthen7() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 7);
    }

    public void testInlineMethodStrengthen8() throws Exception {
        String[] files = {"PizzaDelivery.java"};
        doTestFoldersMulti(files, 8);
    }

    public void testInlineMethodStrengthen9() throws Exception {
        String[] files = {"Test9.java"};
        try {
            doTestFoldersMulti(files, 9);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }

    /* [Test 10] RefactorValid: Constructor Test */
    public void testInlineMethodStrengthen10() throws Exception {
        String[] files = {"EF_owner.java"};
        try {
            doTestFoldersMulti(files, 10);
        }
        catch(RuntimeException e){
            assertEquals(e.getMessage(), "Nothing to do");
        }
    }
}
