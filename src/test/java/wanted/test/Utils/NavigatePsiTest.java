package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.concurrency.Promise;
import org.junit.jupiter.api.Assertions;
import wanted.refactoring.SelfEncapField;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.NavigatePsi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author seha Park
 */
public class NavigatePsiTest extends AbstractLightCodeInsightTestCase {
    public void testNavigatorFactory () throws TimeoutException, ExecutionException
    {
        AnAction anAction = new PsiUtilsTest();

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        NavigatePsi navigator = NavigatePsi.NavigatorFactory(anActionEvent);
        Assertions.assertNotNull(navigator);
        System.out.println(navigator.findProject());
    }
}
