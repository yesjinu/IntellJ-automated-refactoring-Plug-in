package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;

// Edited by YSJ
public class ConsolidateCondExpr extends refactoring.RefactoringAlgorithm {
    @Override
    public String storyName() {
        return "Consolidate Conditional Exp";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor() {

    }
}
