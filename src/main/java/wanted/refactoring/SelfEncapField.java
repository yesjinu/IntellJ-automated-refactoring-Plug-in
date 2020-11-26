package wanted.refactoring;

import wanted.utils.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Self Encapsulate Field'
 *
 * @author seha Park
 */
public class SelfEncapField extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private List<PsiReferenceExpression> references;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName()
    {
        return "Self Encapsulation Field";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Self Encapsulation Field'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
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
        member = members.get(0); // -> traverse version
        //member = FindPsi.findMemberByCaret(navigator.findFile(), e); // caret version
        //if(!member.getModifierList().hasModifierProperty(PsiModifier.PRIVATE)){ return false; } // fail if member is not private

        // check if there's getMember or setMember
        String newName = CreatePsi.capitalize(member);
        List<String> methods = new ArrayList<>();
        methods.add("get"+newName); methods.add("set"+newName);

        List<String> methodToImpl = navigator.findMethodByName(methods);
        if(methodToImpl.size()!=2){ return false; } // there's either getMember or setMember already

        return true;
    }

    /**
     * Method that performs refactoring: 'Self Encapsulate Field'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    protected void refactor(AnActionEvent e)
    {
        references = FindPsi.findMemberReference(targetClass, member);

        List<PsiElement> addList = new ArrayList<>();

        // create getter and setter
        PsiMethod getMember = CreatePsi.createGetMethod(project, member, PsiModifier.PROTECTED);
        addList.add(getMember);
        PsiMethod setMember = CreatePsi.createSetMethod(project, member, PsiModifier.PROTECTED);
        addList.add(setMember);

        WriteCommandAction.runWriteCommandAction(project, ()->{
            AddPsi.addMethod(targetClass, addList); // add method in addList to targetClass
            ReplacePsi.encapFied(project, (PsiMethod)addList.get(0), (PsiMethod)addList.get(1), references); // encapsulate with getter and setter
        });
    }
}
