/**
 * Class to navigate Psi structure, use for RefactorValid().
 *
 * @author seha park
 */
package utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class NavigatePsi {
    private static NavigatePsi navigator = null;
    private static Project focusProject;
    private static PsiFile focusFile;
    private static PsiClass focusClass;
    private static PsiMethod focusMethod; // 하나의 클래스에 여러 개의 method가 있을 경우 어떻게 처리할 것인지?

    /**
     * Collect project and psi file from given context
     * @param e
     */
    private NavigatePsi(AnActionEvent e)
    {
        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE); // ? look for only currently opened file

        try
        {
            focusClass = ((PsiClassOwner)focusFile).getClasses()[0];
            focusMethod = focusClass.getMethods()[0];
        }catch(ArrayIndexOutOfBoundsException exception)
        {
            // NO class in current file
            focusClass = null;
        }
    }

    /**
     * factory for navigator
     * @param e
     * @return NavigatePsi
     */
    public static NavigatePsi NavigatorFactory(AnActionEvent e)
    {
        if(navigator==null){ navigator = new NavigatePsi(e); }
        else if(focusFile!=e.getData(LangDataKeys.PSI_FILE)){ navigator = new NavigatePsi(e); }

        return navigator;
    }

    /**
     * Returns list of private members from focused class
     * @return list of private fields
     */
    public List<PsiField> findPrivateField()
    {
        List<PsiField> ret = new ArrayList<>();

        for(PsiField f : focusClass.getFields())
        {
            if(f.getModifierList().hasModifierProperty("private")){ ret.add(f); }
        }

        return ret;
    }

    /**
     * Check whether methods with given names are already implemented in class
     * @param methods List of method names in string
     * @return names of methods that haven't implemented in current class
     *         if all entries of methods are already implemented, return empty list
     */
    public List<String> findMethod(List<String> methods)
    {
        List<String> ret = methods;
        for(PsiMethod m: focusClass.getMethods())
        {
            if(methods.contains(m.getName()))
            {
                ret.remove(m.getName());
            }
        }

        return ret;
    }

    /* return currently open project */
    public Project findProject(){ return focusProject; }

    /* return currently open file */
    public PsiFile findFile(){ return focusFile; }

    /* return currently class of current file*/
    public PsiClass findClass(){ return focusClass; }

    /* return first method of focus class
    *  TODO: 추후 여러 개의 method에 대응하는 방법을 고려해야 할듯
    * */
    public PsiMethod getMethod(){ return focusMethod; }
}
