package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.MethodSignatureUtil;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.*;

/**
 * Class to provide refactoring: 'Inline Method'
 *
 * @author Mintae Kim
 */
public class InlineMethod extends BaseRefactorAction {
    protected Project project;
    protected PsiMethod method;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "IM";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Inline Method";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When a method body is more obvious than the method itself, <br/>" +
                "Replace calls to the method with the method's content and delete the method itself.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>Make sure that the method isn't redefined in subclasses. <br/>" +
                "If the method is redefined, You cannot apply this technique.</html>";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Inline Method'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();

        method = navigator.findMethod();
        if (method == null) return false;

        return refactorValid(project, method);
    }

    /**
     * Static method that checks whether candidate method is refactorable using 'Inline Method'.
     *
     * Every candidate methods should follow these three requisites:
     * 1. Refactorable method is not a constructor
     * 2. Refactorable method is not defined in subclasses
     * 3. Refactorable method has only one statement in its body.
     *
     * @param project Project
     * @param method PsiMethod
     * @return true if method is refactorable
     */
    public static boolean refactorValid(Project project, PsiMethod method) {
        // 0. Check whether method is null
        if (method == null) return false;

        // 1. Constructor is not refactorable
        if (method.isConstructor()) return false;

        // 2. Check whether method is redefined in subclasses
        PsiClass targetClass = FindPsi.getContainingClass(method);
        if (targetClass == null) return false;

        List<PsiClass> subclassList;

        // MethodHierarchyTreeStructure treeStructure = new MethodHierarchyTreeStructure(project, method, null);
        try {
            subclassList =
                    new ArrayList<>(
                            ClassInheritorsSearch.search(targetClass, GlobalSearchScope.allScope(project), false).findAll());
        } catch (IndexNotReadyException e) {
            return false;
        }

        for (PsiClass subclass : subclassList) {
            for (PsiMethod method_sub : subclass.getMethods()){
                if (MethodSignatureUtil.areSignaturesEqual(method, method_sub))
                    return false;
            }
        }

        // 3. Choosing Methods with One Statement
        return isOneStatement(method);
    }

    /**
     * Method that performs refactoring: 'Inline Method'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        assert refactorValid (project, method);

        Comparator<PsiReference> comparator = new Comparator<PsiReference>() {
            @Override
            public int compare(PsiReference a, PsiReference b) {
                return a.getElement().getParent().getText().compareTo(b.getElement().getParent().getText());
            }
        };

        List<PsiReference> references = new ArrayList<>(ReferencesSearch.search(method).findAll());
        Collections.sort(references, comparator);

        if (!references.isEmpty()) {

            // Fetching element to replace
            PsiStatement methodStatementOrigin = method.getBody().getStatements()[0];
            // Fetching Method Parameter: Replace
            PsiParameterList paramList = method.getParameterList();

            for (PsiReference reference : references) {
                // Copy Statement from origin
                PsiStatement methodStatement = CreatePsi.copyStatement(project, methodStatementOrigin);

                // Fetching Replace Element
                PsiElement replaceElement = fetchReplaceElement(methodStatement);
                if (replaceElement == null) continue;

                // Fetching Reference Element
                PsiElement refElement = reference.getElement().getParent();
                assert refElement instanceof PsiMethodCallExpression;

                // Step 3. Replace Parameters (Be Aware of DummyHolder)
                replaceElement =
                        replaceParameters((PsiMethodCallExpression)refElement,
                                null, replaceElement,
                                paramList, null);

                // Step 4. Insert Statement
                insertStatements((PsiMethodCallExpression)refElement, null, replaceElement);
            }
        }

        // Delete Original Method
        WriteCommandAction.runWriteCommandAction(project, () -> {
            method.delete();
        });
    }

    /**
     * Method that checks if method has only one statement
     *
     * @param method PsiMethod
     * @return true if method has only one statement, otherwise false.
     */
    private static boolean isOneStatement(PsiMethod method) {
        PsiCodeBlock body = method.getBody();
        if (body == null) return false;

        if (body.getStatementCount() > 1) return false;
        PsiStatement statement = body.getStatements()[0];

        final boolean[] check = new boolean[1];
        check[0] = true;
        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitBreakStatement(PsiBreakStatement statement) { check[0] &= true; }
            @Override
            public void visitContinueStatement(PsiContinueStatement statement) { check[0] &= true; }
            @Override
            public void visitEmptyStatement(PsiEmptyStatement statement) { check[0] &= true; }

            @Override
            public void visitAssertStatement(PsiAssertStatement statement) { check[0] &= true; }
            @Override
            public void visitYieldStatement(PsiYieldStatement statement) { check[0] &= true; }
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) { check[0] &= true; }
            @Override
            public void visitReturnStatement(PsiReturnStatement statement) { check[0] &= true; }
            @Override
            public void visitThrowStatement(PsiThrowStatement statement) { check[0] &= true; }

            @Override
            public void visitExpressionListStatement(PsiExpressionListStatement statement) { check[0] &= true; }
            @Override
            public void visitExpressionStatement(PsiExpressionStatement statement) { check[0] &= true; }
            @Override
            public void visitCodeBlock(PsiCodeBlock block) {
                super.visitElement(block);
                check[0] &= (block.getStatementCount() <= 1);
            }
            @Override
            public void visitBlockStatement(PsiBlockStatement statement) { super.visitBlockStatement(statement); }

            @Override
            public void visitIfStatement(PsiIfStatement statement) { super.visitIfStatement(statement); }

            @Override
            public void visitDoWhileStatement(PsiDoWhileStatement statement) { super.visitStatement(statement.getBody()); }
            @Override
            public void visitWhileStatement(PsiWhileStatement statement) { super.visitWhileStatement(statement); }
            @Override
            public void visitForStatement(PsiForStatement statement) { super.visitForStatement(statement); }
            @Override
            public void visitForeachStatement(PsiForeachStatement statement) { super.visitForeachStatement(statement); }
            @Override
            public void visitTryStatement(PsiTryStatement statement) { super.visitTryStatement(statement); }

            @Override
            public void visitSwitchStatement(PsiSwitchStatement statement) { super.visitSwitchStatement(statement); }
            @Override
            public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) { super.visitSwitchLabelStatement(statement); }
        };

        statement.accept(visitor);

        return check[0];
    }

    /**
     * Method that fetch value of return statement
     *
     * @param statement PsiStatement
     * @return the return value of the statement if the statement is instance of PsiReturnStatement,
     * otherwise just return the statement itself.
     */
    protected PsiElement fetchReplaceElement(PsiStatement statement) {
        if (statement instanceof PsiReturnStatement) // Return Values
            return ((PsiReturnStatement) statement).getReturnValue();
        else
            return statement;
    }

    /**
     * Method that raplace variables in replaceElement and declarations with newParamArray
     *
     * @param reference PsiMethodCallExpression
     * @param declarations List<PsiStatement>
     * @param replaceElement PsiElement
     * @param paramList PsiParameterList
     * @param newParamArray PsiExpression[]
     * @return replaced PsiElement
     */
    protected PsiElement replaceParameters(PsiMethodCallExpression reference,
                                           List<PsiStatement> declarations, PsiElement replaceElement,
                                           PsiParameterList paramList, PsiExpression[] newParamArray) {

        PsiExpressionList paramRefList = reference.getArgumentList();

        PsiElement afterReplaceElement;
        // replace vars in replaceElement with Map paramList -> paramRefList
        if (newParamArray == null) {
            afterReplaceElement = replaceElement.replace(
                    ReplacePsi.replaceParamToArgs(project, replaceElement,
                            paramList, paramRefList));
        }
        else {
            afterReplaceElement = replaceElement.replace(
                    ReplacePsi.replaceParamToArgs(project, replaceElement,
                            paramList.getParameters(), newParamArray));
        }

        // replace vars in declaration with Map paramList -> paramRefList
        if (declarations != null) {
            // Modify Parameters for declarations
            for (int i = 0; i < declarations.size(); i++) {
                PsiStatement declaration = declarations.get(i);
                declarations.set(i,
                        (PsiStatement) (declaration.replace
                                (ReplacePsi.replaceParamToArgs(project, declaration,
                                        paramList, paramRefList))));
            }
        }

        return afterReplaceElement;
    }

    /**
     * Method that insert declaration to the reference
     *
     * @param reference PsiMethodCallExpression
     * @param declarations List<PsiStatement>
     * @param replaceElement PsiElement
     */
    protected void insertStatements(PsiMethodCallExpression reference,
                                    List<PsiStatement> declarations, PsiElement replaceElement) {

        // Replace Statement
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Removal of Semicolon for Void Type
            if (reference.getType() != null && reference.getType().equals(PsiType.VOID))
                    reference.getNextSibling().delete();

            // 1. Replace replaceElement
            PsiElement anchor = reference.replace(replaceElement);

            // 2. Insert Declarations
            // Fetch the Widest Statement or Expression
            while (!(anchor.getParent().getParent() instanceof PsiClass ||
                    anchor.getParent().getParent() instanceof PsiMethod))
                anchor = anchor.getParent();

            if (declarations != null) {
                final PsiElement newLineNode =
                        PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n");
                for (PsiStatement declaration : declarations) {
                    // Insert AssignExp
                    anchor.getParent().addBefore(declaration, anchor);

                    // New Line
                    anchor.getParent().addBefore(newLineNode, anchor);
                }
            }
        });
    }
}
