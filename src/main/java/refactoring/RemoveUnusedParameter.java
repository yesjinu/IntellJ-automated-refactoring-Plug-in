package SimplifyMethodCalls;

import DataOrganize.Common.Refactoring;
import DataOrganize.Common.PsiUtil.FindPsi;
import DataOrganize.Common.PsiUtil.CreatePsi;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;

public class RemoveUnusedParameter extends Refactoring {

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
