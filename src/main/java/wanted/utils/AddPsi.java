package wanted.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.sun.istack.NotNull;

import java.util.List;

/**
 * Class to add Psi Elements.
 *
 * @author seha park
 */
public class AddPsi {
    /**
     * add all methods of addList to target class in order
     * append at end of class
     *
     * @param targetClass
     * @param addList
     */
    public static void addMethod(@NotNull PsiClass targetClass, @NotNull List<PsiElement> addList)
    {
        for(PsiElement e :addList)
        {
            targetClass.addBefore(e, targetClass.getLastChild());
        }
    }
}
