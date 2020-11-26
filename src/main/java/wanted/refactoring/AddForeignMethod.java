package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class AddForeignMethod extends BaseRefactorAction {

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "Add Foreign Method";
    }

    /**
     * Returns the possibility of wanted.refactoring for current project with particular strategy.
     *
     * @param e An Actionevent
     * @return true if wanted.refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        return true;
    }

    /**
     * Method that performs wanted.refactoring.
     */
    @Override
    protected void refactor(AnActionEvent e) {

    }
}