package wanted.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to find specific Psi element in given context.
 * @author seha Park
 * @author Mintae Kim
 * @author JINU NOH
 * @author chanyoung Kim
 * @author seungjae yoo
 * @author CSED332 2019 Team 1
 */
public class FindPsi {

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

                Set<PsiReferenceExpression> refers = findReferenceExpression(s);
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
     * method that returns set of parameters that passed to the method
     * 
     * @param focusMethod : 검사하고 싶은 메소드 (PsiMethod)
     * @return set of unused parameters 
     */
    public static Set<PsiParameter> findParametersOfMethod(@NotNull PsiMethod focusMethod) {
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
     * @param focusElement : 검사하고 싶은 요소 (PsiElement)
     * @return set of used reference in PsiElement
     */
    public static Set<PsiReferenceExpression> findReferenceExpression(PsiElement focusElement) {
        Set<PsiReferenceExpression> result = new HashSet<>();
        focusElement.accept((new JavaRecursiveElementVisitor() {
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

    /**
     * retrieve member field from caret
     * @param f PsiFile context
     * @param e action event
     * @return PsiField
     */
    public static PsiField findMemberByCaret(PsiFile f, AnActionEvent e)
    {
        PsiField ret;
        int caret = e.getData(CommonDataKeys.EDITOR).getCaretModel().getOffset();
        ret = PsiTreeUtil.getParentOfType(f.findElementAt(caret), PsiField.class);
        return ret;
    }

    public static PsiClass getContainingClass (PsiMethod method) {
        PsiElement targetClass = method;
        while (!(targetClass instanceof PsiClass)) {
            targetClass = targetClass.getParent();
            if (targetClass == null)
                return null;
        }
        return (PsiClass)targetClass;
    }
}

