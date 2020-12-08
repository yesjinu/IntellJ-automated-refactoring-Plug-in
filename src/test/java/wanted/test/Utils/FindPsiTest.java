
package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.ide.navigationToolbar.NavBarActions;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.test.base.LightActionTestCase;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for 'FindPsi' util
 *
 * @author Jinu Noh
 */
public class FindPsiTest extends AbstractLightCodeInsightTestCase {
    public void testFindMemberReference1() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Project project = navigator.findProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiClass targetClass = navigator.findClass();
        PsiField targetField = navigator.findField();

        PsiReferenceExpression expected = (PsiReferenceExpression) factory.createExpressionFromText("dummyValue", null);

        assertEquals(FindPsi.findMemberReference(targetClass, targetField).get(0).getText(), expected.getText());
    }

    public void testFindMemberReference2() {

    }

    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileName file to test
     * @return AnActionEvent with given file context
     */
    public AnActionEvent createAnActionEvent(String fileName) throws TimeoutException, ExecutionException {
        if(fileName!=null) {
            myFixture.configureByFile("testData/findPsi/" + fileName);
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
