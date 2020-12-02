package wanted.ui;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.impl.DiffRequestProcessor;
import com.intellij.diff.impl.DiffWindow;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.psi.PsiFile;
import org.apache.commons.codec.language.bm.Languages;
import org.jetbrains.annotations.Nullable;
import wanted.refactoring.BaseRefactorAction;
import wanted.utils.NavigatePsi;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RefactorPreviewWindow extends DialogWrapper {

    private JPanel dialogPanel;
    private JPanel diffPanel;

    private BaseRefactorAction refactorAction;
    private AnActionEvent e;

    public RefactorPreviewWindow(BaseRefactorAction refactorAction, AnActionEvent e) {
        super(true);

        this.refactorAction = refactorAction;
        this.e = e;

        init();
        setTitle("Refactoring Preview: " + refactorAction.storyName());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        dialogPanel = new JPanel(new BorderLayout(10, 10));

        diffPanel = new JPanel(new GridLayout(0, 1));
        dialogPanel.add(diffPanel, BorderLayout.CENTER);

        /*
        Set<PsiFile> alterFileList = refactorAction.getAlterFileList(e);

        List<DiffContent> contentBeforeList = new ArrayList<>();

        for (int i = 0; i < Math.max(alterFileList.size(), 3); i++){
            PsiFile psiFile = alterFileList.get(i);
            DiffContent contentBefore = DiffContentFactory.getInstance().create(project, psiFile.getText());
            contentBeforeList.add(contentBefore);

        refactorAction.refactor(e);

        for (int i = 0; i < Math.max(alterFileList.size(), 3); i++){
            PsiFile psiFile = alterFileList.get(i);
            DiffContent contentAfter = DiffContentFactory.getInstance().create(project, psiFile.getText());

            SimpleDiffRequest request = new SimpleDiffRequest("Before - After", contentBefore, contentAfter, "Before", "After");
            SimpleDiffRequestChain requestChain = new SimpleDiffRequestChain(Arrays.asList(request, request));

            // TODO
            JComponent dialogPanel = new JPanel();
            DiffWindow b = new DiffWindow(project, requestChain, new DiffDialogHints(WindowWrapper.Mode.FRAME, dialogPanel));

        */

        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
        Project project = navigator.findProject();
        PsiFile psiFile = navigator.findFile();

        DiffContent contentBefore = DiffContentFactory.getInstance().create(project, psiFile.getText());

        WriteCommandAction.runWriteCommandAction(project, ()-> {
            refactorAction.refactor(e);
        });

        for (int i = 0; i < 3; i++) {
            JPanel subPanel = new JPanel(new BorderLayout (10, 10));

            JLabel fileLabel = new JLabel(psiFile.getName());
            fileLabel.setFont(new Font("Jetbrains Mono", Font.PLAIN, 11));
            subPanel.add(fileLabel, BorderLayout.NORTH);

            // Editor
            DiffContent contentAfter = DiffContentFactory.getInstance().create(project, psiFile.getText());

            SimpleDiffRequest request = new SimpleDiffRequest("Before - After", contentBefore, contentAfter, "Before", "After");
            SimpleDiffRequestChain requestChain = new SimpleDiffRequestChain(Arrays.asList(request, request));

            // TODO

            JComponent diffComponent =
                    new DiffWindow(project, requestChain, new DiffDialogHints(WindowWrapper.Mode.FRAME)) {
                public JComponent getMyWrapper() {
                    DiffRequestProcessor myProcessor = createProcessor();
                    return myProcessor.getComponent();
                }
            }.getMyWrapper();

            subPanel.add(diffComponent, BorderLayout.CENTER);
            diffPanel.add(subPanel);
        }

        if (true) { // TODO: MODIFY CONDITION into alterFileList.size() > 3
            // TODO: MODIFY NUMBER to alterFileList.size() - 3
            JLabel andLabel = new JLabel("And " + Integer.toString(5 - 3) + " file(s) more...");
            andLabel.setFont(new Font("Jetbrains Mono", Font.PLAIN, 11));
            dialogPanel.add(andLabel, BorderLayout.SOUTH);
        }

        return dialogPanel;
    }
}
