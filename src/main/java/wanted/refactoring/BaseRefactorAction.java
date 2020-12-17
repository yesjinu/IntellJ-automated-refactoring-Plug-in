package wanted.refactoring;

import com.intellij.diff.*;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import wanted.ui.DiffWindowWithButton;
import wanted.utils.NavigatePsi;
import wanted.utils.TraverseProjectPsi;

import java.util.*;

/**
 * Abstract class to provide refactoring techniques.
 *
 * @author seha Park
 * @author seungjae yoo
 * @author Chanyoung Kim
 */
public abstract class BaseRefactorAction extends AnAction {

    /**
     * Returns the story ID.
     *
     * @return story ID
     */
    public abstract String storyID();

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     */
    public abstract String storyName();

    /**
     * Returns the description of each story.
     * You must use html-style (<html>content</html>) for multi-line explanation.
     *
     * @return description of each stories as a sting format
     */
    public abstract String description();

    /**
     * Returns the precondition of each story.
     * You must use html-style (<html>content</html>) for multi-line explanation.
     *
     * @return description of each stories as a sting format
     */
    public abstract String precondition();

    /**
     * Method that checks whether candidate method is refactorable.
     *
     * @param e AnActionEvent
     * @return true if method is refactorable
     */
    public abstract boolean refactorValid(AnActionEvent e);

    /**
     * Method that performs refactoring.
     *
     * @param e AnActionEvent
     */
    public abstract void refactor(AnActionEvent e);

    /**
     * Implement this method to provide your action handler.
     *
     * @param e AnActionEvent
     * @see AnAction#update(AnActionEvent)
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        refactorRequestWithWindow(e);
    }

    /**
     * Updates the state of the action.
     * If refactoring is possible, make the function enabled and visible.
     *
     * @param e AnActionEvent
     * @see AnAction#update(AnActionEvent)
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        boolean visible = refactorValid(e) && isActionAvailable(e);

        final Presentation presentation = e.getPresentation();
        presentation.setVisible(visible);
        presentation.setEnabled(visible);
    }

    /**
     * Check if the file to which the action is applied exists and whether the action is possible.
     *
     * @param e AnActionEvent
     * @return true if action is valid in terms of virtual file and presence of project
     */
    private boolean isActionAvailable(AnActionEvent e) {
        final VirtualFile file = getVirtualFiles(e);
        if (getEventProject(e) != null && file != null) {
            final FileType fileType = file.getFileType();
            return StdFileTypes.JAVA.equals(fileType);
        }
        return false;
    }

    /**
     * Returns the VirtualFile to which the action is applied
     *
     * @param e AnActionEvent
     * @return VirtualFile in Project
     */
    private VirtualFile getVirtualFiles(AnActionEvent e) {
        return PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }

    /**
     * Helper Method that applies refactoring if it's available.
     *
     * @param e AnActionEvent
     */
    public void refactorRequest(AnActionEvent e) {
        if (!refactorValid(e)) {
            Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null);
        } else {
            refactor(e);
        }
    }

    /**
     * Helper Method that applies refactoring if it's available.
     * When refactor is valid, open window to show before-after
     *
     * @param e AnActionEvent
     */
    private void refactorRequestWithWindow(AnActionEvent e) {
        if (!refactorValid(e)) {
            Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null);
        } else {
            NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);
            Project project = navigator.findProject();
            List<PsiFile> fileList = TraverseProjectPsi.findFile(project);

            Map<PsiFile, String> fileMap = new HashMap<>();
            for (PsiFile f : fileList) fileMap.put(f, f.getText());

            // Refactor
            refactor(e);

            // Composing changeMap (PsiFile -> PsiFile for Changed PsiFiles)
            List<SimpleDiffRequest> requestList = new ArrayList<>();
            Map<PsiFile, String> changeMap = new HashMap<>();
            PsiFile[] ff = fileMap.keySet().toArray(new PsiFile[fileMap.size()]);
            for (PsiFile f : ff) {
                if (!f.getText().equals(fileMap.get(f))) {
                    DiffContent contentBefore = DiffContentFactory.getInstance().create(project, fileMap.get(f));
                    DiffContent contentAfter = DiffContentFactory.getInstance().create(project, f.getText());
                    requestList.add(new SimpleDiffRequest(f.getName(), contentBefore, contentAfter, "Original", "Refactor"));

                    changeMap.put(f, f.getText());
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        Document document = PsiDocumentManager.getInstance(project).getDocument(f);
                        document.setText(fileMap.get(f));
                    });
                }
            }

            // Opening Window
            SimpleDiffRequestChain requestChain = new SimpleDiffRequestChain(requestList);
            DiffWindowWithButton window = new DiffWindowWithButton(
                    project, requestChain, new DiffDialogHints(WindowWrapper.Mode.FRAME),
                    changeMap, this);
            window.show();
        }
    }
}