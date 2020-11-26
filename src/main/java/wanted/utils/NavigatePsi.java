package wanted.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sun.istack.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    private static Project focusProject;
    private static PsiFile focusFile;
    private static PsiClass focusClass;
    private static PsiMethod focusMethod;
    private static PsiField focusField;

    private static Editor editor;
    private static int caret;
    
    /**
     * Collect project and psi file from given context
     * 
     * @param e AnActionEvent
     */
    private NavigatePsi(AnActionEvent e)
    {
        editor = e.getData(CommonDataKeys.EDITOR);

        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE); // ? look for only currently opened file

        if (focusFile == null) {
            focusClass = null;
            focusMethod = null;
        }
        else {
            try {
                focusClass = ((PsiClassOwner) focusFile).getClasses()[0];
            } catch (ArrayIndexOutOfBoundsException exception) {
                // NO class in current file
                focusClass = null;
            }

            try {
                caret = editor.getCaretModel().getOffset();
                focusMethod = PsiTreeUtil.getParentOfType(focusFile.findElementAt(caret), PsiMethod.class);
                // focusMethod = FindPsi.getContainingMethod(focusClass, caret);
            } catch (ArrayIndexOutOfBoundsException exception) {
                focusMethod = null;
            }

            try {
                caret = editor.getCaretModel().getOffset();
                focusField = PsiTreeUtil.getParentOfType(focusFile.findElementAt(caret), PsiField.class);
            } catch (ArrayIndexOutOfBoundsException exception) {
                focusField = null;
            }
        }
    }

    /**
     * factory for navigator
     * 
     * @param e event
     * @return NavigatePsi object
     */
    public static NavigatePsi NavigatorFactory(AnActionEvent e)
    {
        navigator = new NavigatePsi(e);
        return navigator;
    }

    /* return currently open project */
    public Project findProject(){ return focusProject; }

    /* return currently open file */
    public PsiFile findFile(){ return focusFile; }

    /* return currently class of current file*/
    public PsiClass findClass(){ return focusClass; }

    /* return first method of focus class */
    public PsiMethod findMethod(){ return focusMethod; }

    /* return chosen field */
    public PsiField findField(){ return focusField; }
}
