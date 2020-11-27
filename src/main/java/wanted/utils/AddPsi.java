package wanted.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.sun.istack.NotNull;

import java.util.List;

/**
 * Class to add Psi Elements.
 *
 * @author seha Park
 */
public class AddPsi {

    /**
     * add all elements of addList to target class
     * append at end of class
     * 
     * @param targetClass class to modify
     * @param addList elements to add
     */
    public static void addMethod(@NotNull PsiClass targetClass, @NotNull List<PsiElement> addList)
    {
        for(PsiElement e :addList)
        {
            targetClass.addBefore(e, targetClass.getLastChild());
        }
    }
}
