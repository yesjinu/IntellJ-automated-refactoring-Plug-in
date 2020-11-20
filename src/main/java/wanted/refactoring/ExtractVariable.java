package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class ExtractVariable extends BaseRefactorAction {

    public boolean isRefactorable() {
        return false;
    }

    /* return story name for message */
    public String storyName(){ return "Extract Variable"; }

    /* return true if refactoring is available */
    public boolean refactorValid(AnActionEvent e){ return false; }

    @Override
    /* perform refactoring */
    protected void refactor(AnActionEvent e) {

    }
}
