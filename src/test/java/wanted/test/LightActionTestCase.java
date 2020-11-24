package wanted.test;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileDocumentManager;

import org.jetbrains.concurrency.Promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class LightActionTestCase extends AbstractLightCodeInsightTestCase {
    protected void doTest() throws Exception {
        myFixture.configureByFile(getBasePath() + "/before" + getTestName(false) + ".java");
        performActionTest();
        checkResultByFile(getBasePath() + "/after" + getTestName(false) + ".java");
    }

    /**
     * Test function for multiple files
     * @param files names of files in BasePath()+before<testName>/
     * @throws Exception
     * caution: there should be no package statement for each file
     */
    protected void doTestDirectory(String[] files) throws Exception {
        String beforePath = getBasePath() + "/before" + getTestName(false) + "/";
        String afterPath = getBasePath() + "/after" + getTestName(false) + "/";

        String[] inputFiles = new String[files.length]; // add path
        for(int i=0; i<files.length; i++)
        {
            inputFiles[i] = beforePath + files[i];
        }

        myFixture.configureByFiles(inputFiles);

        performActionTest();
        checkResultByFiles(afterPath, beforePath);
    }

    private void performActionTest() throws TimeoutException, ExecutionException {
        AnAction anAction = getAction();

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        anAction.actionPerformed(anActionEvent);
        FileDocumentManager.getInstance().saveAllDocuments();
    }

    protected abstract AnAction getAction();
}
