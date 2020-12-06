package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.junit.jupiter.api.Assertions;
import wanted.refactoring.SelfEncapField;
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
    public void testFindProject() throws TimeoutException, ExecutionException
    {
        AnActionEvent e = createAnActionEvent("test1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertNotNull(navigator.findProject());
    }

    public void testFindFile() throws TimeoutException, ExecutionException
    {
        AnActionEvent e = createAnActionEvent("test1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findFile().getName(), "test1.java");
    }

    public void testFindClass() throws TimeoutException, ExecutionException
    {
        AnActionEvent e = createAnActionEvent("test1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Assertions.assertEquals(navigator.findClass().getName(), "dummy");
    }

    /**
     * Method to create AnActionEvent with given file
     *
     * @param fileName file to test
     * @return created AnActionEvent
     */
    public AnActionEvent createAnActionEvent (String fileName) throws TimeoutException, ExecutionException
    {
        myFixture.configureByFile("testData/navigatePsi/"+fileName);

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
