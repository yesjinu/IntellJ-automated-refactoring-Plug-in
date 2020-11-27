package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Consolidate Duplicate Conditional Fragments'
 *
 * @author seungjae yoo
 */
public class ConsolidateDupCondFrag extends BaseRefactorAction {

    private Project project;
    private PsiClass targetClass;

    private PsiIfStatement ifStatement;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "Consolidate Duplicate Conditional Fragments";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Consolidate Duplicate Conditional Fragments'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        targetClass = navigator.findClass();
        if(targetClass==null) return false;

        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        ifStatement = FindPsi.findIfStatement(targetClass, offset);
        if (ifStatement == null) return false;
        while (ifStatement.getParent() instanceof PsiIfStatement) ifStatement = (PsiIfStatement) ifStatement.getParent();

        return refactorValid(ifStatement);
    }

    /**
     * Determine whether PsiIfStatement object can refactor
     *
     * @param s the target which should be validated
     * @return true if s is valid to refactor
     */
    public static boolean refactorValid(PsiIfStatement s) {
        List<PsiStatement> statementList = new ArrayList<>();
        PsiStatement nowStatement = s;
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

    /**
     * Method that performs refactoring: 'Consolidate Duplicate Conditional Fragments'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    protected void refactor(AnActionEvent e) {
        List<PsiStatement> statementList = new ArrayList<>();
        PsiStatement nowStatement = ifStatement;
        while (nowStatement instanceof PsiIfStatement) {
            statementList.add(((PsiIfStatement) nowStatement).getThenBranch());
            nowStatement = ((PsiIfStatement) nowStatement).getElseBranch();
        }
        statementList.add(nowStatement);

        while (isDupStatementFirst(statementList)) {
            WriteCommandAction.runWriteCommandAction(project, ()->{
                ReplacePsi.pulloutFirstCondExpr(project, ifStatement, statementList);
            });
        }

        while (isDupStatementLast(statementList)) {
            WriteCommandAction.runWriteCommandAction(project, ()->{
                ReplacePsi.pulloutLastCondExpr(project, ifStatement, statementList);
            });
        }
        WriteCommandAction.runWriteCommandAction(project, ()->{
            ReplacePsi.removeUselessCondition(project, ifStatement);
        });
    }

    /**
     * Helper Method which determines whether first statement of each condition is same
     *
     * @param statementList List of Statement
     * @return true if first statement is same for every condition
     *         false otherwise
     */
    private static boolean isDupStatementFirst(List<PsiStatement> statementList) {
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

    /**
     * Helper Method which determines whether last statement of each condition is same
     *
     * @param statementList List of Statement
     * @return true if last statement is same for every condition
     *         false otherwise
     */
    private static boolean isDupStatementLast(List<PsiStatement> statementList) {
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