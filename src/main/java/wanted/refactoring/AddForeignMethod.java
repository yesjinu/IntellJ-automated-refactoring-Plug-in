package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class AddForeignMethod extends RefactoringAlgorithm {

    /**
     * Returns the possibility of wanted.refactoring for current project with particular strategy.
     * @param e An Actionevent
     * @return true if wanted.refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        return true;
    }


    /**
     * Returns the story name as a string format, for message.
     * @return story name "Inline Method"
     */
    @Override
    public String storyName() {
        return "Add Foreign Method";
    }



    /**
     * Method that performs wanted.refactoring.
     */
    @Override
    protected void refactor(AnActionEvent e) {

    }
}