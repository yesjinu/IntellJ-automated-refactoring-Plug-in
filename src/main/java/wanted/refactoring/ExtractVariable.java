package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

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
