package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.test.base.LightActionTestCase;
import wanted.utils.NavigatePsi;
import wanted.utils.TraverseProjectPsi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for 'FindPsi' util
 *
 * @author Jinu Noh
 */
public class TraverseProjectPsiTest extends AbstractLightCodeInsightTestCase {

    /* test TraversePsi::getRootPackages */
    public void testGetRootPackages() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        Project project = e.getProject();
        String expected = "[PsiPackage:testData]";
        assertEquals(TraverseProjectPsi.getRootPackages(project).size(), 1);
        assertEquals(TraverseProjectPsi.getRootPackages(project).toString(), expected);
    }

    /* test TraversePsi::findFile */
    public void testFindFile() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file2.java");
        Project project = e.getProject();
        String expected = "[PsiJavaFile:file2.java]";
        assertEquals(TraverseProjectPsi.findFile(project).size(), 1);
        assertEquals(TraverseProjectPsi.findFile(project).toString(), expected);
    }


    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileName file to test
     * @return AnActionEvent with given file context
     */
    public AnActionEvent createAnActionEvent(String fileName) throws TimeoutException, ExecutionException {
        if(fileName!=null) {
            myFixture.configureByFile("testData/traversePsi/" + fileName);
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