package wanted.test.base;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileDocumentManager;

import org.jetbrains.concurrency.Promise;
import wanted.refactoring.BaseRefactorAction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class to implement light test through files before and after refactoring.
 *
 * @author Chanyoung Kim
 * @author seha Park
 * @author Mintae Kim
 */
public abstract class LightActionTestCase extends AbstractLightCodeInsightTestCase {
    /**
     * Do Test and check the result by file
     *
     * before file: testData/basePath/before<TestName>.java
     * after file: testData/basePath/after<TestName>.java
     *
     * @throws Exception
     */
    protected void doTest() throws Exception {
        myFixture.configureByFile(getBasePath() + "/before" + getTestName(false) + ".java");
        performActionTest();
        checkResultByFile(getBasePath() + "/after" + getTestName(false) + ".java");
    }

    /**
     * Do Test and check the result by files
     *
     * before files: testData/basePath/test<num>/input.java
     * after files: testData/basePath/test<num>/output.java
     *
     * @param test_num the number of test cases
     * @throws Exception
     */
    protected void doTest_io(int test_num) throws Exception {
        myFixture.configureByFile(getBasePath() + "/test" + String.valueOf(test_num) + "/input.java");
        performActionTest();
        checkResultByFile(getBasePath() + "/test" + String.valueOf(test_num) + "/output.java");
    }

    /**
     * Test function for multiple files
     * @param files names of files in BasePath()+before<testName>/
     * @throws Exception
     * caution: there should be no package statement for each file
     *          first element of files[] will be opened with editor.
     *          i.e, the target file(file to inspect, file with caret ... ) must be the first element of files[]
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

    /**
     * Perform action test with action that is acquired by getAction()
     *
     * @throws TimeoutException, ExecutionException
     */
    private void performActionTest() throws TimeoutException, ExecutionException {
        AnAction anAction = getAction();

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        if (anAction instanceof BaseRefactorAction) ((BaseRefactorAction) anAction).refactorRequest(anActionEvent);
        else anAction.actionPerformed(anActionEvent);
        FileDocumentManager.getInstance().saveAllDocuments();
    }

    protected abstract AnAction getAction();
}
