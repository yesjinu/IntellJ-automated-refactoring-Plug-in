package wanted.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.sun.istack.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
     * append at end of existing fields
     *
     * @param targetClass class to modify
     * @param addList     PsiFields to add, element of list will be appended in order
     */
    public static void addField(@NotNull PsiClass targetClass, @NotNull List<PsiField> addList) {
        PsiElement element;

        List<PsiField> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(targetClass.getFields()));

        Collections.reverse(addList);

        for (PsiElement e : addList) {
            if (fields.size() == 0) {
                element = targetClass.getLBrace();
            } else {
                element = fields.get(fields.size() - 1);
            }

            targetClass.addAfter(e, element);
        }
    }
}
