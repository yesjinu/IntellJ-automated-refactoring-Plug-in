package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.NavigatePsi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for navigate psi
 *
 * @author seha Park
 */
public class NavigatePsiTest extends AbstractLightCodeInsightTestCase {

    public void testNavigatorFactory() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNotNull(navigator);
    }

    public void testFindProject() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNotNull(navigator.findProject());
    }

    /* findFile test 1: file exists */
    public void testFindFile1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findFile().getName(), "file1.java");
    }

    /* findFile test 2 : file doesn't exist */
    public void testFindFile2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent(null);
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findFile());
    }

    /* findClass test 1 - class exists */
    public void testFindClass1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findClass().getName(), "dummy");
    }

    /* findClass test 2 - class doesn't exist */
    public void testFindClass2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file2.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findClass());
    }

    /* findClass test 3 - get outer class */
    public void testFindClass3() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file3.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findClass().getName(), "dummy2");
    }

    /* findClass test 4 - file that can't be cast to class owner */
    public void testFindClass4() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file7.xml");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findClass());
    }

    /* findMethod test 1 - select method */
    public void testFindMethod1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file4.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findMethod().getName(), "method1");
    }

    /* findMethod test 2 - doesn't select method */
    public void testFindMethod2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file5.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findMethod());
    }

    /* findField test 1 - select field */
    public void testFindField1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file5.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findField().getName(), "target");
    }

    /* findField test 2 - doesn't select field */
    public void testFindField2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file4.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findField());
    }

    /* findField test 1 - select literal */
    public void testFindLiteral1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file6.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        PsiLiteralExpression literal = navigator.findLiteral();
        Assertions.assertEquals(literal.getText(), "456789");
    }

    /* findLiteral test 2 - doesn't select literal */
    public void testFindLiteral2() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file5.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNull(navigator.findLiteral());
    }

    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileName file to test
     * @return AnActionEvent with given file context
     */
    public AnActionEvent createAnActionEvent(String fileName) throws TimeoutException, ExecutionException {
        if(fileName!=null) {
            myFixture.configureByFile("testData/navigatePsi/" + fileName);
        }

        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // do nothing
            }
        };

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        return anActionEvent;
    }
}
