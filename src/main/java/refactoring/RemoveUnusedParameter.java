package refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;
import com.intellij.refactoring.Refactoring;

public class RemoveUnusedParameter extends RefactoringAlgorithm {

    @Override
    public String storyName() {
        return "Remove Unused Parameter";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor() {

    }
}
