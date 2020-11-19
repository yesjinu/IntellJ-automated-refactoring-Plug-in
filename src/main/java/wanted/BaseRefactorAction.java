package wanted;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public abstract class BaseRefactorAction extends AnAction {

    protected abstract BaseRefactorHandler initHandler(Project project, DataContext dataContext);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        boolean visible = isActionAvailable(e);

        final Presentation presentation = e.getPresentation();
        presentation.setVisible(visible);
        presentation.setEnabled(visible);
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        int result = Messages.showYesNoDialog("Apply "+storyName(), "Wanted Refactoring", null);
        if(result==0) // perform wanted.refactoring
        {
            refactorRequest(e);
        }

    }

    /* apply wanted.refactoring if it's available */
    private void refactorRequest(AnActionEvent e)
    {
        if(!refactorValid(e)){ Messages.showMessageDialog("Nothing to do", "Wanted Refactoring", null); }
        else
        {
            refactor();
            Messages.showMessageDialog("Refactoring success", "Wanted Refactoring", null);
        }
    }

    /* return story name for message */
    public abstract String storyName();

    /* return true if wanted.refactoring is available */
    public abstract boolean refactorValid(AnActionEvent e);

    /* perform wanted.refactoring */
    protected abstract void refactor();
}
