/**
 * Class to find specific Psi element in given context.
 *
 * @author seha park
 * @author Mintae Kim
 */
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
 * Class to find specific Psi element in given context
 * @author seha Park
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
     * Collect reference expression from given element
     * @author CSED332 2019 Team 1
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

    /**
     * Return the List containing PsiMethodCallExpression Object in current PSI Element
     * @author Chanyoung Kim
     * @param element the PSI Element.
     * @return List<PsiMethodCallExpression> if element has MethodCallExpressions, empty() otherwise
     */
    public static List<PsiMethodCallExpression> findPsiMethodCallExpressions(PsiElement element) {
        List<PsiMethodCallExpression> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                result.add(expression);
            }
        });
        return result;
    }

    /**
     * Return PsiIfstatement from cursor offset inside of PsiClass
     * @author seungjae yoo
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
     * Return the List containing PsiClass Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiClass> if element has PsiClass, empty() otherwise
     */
    public static List<PsiClass> findPsiClasses(PsiElement element) {
        List<PsiClass> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(PsiClass c) {
                super.visitClass(c);
                result.add(c);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiField Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiField> if element has PsiField, empty() otherwise
     */
    public static List<PsiField> findPsiFields(PsiElement element) {
        List<PsiField> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(PsiField elem) {
                super.visitField(elem);
                result.add(elem);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiMethod Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiMethod> if element has PsiMethod, empty() otherwise
     */
    public static List<PsiMethod> findPsiMethods(PsiElement element) {
        List<PsiMethod> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod m) {
                super.visitMethod(m);
                result.add(m);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiDeclarationStatement Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiDeclarationStatement> if element has PsiDeclarationStatement, empty() otherwise
     */
    public static List<PsiDeclarationStatement> findPsiDeclarationStatements(PsiElement element) {
        List<PsiDeclarationStatement> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement element) {
                super.visitDeclarationStatement(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiExpression Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiExpression> if element has PsiExpression, empty() otherwise
     */
    public static List<PsiExpression> findPsiExpressions(PsiElement element) {
        List<PsiExpression> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitExpression(PsiExpression element) {
                super.visitExpression(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiExpression Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiExpression> if element has PsiExpression, empty() otherwise
     */
    public static List<PsiExpression> findChildPsiExpressions(PsiElement element) {
        List<PsiExpression> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiExpression) result.add((PsiExpression) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiLocalVariable Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiLocalVariable> if element has PsiLocalVariable, empty() otherwise
     */
    public static List<PsiLocalVariable> findPsiLocalVariables(PsiElement element) {
        List<PsiLocalVariable> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(PsiLocalVariable element) {
                super.visitLocalVariable(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiLocalVariable Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiLocalVariable> if element has PsiLocalVariable, empty() otherwise
     */
    public static List<PsiLocalVariable> findChildPsiLocalVariables(PsiElement element) {
        List<PsiLocalVariable> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiLocalVariable) result.add((PsiLocalVariable) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiTypeElement Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiTypeElement> if element has PsiTypeElement, empty() otherwise
     */
    public static List<PsiTypeElement> findPsiTypeElements(PsiElement element) {
        List<PsiTypeElement> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitTypeElement(PsiTypeElement element) {
                super.visitTypeElement(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiTypeElement Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiTypeElement> if element has PsiTypeElement, empty() otherwise
     */
    public static List<PsiTypeElement> findChildPsiTypeElements(PsiElement element) {
        List<PsiTypeElement> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiTypeElement) result.add((PsiTypeElement) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiNewExpression Object in current PsiElement
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiNewExpression> if element has PsiNewExpression, empty() otherwise
     */
    public static List<PsiNewExpression> findPsiNewExpressions(PsiElement element) {
        List<PsiNewExpression> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitNewExpression(PsiNewExpression element) {
                super.visitNewExpression(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiNewExpression Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiNewExpression> if element has PsiNewExpression, empty() otherwise
     */
    public static List<PsiNewExpression> findChildPsiNewExpressions(PsiElement element) {
        List<PsiNewExpression> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiNewExpression) result.add((PsiNewExpression) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiJavaCodeReferenceElement Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiJavaCodeReferenceElement> if element has PsiJavaCodeReferenceElement, empty() otherwise
     */
    public static List<PsiJavaCodeReferenceElement> findChildPsiJavaCodeReferenceElements(PsiElement element) {
        List<PsiJavaCodeReferenceElement> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiJavaCodeReferenceElement) result.add((PsiJavaCodeReferenceElement) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiKeyword Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiKeyword> if element has PsiKeyword, empty() otherwise
     */
    public static List<PsiKeyword> findChildPsiKeywords(PsiElement element) {
        List<PsiKeyword> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiKeyword) result.add((PsiKeyword) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiExpressionList Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiExpressionList> if element has PsiExpressionList, empty() otherwise
     */
    public static List<PsiExpressionList> findChildPsiExpressionLists(PsiElement element) {
        List<PsiExpressionList> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiExpressionList) result.add((PsiExpressionList) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiReferenceExpression Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiReferenceExpression> if element has PsiReferenceExpression, empty() otherwise
     */
    public static List<PsiReferenceExpression> findChildPsiReferenceExpressions(PsiElement element) {
        List<PsiReferenceExpression> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiReferenceExpression) result.add((PsiReferenceExpression) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiBinaryExpression Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiBinaryExpression> if element has PsiBinaryExpression, empty() otherwise
     */
    public static List<PsiBinaryExpression> findChildPsiBinaryExpressions(PsiElement element) {
        List<PsiBinaryExpression> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiBinaryExpression) result.add((PsiBinaryExpression) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiLiteralExpression Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiLiteralExpression> if element has PsiLiteralExpression, empty() otherwise
     */
    public static List<PsiLiteralExpression> findChildPsiLiteralExpressions(PsiElement element) {
        List<PsiLiteralExpression> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiLiteralExpression) result.add((PsiLiteralExpression) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiIdentifier Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiIdentifier> if element has PsiIdentifier, empty() otherwise
     */
    public static List<PsiIdentifier> findChildPsiIdentifiers(PsiElement element) {
        List<PsiIdentifier> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiIdentifier) result.add((PsiIdentifier) elem);
        }
        return result;
    }

    /**
     * Return the List containing PsiJavaToken Object in current PsiElement children
     * @author Chanyoung Kim
     * @param element the PsiElement.
     * @return List<PsiJavaToken> if element has PsiJavaToken, empty() otherwise
     */
    public static List<PsiJavaToken> findChildPsiJavaTokens(PsiElement element) {
        List<PsiJavaToken> result = new ArrayList<>();
        for (PsiElement elem : element.getChildren()) {
            if (elem instanceof PsiJavaToken) result.add((PsiJavaToken) elem);
        }
        return result;
    }

}

