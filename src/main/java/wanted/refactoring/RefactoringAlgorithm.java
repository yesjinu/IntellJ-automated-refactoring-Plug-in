package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public abstract class RefactoringAlgorithm extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        refactorRequest(e);

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

    /* return story name for message */
    public abstract String storyName();

    /* return true if refactoring is available */
    public abstract boolean refactorValid(AnActionEvent e);

    /* perform refactoring */
    protected abstract void refactor(AnActionEvent e);
}
