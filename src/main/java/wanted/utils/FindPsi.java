package wanted.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to find specific Psi element in given context.
 *
 * @author seha Park
 * @author Mintae Kim
 * @author JINU NOH
 * @author chanyoung Kim
 * @author seungjae yoo
 * @author CSED332 2019 Team 1
 */
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
     * @param focusClass search scope
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
     * Find reference expression which refers given member
     * search scope: directory of file. i.e, only check files in same package
     * @param file the file which own class with member field
     * @param member PsiField to find reference
     * @return
     */
    public static List<PsiReferenceExpression> findMemberReference(PsiFile file, PsiField member)
    {
        List<PsiReferenceExpression> ret = new ArrayList<>();

        List<PsiFile> files = Arrays.asList(file.getContainingDirectory().getFiles());

        for(PsiFile f : files)
        {
            if(f.equals(file)){ continue; } // do not check itself
            else
            {
                PsiClass[] classes;
                if(f instanceof PsiClassOwner)
                {
                    classes = ((PsiClassOwner)f).getClasses();
                    for(PsiClass c : classes)
                    {
                        ret.addAll(findMemberReference(c, member));
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Collect reference expression from given element
     * 
     * @param statement Psi element to check
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
     * 
     * @param focusMethod : 검사하고 싶은 메소드 (PsiMethod)
     * @return set of unused parameters 
     */
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
     * 
     * @param focusMethod : 검사하고 싶은 메소드 (PsiMethod)
     * @return set of used reference in method
     */
    public static Set<PsiReferenceExpression> findReferenceUsedInMethod(PsiMethod focusMethod) {
        Set<PsiReferenceExpression> result = new HashSet<>();
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
     * Return the List containing PsiMethodCallExpression Object in current PSI Element
     * 
     * @param element the PSI Element.
     * @return List<PsiMethodCallExpression> if element has MethodCallExpressions, empty() otherwise
     */
    public static List<PsiMethodCallExpression> findPsiMethodCallExpression(PsiElement element) {
        List<PsiMethodCallExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Return PsiIfstatement from cursor offset inside of PsiClass
     * 
     * @param psiClass
     * @param offset
     * @return PsiIfStatement which contains cursor
     *         If various PsiIfStatements are correct, choose narrowest one
     */
    public static PsiIfStatement findIfStatement(PsiClass psiClass, int offset)
    {
        List<PsiIfStatement> ifStatementList = new ArrayList<>();

        JavaRecursiveElementVisitor v = new JavaRecursiveElementVisitor(){
                @Override
                public void visitIfStatement(PsiIfStatement statement)
                {
                    if(statement.getTextRange().contains(offset)) ifStatementList.add(statement);
                    super.visitIfStatement(statement);
                }
        };
        psiClass.accept(v);

        if (ifStatementList.isEmpty()) return null;
        else return ifStatementList.get(ifStatementList.size()-1);
    }
    /**
     * Searching for every subclasses
     *
     * @param superclass Superclass
     * @param classList List of all classes in project
     * @return List of all subclasses extends superclass
     */
    public static List<PsiClass> findEverySubClass (PsiClass superclass, List<PsiClass> classList) {
        List<PsiClass> subclassList = new ArrayList<>();
        for (PsiClass psiClass : classList)
            if (Arrays.asList(psiClass.getSupers()).contains(superclass))
                subclassList.add(psiClass);
        return subclassList;
    }
}

