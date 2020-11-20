package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public abstract class RefactoringAlgorithm extends AnAction {

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
