package wanted.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import wanted.refactoring.BaseRefactorAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.lang.reflect.Method;


public class PsiUtilsTest extends BaseRefactorAction {
    public Project project;
    public PsiField member;

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

        testParams[0] = member;

        WriteCommandAction.runWriteCommandAction(project, ()->{
            try{ testMethod.invoke(testClass, testParams); }
            catch(Exception e2){}
        });
    }

}