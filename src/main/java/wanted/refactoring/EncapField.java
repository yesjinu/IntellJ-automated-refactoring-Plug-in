package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Encapsulate Field'
 *
 * @author seha Park
 */
public class EncapField extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private PsiFile file;
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
        return "Encapsulation Field";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Encapsulation Field'.
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
        file = navigator.findFile();
        targetClass = navigator.findClass();
        if(targetClass==null){ return false; }

        // find member from caret
        member = navigator.findField();

        return refactorValid(project, member);
    }

    /**
     * Method that performs refactoring: 'Encapsulate Field'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
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

    /**
     * Helper method that checks whether candidate method is refactorable using 'Encapsulate Field'.
     *
     * Every candidate fields should follow these two requisites:
     * 1. Field should be public
     * 2. It has neither getter nor setter
     *
     * @param project Project
     * @param member PsiField
     * @return true if method is refactorable
     * @see InlineMethodAction#refactorValid(Project, PsiMethod)
     */
    public static boolean refactorValid(Project project, PsiField member) {
        if(member==null){ return false; } // nothing is chosen

        if(!member.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)){ return false; } // member is not public

        // check if there's getter or setter
        String newName = CreatePsi.capitalize(member);
        List<String> methods = new ArrayList<>();
        methods.add("get"+newName); methods.add("set"+newName);

        methods = FindPsi.checkDuplicateName(member.getContainingClass(), methods);
        if(methods.size()!=2){ return false; } // there's either getMember or setMember already

        return true;
    }
}
