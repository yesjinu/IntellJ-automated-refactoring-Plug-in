package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Refactoring Techinque: Extract long variables
 *
 * @author Mintae Kim
 */
public class ExtractVariable extends BaseRefactorAction {

    public boolean isRefactorable() {
        return false;
    }

    @Override
    public String storyName() {
        return "Extract Variable";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor(AnActionEvent e) {

    }
}
