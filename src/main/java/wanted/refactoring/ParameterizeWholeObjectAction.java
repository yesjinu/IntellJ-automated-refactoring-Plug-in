package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.AddPsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ParameterizeWholeObjectAction extends BaseRefactorAction{
    @Override
    public String storyName() {
        return "Parameterize Whole Object";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return false;
    }

    @Override
    protected void refactor(AnActionEvent e) {

    }
}
