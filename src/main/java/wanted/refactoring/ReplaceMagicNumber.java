package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.*;

import java.util.List;

/**
 * Class to replace magic number to constant symbol
 * @author seha Park
 */
public class ReplaceMagicNumber extends BaseRefactorAction{
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private List<PsiReferenceExpression> references;

    @Override
    public String storyName()
    {
        return "Replace Magic Number";
    }

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();

        // find all constants
        // for each constants, determine whether it is worth to refactor
        // if false, find other constants
        // if true, proceed to refactor (for now)

        // if there's no constant worth to refactor, return false

        return true;
    }

    @Override
    protected void refactor(AnActionEvent e)
    {
        // find expression with same value (it can be numeric value or string)
        // build symbolic constant with name constant#N (need to check duplicate)


        WriteCommandAction.runWriteCommandAction(project, ()->{
            // replace values
            // introduce constant
        });
    }
}
