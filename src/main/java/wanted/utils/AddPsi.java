package wanted.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

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
     * @param addList     elements to add
     */
    public static void addMethod(@NotNull PsiClass targetClass, @NotNull List<PsiElement> addList) {
        for (PsiElement e : addList) {
            targetClass.addBefore(e, targetClass.getLastChild());
        }
    }


    /**
     * add all PsiField of addList to target class
     * append before existing fields
     *
     * @param targetClass class to modify
     * @param addList     PsiFields to add, element of list will be appended in order
     */
    public static void addField(@NotNull PsiClass targetClass, @NotNull List<PsiField> addList) {
        PsiElement element = targetClass.getLBrace();

        for (int i = 0; i < addList.size(); i++) {
            targetClass.addAfter(addList.get(i), element);
            element = targetClass.getFields()[i];
        }
    }
}
