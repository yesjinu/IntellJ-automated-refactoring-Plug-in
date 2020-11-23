package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiStatement;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.ArrayList;
import java.util.List;

public class ConsolidateDupCondFrag extends BaseRefactorAction {

    private Project project;
    private PsiClass targetClass;

    private PsiIfStatement ifStatement;

    @Override
    public String storyName() {
        return "Consolidate Duplicate Conditional Fragments";
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

        List<PsiStatement> statementList = new ArrayList<>();
        PsiStatement nowStatement = ifStatement;
        while (nowStatement instanceof PsiIfStatement) {
            statementList.add(((PsiIfStatement) nowStatement).getThenBranch());
            nowStatement = ((PsiIfStatement) nowStatement).getElseBranch();
        }
        if (nowStatement == null) return false;
        else statementList.add(nowStatement);

        if (isDupStatementFirst(statementList)) return true;
        if (isDupStatementLast(statementList)) return true;
        return false;
    }

    @Override
    protected void refactor(AnActionEvent e) {
        List<PsiStatement> statementList = new ArrayList<>();
        PsiStatement nowStatement = ifStatement;
        while (nowStatement instanceof PsiIfStatement) {
            statementList.add(((PsiIfStatement) nowStatement).getThenBranch());
            nowStatement = ((PsiIfStatement) nowStatement).getElseBranch();
        }
        statementList.add(nowStatement);
    }

    private boolean isDupStatementFirst(List<PsiStatement> statementList) {
        PsiStatement nowStatement;
        PsiStatement standardStatement = statementList.get(0);

        if (standardStatement instanceof PsiBlockStatement) {
            if (((PsiBlockStatement) standardStatement).getCodeBlock().getStatementCount() == 0) return false;
            else standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[0];
        }
        for (PsiStatement s : statementList) {
            nowStatement = s;
            if (nowStatement instanceof PsiBlockStatement) {
                if (((PsiBlockStatement) nowStatement).getCodeBlock().getStatementCount() == 0) return false;
                else nowStatement = ((PsiBlockStatement) nowStatement).getCodeBlock().getStatements()[0];
            }
            String sText = standardStatement.getText();
            String nText = nowStatement.getText();
            if (!sText.equals(nText)) return false;
        }
        return true;
    }

    private boolean isDupStatementLast(List<PsiStatement> statementList) {
        PsiStatement nowStatement;
        PsiStatement standardStatement = statementList.get(0);

        if (standardStatement instanceof PsiBlockStatement) {
            if (((PsiBlockStatement) standardStatement).getCodeBlock().getStatementCount() == 0) return false;
            else {
                int size = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatementCount();
                standardStatement = ((PsiBlockStatement) standardStatement).getCodeBlock().getStatements()[size-1];
            }
        }
        for (PsiStatement s : statementList) {
            nowStatement = s;
            if (nowStatement instanceof PsiBlockStatement) {
                if (((PsiBlockStatement) nowStatement).getCodeBlock().getStatementCount() == 0) return false;
                else {
                    int size = ((PsiBlockStatement) nowStatement).getCodeBlock().getStatementCount();
                    nowStatement = ((PsiBlockStatement) nowStatement).getCodeBlock().getStatements()[size-1];
                }
            }
            String sText = standardStatement.getText();
            String nText = nowStatement.getText();
            if (!sText.equals(nText)) return false;
        }
        return true;
    }
}