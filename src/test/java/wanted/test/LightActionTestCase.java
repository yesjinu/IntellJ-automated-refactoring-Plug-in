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

    // TODO
    protected void doDirTest() throws Exception {
        //String beforePath = getBasePath() + "/before" + getTestName(false) + "/";
        String beforePath = "java/wanted/test"+ "/before" + getTestName(false) + "/";
        String afterPath = getBasePath() + "/after" + getTestName(false) + "/";

        String[] beforeFiles = {beforePath + "EF_owner.java", beforePath + "EF_other.java"};

        myFixture.configureByFiles(beforeFiles);
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
