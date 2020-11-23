package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class SimplifyingMethodCalls extends RefactoringAlgorithm{
    @Override
    public String storyName() {
        return "Simplifying Method Calls";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor() {

    }
}
