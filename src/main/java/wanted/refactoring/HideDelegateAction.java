package wanted.refactoring;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.*;

/**
 * Class to provide refactoring: 'Introduce Local Extension'
 * - Create new class that inherits the utility,
 * - Add the method required by the user in the class,
 *
 * @author Chanyoung Kim
 */
public class HideDelegateAction extends BaseRefactorAction {
    private static Project project;
    private static PsiFile file;
    private static PsiClass targetClass;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String descripton() {
        return "<html>, <br/>" +
                ".</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>, <br/>" +
                ".</html>";
    }


    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        if (project == null) return false;

        file = navigator.findFile();
        if (file == null) return false;

        targetClass = navigator.findClass();
        if (targetClass == null) return false;

        return refactorValid(project, targetClass);
    }


    public static boolean refactorValid(Project project, @NotNull PsiClass targetClass) {




        return true;
    }

    /**
     * Method that performs refactoring: 'Introduct Local Extension'
     *
     * How to implement this refactoring:
     * 1. Change it to a new PsiDeclarationStatement.
     * 2. Add a new method at the end of the class to which the method belongs.
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e)
    {
        Project project = e.getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        WriteCommandAction.runWriteCommandAction(project, () -> {

        });
    }
}
