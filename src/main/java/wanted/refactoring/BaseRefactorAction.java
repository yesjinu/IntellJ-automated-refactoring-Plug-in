package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class to provide refactoring techniques.
 *
 * @author seha Park
 * @author seungjae yoo
 * @author Chanyoung Kim
 */
public abstract class BaseRefactorAction extends AnAction {

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     */
    public abstract String storyName();

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
    protected abstract void refactor(AnActionEvent e);

    /**
     * Implement this method to provide your action handler.
     *
     * @param e AnActionEvent
     * @see AnAction#update(AnActionEvent)
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
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
    public void refactorRequest(AnActionEvent e)
    {
        if(!refactorValid(e)) {
            Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null);
        }
        else
        {
            refactor(e);
        }
    }

    /**
     * Helper Method that applies refactoring if it's available.
     * When refactor is valid, open window to show before-after
     *
     * @param e AnActionEvent
     */
    private void refactorRequestWithWindow(AnActionEvent e)
    {
        if(!refactorValid(e)) {
            Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null);
        }
        else
        {
            refactor(e);
        }
    }
}
