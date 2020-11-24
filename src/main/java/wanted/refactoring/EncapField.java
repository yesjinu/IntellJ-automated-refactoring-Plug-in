package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.*;

import java.util.ArrayList;
import java.util.List;

public class EncapField extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private PsiFile file;
    private List<PsiReferenceExpression> references;

    @Override
    public String storyName()
    {
        return "Encapsulation Field";
    }

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        file = navigator.findFile();

        targetClass = navigator.findClass();
        if(targetClass==null){ return false; }

        List<PsiField> members = navigator.findPublicField(); // find public field
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
    protected void refactor(AnActionEvent e)
    {
       references = FindPsi.findMemberReference(file, member);

       List<PsiElement> addList = new ArrayList<>();

        // create getter and setter
        PsiMethod getMember = CreatePsi.createGetMethod(project, member, PsiModifier.PUBLIC);
        addList.add(getMember);
        PsiMethod setMember = CreatePsi.createSetMethod(project, member, PsiModifier.PUBLIC);
        addList.add(setMember);

        // create modifier
        List<String> removeValue = new ArrayList<>();
        removeValue.add(PsiModifier.PUBLIC);
        List<String> addValue = new ArrayList<>();
        addValue.add(PsiModifier.PRIVATE);

        WriteCommandAction.runWriteCommandAction(project, ()->{
            AddPsi.addMethod(targetClass, addList); // add method in addList to targetClass
            ReplacePsi.changeModifier(member, removeValue, addValue); // replace modifier
            ReplacePsi.encapFied(project, (PsiMethod)addList.get(0), (PsiMethod)addList.get(1), references); // encapsulate with getter and setter
        });
    }
}
