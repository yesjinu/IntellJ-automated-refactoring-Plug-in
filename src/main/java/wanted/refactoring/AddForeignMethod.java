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
     * Method that checks whether candidate method is refactorable
     * using 'Add Foreign Method'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        return true;
    }

    /**
     * Method that performs refactoring: 'Add Foreign Method'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    protected void refactor(AnActionEvent e) {

    }
}