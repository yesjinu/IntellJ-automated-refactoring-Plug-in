package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Class to provide refactoring: 'Extract Variable'
 *
 * @author Mintae Kim
 */
public class ExtractVariable extends BaseRefactorAction {

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
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
