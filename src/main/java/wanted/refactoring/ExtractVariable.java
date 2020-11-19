package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class ExtractVariable extends RefactoringAlgorithm {

    public boolean isRefactorable() {
        return false;
    }

    @Override
    public String storyName() {
        return null;
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor(AnActionEvent e) {

    }
}
