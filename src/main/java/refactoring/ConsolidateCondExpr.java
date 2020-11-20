package refactoring;

import com.github.markusbernhardt.proxy.util.PListParser;
import com.intellij.ide.projectWizard.ModuleTypeCategory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.mozilla.javascript.ast.IfStatement;
import utils.FindPsi;
import utils.NavigatePsi;

import java.util.ArrayList;
import java.util.List;

// Edited by YSJ
public class ConsolidateCondExpr extends refactoring.RefactoringAlgorithm {

    private Project project;
    private PsiClass targetClass;

    private PsiIfStatement ifStatement;

    @Override
    public String storyName() {
        return "Consolidate Conditional Exp";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        targetClass = navigator.findClass();
        if(targetClass==null) return false;

        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        ifStatement = FindPsi.findIfStatement(targetClass, offset);
        if (ifStatement == null) return false;

        PsiStatement thenStatement = ifStatement.getThenBranch();
        PsiStatement elseStatement = ifStatement.getElseBranch();
        if (elseStatement == null) return false;
        else if (elseStatement instanceof PsiIfStatement) elseStatement = ((PsiIfStatement) elseStatement).getThenBranch();

        String thenText = thenStatement.getText();
        String elseText = elseStatement.getText();
        return thenText.equals(elseText);
    }

    @Override
    protected void refactor() {

    }
}
