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
    private PsiField member;
    private List<PsiReferenceExpression> references;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "Self Encapsulation Field";
    }

    /**
     * Returns the description of each story.
     * You can freely use html-style (<html>content</html>).
     *
     * @return description of each stories as a sting format
     * @see BaseRefactorAction#descripton()
     */
    @Override
    public String descripton() {
        // TODO: description
        return "Description.";
    }

    /**
     * Returns the name of subdirectory for example code.
     *
     * @return subdirectory name
     * @see BaseRefactorAction#getSubdirectoryName()
     */
    @Override
    protected String getSubdirectoryName() {
        // TODO: Directory
        return "Directory";
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
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        if (project == null) {
            return false;
        }

        // find member from caret
        member = navigator.findField();

        return refactorValid(project, member);
    }

    /**
     * Helper method that checks whether candidate method is refactorable using 'Self Encapsulate Field'.
     *
     * Every candidate fields should follow these two requisites:
     * 1. Field should be private
     * 2. It has neither getter nor setter
     *
     * @param project Project
     * @param member  PsiField Object
     * @return true if method is refactorable
     * @see InlineMethodAction#refactorValid(Project, PsiMethod)
     */
    public static boolean refactorValid(Project project, PsiField member) {
        if (member == null || member.getContainingClass() == null) {
            return false;
        } // nothing is chosen or invalid member

        if (member.getModifierList() == null) {
            return false;
        }
        else if (!member.getModifierList().hasModifierProperty(PsiModifier.PRIVATE)) {
            return false;
        } // member is not private

        // check if there's getter or setter
        String newName = CreatePsi.capitalize(member);
        List<String> methods = new ArrayList<>();
        methods.add("get" + newName);
        methods.add("set" + newName);

        methods = FindPsi.checkDuplicateName(member.getContainingClass(), methods);
        if (methods.size() != 2) {
            return false;
        } // there's either getMember or setMember already

        return true;
    }

    /**
     * Method that performs refactoring: 'Self Encapsulate Field'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        references = FindPsi.findMemberReference(member.getContainingClass(), member);

        List<PsiElement> addList = new ArrayList<>();

        // create getter and setter
        PsiMethod getMember = CreatePsi.createGetMethod(project, member, PsiModifier.PROTECTED);
        addList.add(getMember);
        PsiMethod setMember = CreatePsi.createSetMethod(project, member, PsiModifier.PROTECTED);
        addList.add(setMember);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            AddPsi.addMethod(member.getContainingClass(), addList); // add method in addList to targetClass
            ReplacePsi.encapFied(project, (PsiMethod) addList.get(0), (PsiMethod) addList.get(1), references); // encapsulate with getter and setter
        });
    }
}
