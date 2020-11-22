package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.*;

import java.util.ArrayList;
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

        return true;
    }

    @Override
    protected void refactor(AnActionEvent e)
    {

        WriteCommandAction.runWriteCommandAction(project, ()->{
        });
    }
}
