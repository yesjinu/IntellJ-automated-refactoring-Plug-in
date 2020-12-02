package wanted.ui;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import wanted.refactoring.BaseRefactorAction;
import wanted.utils.NavigatePsi;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RefactorPreviewWindow extends DialogWrapper {

    private JPanel dialogPanel;

    private Editor editorBefore;
    private JComponent editorCompBefore;

    private Editor editorAfter;
    private JComponent editorCompAfter;

    private BaseRefactorAction refactorAction;
    private AnActionEvent e;

    public RefactorPreviewWindow(BaseRefactorAction refactorAction, AnActionEvent e) {
        super(true);

        this.refactorAction = refactorAction;
        this.e = e;

        /*
        Project project = e.getData(CommonDataKeys.PROJECT);

        EditorFactory ef = EditorFactory.getInstance();

        Document docBefore = ef.createDocument(e.getData(CommonDataKeys.PSI_FILE).getText());
        editorBefore = ef.createEditor(docBefore);

        Document docAfter = ef.createDocument(e.getData(CommonDataKeys.PSI_FILE).getText());
        editorAfter = ef.createEditor(docAfter);
        */

        init();
        setTitle("Refactoring Preview: " + refactorAction.storyName());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        dialogPanel = new JPanel(new BorderLayout());

        fetchDiffUI(dialogPanel);

        /*
        editorCompBefore = editorBefore.getComponent();
        editorCompAfter = editorAfter.getComponent();

        dialogPanel.add(editorCompBefore, BorderLayout.WEST);
        dialogPanel.add(editorCompAfter, BorderLayout.EAST);
        */

        return dialogPanel;
    }

    private void fetchDiffUI (JComponent dialogPanel) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        Project project = navigator.findProject();

        PsiFile file = navigator.findFile();
        DiffContent contentBefore = DiffContentFactory.getInstance().create(project, file.getText());

        refactorAction.refactor(e);

        DiffContent contentAfter = DiffContentFactory.getInstance().create(project, file.getText());

        SimpleDiffRequest request = new SimpleDiffRequest("Before - After", contentBefore, contentAfter, "Before", "After");

        SimpleDiffRequestChain requestChain = new SimpleDiffRequestChain(Arrays.asList(request, request));

        DiffManager.getInstance().showDiff(project, requestChain, new DiffDialogHints(WindowWrapper.Mode.FRAME, dialogPanel));
    }
}
