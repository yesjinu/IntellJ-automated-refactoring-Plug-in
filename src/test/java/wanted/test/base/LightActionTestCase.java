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
     * before file: testData/basePath/test<num>/input.java
     * after file: testData/basePath/test<num>/output.java
     *
     * @param test_num the number of test cases
     * @throws Exception
     */
    protected void doTestFoldersSingle(int test_num) throws Exception {
        myFixture.configureByFile(getBasePath() + "/test" + String.valueOf(test_num) + "/input.java");
        performActionTest();
        checkResultByFile(getBasePath() + "/test" + String.valueOf(test_num) + "/output.java");
    }

    /**
     * Do Test and check the result by files.
     * MAKE SURE that file which contains caret must be the first element of files array.
     *
     * before files: testData/basePath/test<num>/before/<filename>
     * after files: testData/basePath/test<num>/after/<filename>
     *
     * @param test_num the number of test cases
     * @throws Exception
     */
    protected void doTestFoldersMulti(String[] files, int test_num) throws Exception {
        String beforePath = getBasePath() + "/test" + Integer.toString(test_num) + "/before/";
        String afterPath = getBasePath() + "/test" + Integer.toString(test_num) + "/after/";

        String[] inputFiles = new String[files.length]; // add path
        for(int i = 0; i < files.length; i++)
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
