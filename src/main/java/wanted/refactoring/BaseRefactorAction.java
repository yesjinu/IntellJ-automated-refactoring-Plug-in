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
     * @return true if method is refactorable
     */
    public abstract boolean refactorValid(AnActionEvent e);

    /**
     * Method that performs refactoring.
     *
     * @param e AnActionEvent
     */
    protected abstract void refactor(AnActionEvent e);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        boolean visible = refactorValid(e) && isActionAvailable(e);

        final Presentation presentation = e.getPresentation();
        presentation.setVisible(visible);
        presentation.setEnabled(visible);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        refactorRequest(e);
    }

    private boolean isActionAvailable(AnActionEvent e) {
        final VirtualFile file = getVirtualFiles(e);
        if (getEventProject(e) != null && file != null) {
            final FileType fileType = file.getFileType();
            return StdFileTypes.JAVA.equals(fileType);
        }
        return false;
    }

    private VirtualFile getVirtualFiles(AnActionEvent e) {
        return PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }

    /* apply refactoring if it's available */
    private void refactorRequest(AnActionEvent e)
    {
        if(!refactorValid(e)){ Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null); }
        else
        {
            refactor(e);
        }
    }
}
