package wanted.utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to find specific Psi element in given context.
 *
 * @author seha Park
 * @author Mintae Kim
 * @author Jinu Noh
 * @author Chanyoung Kim
 * @author seungjae yoo
 * @author CSED332 2019 Team 1
 */
public class FindPsi {
    /**
     * Returns list of statements referring to given member
     *
     * @param focusClass search scope
     * @param member     PsiField object
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
     * Find PsiReferenceExpression in same directory PsiFiles (not itself)
     * Search scope: directory of file. i.e, only check files in same package
     *
     * @param file   the file which own class with member field
     * @param member PsiField to find reference
     * @return List<PsiReferenceExpression> if PsiFiles  has PsiReferenceExpression, empty() otherwise
     */
    public static List<PsiReferenceExpression> findMemberReference(PsiFile file, PsiField member) {
        List<PsiReferenceExpression> ret = new ArrayList<>();

        List<PsiFile> files = Arrays.asList(file.getContainingDirectory().getFiles());

        for (PsiFile f : files) {
            if (f.equals(file)) {
                continue;
            } // do not check itself
            else {
                PsiClass[] classes;
                if (f instanceof PsiClassOwner) {
                    classes = ((PsiClassOwner) f).getClasses();
                    for (PsiClass c : classes) {
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
    public static List<PsiMethodCallExpression> findPsiMethodCallExpressions(PsiElement element) {
        List<PsiMethodCallExpression> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression elem) {
                super.visitMethodCallExpression(elem);
                result.add(elem);
            }
        });
        return result;
    }

    /**
     * Return PsiIfstatement from cursor offset inside of PsiClass
     *
     * @param psiClass the scope this function find ifstatement
     * @param offset   the text offset of file containing this class
     * @return PsiIfStatement which contains cursor
     * If various PsiIfStatements are correct, choose narrowest one
     */
    public static PsiIfStatement findIfStatement(PsiClass psiClass, int offset) {
        List<PsiIfStatement> ifStatementList = new ArrayList<>();

        JavaRecursiveElementVisitor v = new JavaRecursiveElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement statement) {
                if (statement.getTextRange().contains(offset)) ifStatementList.add(statement);
                super.visitIfStatement(statement);
            }
        };
        psiClass.accept(v);

        if (ifStatementList.isEmpty()) return null;
        else return ifStatementList.get(ifStatementList.size() - 1);
    }

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

    public static PsiClass getContainingClass(PsiMethod method) {
        PsiElement targetClass = method;
        while (!(targetClass instanceof PsiClass)) {
            targetClass = targetClass.getParent();
            if (targetClass == null)
                return null;
        }
        return (PsiClass) targetClass;
    }

    /**
     * Return containing class of element
     *
     * @param element PsiElement to find containing class
     * @return PsiClass, or null if there's no containing class
     */
    public static @Nullable PsiClass getContainingClass(@NotNull PsiElement element) {
        PsiElement targetClass = element;
        while (!(targetClass instanceof PsiClass)) {
            targetClass = targetClass.getParent();
            if (targetClass == null)
                return null;
        }

        return (PsiClass)targetClass;
    }

    /**
     * Return the List containing PsiClass Object in current PsiElement
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     * Return the List containing PsiLiteralExpression in current PsiElement
     *
     * @param element search scope
     * @return List<PsiLiteralExpression> if element has PsiLiteralExpression, empty() otherwise
     */
    public static List<PsiLiteralExpression> findPsiLiteralExpressions(PsiElement element) {
        List<PsiLiteralExpression> result = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLiteralExpression(PsiLiteralExpression element) {
                super.visitLiteralExpression(element);
                result.add(element);
            }
        });
        return result;
    }

    /**
     * Return the List containing PsiLiteralExpression Object in current PsiElement children
     *
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
     *
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
     *
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

    /**
     * For each query in queries,
     * if there's method with same name in searchClass, remove the query from list
     * return the modified list such that queries with duplicate name are removed
     *
     * @param searchClass PsiClass to check
     * @param queries     list of strings which represents names of methods to check
     * @return modified queries such that duplicate strings are removed
     */
    public static List<String> checkDuplicateName(PsiClass searchClass, List<String> queries) {
        if(searchClass==null){ return new ArrayList<>(); }

        List<String> ret = queries;

        for (PsiMethod m : searchClass.getMethods()) {
            if (queries.contains(m.getName())) {
                ret.remove(m.getName());
            }
        }

        return ret;
    }

    /**
     * Return list with same literal value in current PsiElement
     *
     * @param element search scope
     * @param literal value to find
     * @return List<PsiLiteralExpression> that both value and type matches with literal, which contains itself
     * @note short, byte, long with value ~2^31 ~ 2^31-1, and char with value ~2^31 ~ 2^31-1 are treated as Integer
     */
    public static List<PsiLiteralExpression> findLiteralUsage(@NotNull PsiElement element, @NotNull PsiLiteralExpression literal) {
        List<PsiLiteralExpression> expressions = findPsiLiteralExpressions(element);
        List<PsiLiteralExpression> ret = new ArrayList<>();

        for (PsiLiteralExpression l : expressions) {
            Object value = l.getValue();
            if(value!=null && value.equals(literal.getValue()) && l.getType().equals(literal.getType()))
            {
                ret.add(l);
            }
        }

        return ret;
    }
}

