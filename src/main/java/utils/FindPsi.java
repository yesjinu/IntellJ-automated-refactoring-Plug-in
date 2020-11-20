/**
 * Class to find specific Psi element in given context.
 *
 * @author seha park
 * @author Mintae Kim
 */
package utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindPsi {
    private Project focusProject;
    private PsiFile focusFile;
    private PsiClass focusClass;
    private PsiMethod focusMethod;

    /* get focusProject, File, Class from given event */
    public FindPsi(AnActionEvent e) {
        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE);
        // assume file always contains one class which has only one method
        assert focusFile != null;
        focusClass = ((PsiClassOwner) focusFile).getClasses()[0];
        focusMethod = focusClass.getMethods()[0];
    }

    /**
     * Returns list of statements referring to given member
     *
     * @param member
     * @return list of statements
     */
    public static List<PsiReferenceExpression> findMemberReference(PsiClass focusClass, PsiField member) {
        List<PsiReferenceExpression> ret = new ArrayList<>();
        for (PsiMethod m : focusClass.getMethods()) {
            PsiCodeBlock c = m.getBody();
            if (c == null) {
                return ret;
            } // no code block

            for (PsiStatement s : c.getStatements()) {
                if (!s.getText().contains(member.getName())) {
                    continue;
                }

                List<PsiReferenceExpression> refers = findReference(s);
                for (PsiReferenceExpression r : refers) {
                    if (r.isReferenceTo(member)) {
                        ret.add(r);
                    }
                }

            }
        }

        return ret;
    }

    /**
     * Collect reference expression from given statement
     * from 2019 Team 1 example
     *
     * @param statement
     * @return PsiReferenceExpression in given statement
     */
    public static List<PsiReferenceExpression> findReference(PsiStatement statement) {
        List<PsiReferenceExpression> ret = new ArrayList<>();
        statement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * method that returns set of parameters that passed to the method
     * @param focusMethod : 검사하고 싶은 메소드 (PsiMethod)
     * @return set of unused parameters
     * @author : JINU NOH
     * */
    public static Set<PsiParameter> findParametersOfMethod(PsiMethod focusMethod) {
        Set<PsiParameter> result = new HashSet<>();

        // assume class always contains one field
        if (focusMethod.hasParameters()) {
            result.addAll(Arrays.asList(focusMethod.getParameterList().getParameters()));
        }
        return result;
    }


    /**
     * returns set of reference expressions(symbols) in a method
     * @param focusMethod : 검사하고 싶은 메소드 (PsiMethod)
     * @return set of used reference in method
     * @author : JINU NOH
     * */
    public static Set<PsiReference> findReferenceUsedInMethod(PsiMethod focusMethod) {
        Set<PsiReference> result = new HashSet<>();
        focusMethod.accept((new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                result.add(expression);
            }
        }));
        return result;
    }

    /**
     * Searching for every
     */
    // TODO: Implement Someting @seha park

    /**
     * Searching for every subclasses
     *
     * @param superclass Superclass
     * @param classList  List of all classes in project
     * @return List of all subclasses extends superclass
     */
    public static List<PsiClass> findEverySubClass(PsiClass superclass, List<PsiClass> classList) {
        List<PsiClass> subclassList = new ArrayList<>();
        for (PsiClass psiClass : classList)
            if (Arrays.asList(psiClass.getSupers()).contains(superclass))
                subclassList.add(psiClass);
        return subclassList;
    }
}

