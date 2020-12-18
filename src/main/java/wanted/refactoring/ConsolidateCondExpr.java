package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiStatement;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

/**
 * Class to provide refactoring: 'Consolidate Conditional Expression'
 *
 * @author seungjae yoo
 */
public class ConsolidateCondExpr extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;

    private PsiIfStatement ifStatement;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "CCE";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Consolidate Conditional Expression";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When all statements are same for several conditions from first,<br/>" +
                "merge conditions and decrease number of conditions.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>Make sure that the cursor is in if Statement.<br/>" +
                "This refactoring is valid when all statements in first two conditions are same.</html>";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Consolidate Conditional Expression'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        if(project==null) return false;
        targetClass = navigator.findClass();
        if(targetClass==null) return false;

        if (e.getData(PlatformDataKeys.EDITOR) == null) return false;
        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        ifStatement = FindPsi.findIfStatement(targetClass, offset);
        if (ifStatement == null) return false;

        return refactorValid(ifStatement);
    }

    /**
     * Determine whether PsiIfStatement object can refactor
     *
     * @param s the target which should be validated
     * @return true if s is valid to refactor
     */
    public static boolean refactorValid(PsiIfStatement s) {
        PsiStatement thenStatement = s.getThenBranch();
        PsiStatement elseStatement = s.getElseBranch();
        if (elseStatement == null) return false;
        else if (elseStatement instanceof PsiIfStatement) elseStatement = ((PsiIfStatement) elseStatement).getThenBranch();

        String thenText = thenStatement.getText();
        String elseText = elseStatement.getText();
        return thenText.equals(elseText);
    }

    /**
     * Method that performs refactoring: 'Consolidate Conditional Expression'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
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
