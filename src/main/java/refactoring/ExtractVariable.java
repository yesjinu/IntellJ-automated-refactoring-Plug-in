package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import refactoring.RefactoringAlgorithm;

public class ExtractVariable extends RefactoringAlgorithm {

    public boolean isRefactorable() {
        return false;
    }

    /* return story name for message */
    public String storyName(){ return "Extract Variable"; }

    /* return true if refactoring is available */
    public boolean refactorValid(AnActionEvent e){ return false; }

    /* perform refactoring */
    protected void refactor(){ }
}
