package wanted.test.Utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import wanted.refactoring.BaseRefactorAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import wanted.utils.AddPsi;
import wanted.utils.NavigatePsi;

import java.lang.reflect.Method;


public class PsiUtilsTest extends BaseRefactorAction {
    public Project project;
    public PsiField member;
    public PsiClass focusClass;

    public Object testClass;
    public Method testMethod;
    public Object[] testParams;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "";
    }

    @Override
    public boolean refactorValid(AnActionEvent e) {
        return true;
    }

    @Override
    public void refactor(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e); //?
        project = navigator.findProject(); //?
        member = navigator.findField(); //?
        focusClass = navigator.findClass();
    }

}