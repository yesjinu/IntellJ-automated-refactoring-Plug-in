package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

// Edited by YSJ
public class ConsolidateCondExpr extends BaseRefactorAction {

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
    protected void refactor(AnActionEvent e) {
        PsiStatement thenStatement;
        PsiStatement elseStatement;
        PsiStatement elseThenStatement;

        String thenText, elseText;

        boolean isFirstTime = true;

        while (true) {
            thenStatement = ifStatement.getThenBranch();
            elseStatement = ifStatement.getElseBranch();

            if (elseStatement == null) break;
            else if (!(elseStatement instanceof PsiIfStatement)) {
                thenText = thenStatement.getText();
                elseText = elseStatement.getText();
                if (!thenText.equals(elseText)) break;

                WriteCommandAction.runWriteCommandAction(project, ()->{
                    ReplacePsi.removeCondStatement(project,ifStatement);
                });
                break;
            }
            else {
                elseThenStatement = ((PsiIfStatement) elseStatement).getThenBranch();
                thenText = thenStatement.getText();
                elseText = elseThenStatement.getText();
                if (!thenText.equals(elseText)) break;

                if (isFirstTime) {
                    WriteCommandAction.runWriteCommandAction(project, ()->{
                        ReplacePsi.mergeCondExpr(project, ifStatement, true);
                        ReplacePsi.mergeCondStatement(project,ifStatement);
                    });
                }
                else {
                    WriteCommandAction.runWriteCommandAction(project, ()->{
                        ReplacePsi.mergeCondExpr(project, ifStatement, false);
                        ReplacePsi.mergeCondStatement(project,ifStatement);
                    });
                }
                isFirstTime = false;

            }
        }

    }
}
