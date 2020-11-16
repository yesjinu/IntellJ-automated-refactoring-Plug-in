package refactoring;

import utils.CreatePsi;
import utils.NavigatePsi;
import utils.FindPsi;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

/* class to provide self encapsulate field refactoring */
public class SelfEncapField extends refactoring.RefactoringAlgorithm {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private List<PsiReferenceExpression> statements;

    @Override
    public String storyName()
    {
        return "Self Encapsulation Field";
    }

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        targetClass = navigator.findClass();
        if(targetClass==null){ return false; }

        List<PsiField> members = navigator.findPrivateField();
        if(members.isEmpty()){ return false; }

        // ! only encapsulate one member
        member = members.get(0);

        // check if there's getMember or setMember
        String newName = CreatePsi.capitalize(member);
        List<String> methods = new ArrayList<>();
        methods.add("get"+newName); methods.add("set"+newName);

        List<String> methodToImpl = navigator.findMethod(methods);
        if(methodToImpl.size()!=2){ return false; } // there's either getMember or setMember already

        return true;
    }

    @Override
    protected void refactor()
    {
        statements = FindPsi.findMemberReference(targetClass, member);

        List<PsiElement> addList = new ArrayList<>();

        // create getter and setter
        PsiMethod getMember = CreatePsi.createGetMethod(project, member);
        addList.add(getMember);

        PsiMethod setMember = CreatePsi.createSetMethod(project, member);
        addList.add(setMember);

        // create runner and refactor
        SEFRunner runner = new SEFRunner(project, targetClass, addList, statements, member);
        WriteCommandAction.runWriteCommandAction(project, runner);
    }

}