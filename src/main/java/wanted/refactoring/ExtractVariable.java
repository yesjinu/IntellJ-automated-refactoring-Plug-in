package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Returns the story name as a string format, for message.
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
