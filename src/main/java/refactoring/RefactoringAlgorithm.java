package refactoring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public abstract class RefactoringAlgorithm extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent e) {
        int result = Messages.showYesNoDialog("Apply "+storyName(), "Wanted Refactoring", null);
        if(result==0) // perform refactoring
        {
            refactorRequest(e);
        }
    }

    /* apply refactoring if it's available */
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

    /* return true if refactoring is available */
    public abstract boolean refactorValid(AnActionEvent e);

    /* perform refactoring */
    protected abstract void refactor();
}
