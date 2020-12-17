package wanted.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Class to navigate Psi structure, use for RefactorValid().
 *
 * @author seha Park
 * @author Jinu Noh
 * @author Mintae Kim
 * @author kornilova-1
 */
public class NavigatePsi {
    private static NavigatePsi navigator = null;

    private static Project focusProject = null;
    private static PsiFile focusFile = null;
    private static PsiClass focusClass = null;
    private static PsiMethod focusMethod = null;
    private static PsiField focusField = null;
    private static PsiLiteralExpression focusLiteral = null;

    private static Editor editor;
    private static int caret;

    /**
     * Collect project and psi file from given context
     *
     * @param e AnActionEvent
     */
    private NavigatePsi(AnActionEvent e) {
        editor = e.getData(CommonDataKeys.EDITOR);

        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE);

        if(focusFile==null){
            focusClass = null;
            focusMethod = null;
        }
        else {
            try {
                focusClass = ((PsiClassOwner) focusFile).getClasses()[0];
            } catch (ArrayIndexOutOfBoundsException exception) {
                focusClass = null; // no class in current file
            } catch (ClassCastException exception)
            {
                focusClass = null; // no class in current file
            }

            try{
                caret = editor.getCaretModel().getOffset();
            } catch(NullPointerException exception) // no caret
            {
                focusMethod = null;
                focusField = null;
                focusLiteral = null;
                return;
            }

            focusMethod = PsiTreeUtil.getParentOfType(focusFile.findElementAt(caret), PsiMethod.class);
            focusField = PsiTreeUtil.getParentOfType(focusFile.findElementAt(caret), PsiField.class);
            focusLiteral = PsiTreeUtil.getParentOfType(focusFile.findElementAt(caret), PsiLiteralExpression.class);
        }
    }

    /**
     * factory for navigator
     *
     * @param e event
     * @return NavigatePsi object
     */
    public static NavigatePsi NavigatorFactory(AnActionEvent e) {
        navigator = new NavigatePsi(e);
        return navigator;
    }

    /* return currently open project */
    public Project findProject() {
        return focusProject;
    }

    /* return currently open file */
    public PsiFile findFile() {
        return focusFile;
    }

    /* return currently class of current file*/
    public PsiClass findClass() {
        return focusClass;
    }

    /* return first method of focus class */
    public PsiMethod findMethod() {
        return focusMethod;
    }

    /* return chosen field */
    public PsiField findField() {
        return focusField;
    }

    /* return chosen literal expression */
    public PsiLiteralExpression findLiteral() {
        return focusLiteral;
    }
}
