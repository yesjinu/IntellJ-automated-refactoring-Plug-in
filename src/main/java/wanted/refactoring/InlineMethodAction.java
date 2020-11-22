package wanted.refactoring;

import com.intellij.find.findUsages.FindMethodUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.find.findUsages.FindUsagesHandlerUi;
import com.intellij.ide.hierarchy.method.MethodHierarchyTreeStructure;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.MethodSignatureUtil;
import org.jetbrains.annotations.NotNull;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;
import wanted.utils.TraverseProjectPsi;

import java.util.*;

public class InlineMethodAction extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;
    private PsiField member;
    private PsiMethod method;
    private List<PsiReferenceExpression> statements;

    /**
     * Returns the story name as a string format, for message.
     * @return story name "Inline Method"
     */
    @Override
    public String storyName() {
        return "Inline Method";
    }

    /**
     * Returns the possibility of refactoring for current project with particular strategy.
     * @param e An Actionevent
     * @return true if refactoring is available, otherwise false.
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();

        targetClass = navigator.findClass();
        if (targetClass == null) return false;

        method = navigator.findFocusMethod();
        if (method == null) return false;

        return isCandidate(method);
    }

    /**
     * Method that performs refactoring.
     */
    @Override
    protected void refactor(AnActionEvent e) {
        assert isCandidate (method);

        List<PsiReference> references = new ArrayList<>(ReferencesSearch.search(method).findAll());

        // Fetching element to replace
        PsiStatement removeMethodStatement = method.getBody().getStatements()[0];
        PsiElement replaceElement;
        boolean insert;

        if (PsiType.VOID.equals(method.getReturnType())) {
            replaceElement = removeMethodStatement;
            insert = isInsertStatement(removeMethodStatement);
        }
        else {
            replaceElement = ((PsiReturnStatement) removeMethodStatement).getReturnValue();
            insert = true;
        }

        assert replaceElement != null;

        if (insert) {
            // Fetching Method Parameter: Replace
            PsiParameterList paramList = method.getParameterList();
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement().getParent();

                assert refElement instanceof PsiMethodCallExpression;
                PsiExpressionList paramRefList = ((PsiMethodCallExpression)refElement).getArgumentList();

                // Replace & Delete
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Replace statement
                    refElement.replace(replaceElement);
                    // replace vars in replaceElement with Map paramList -> paramRefList
                    ReplacePsi.replaceParamToArgs(refElement, paramList, paramRefList);
                });
            }
        }
        else {
            // Remove
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement();
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    // Replace statement
                    refElement.delete();
                });
            }
        }
        // Delete Original Method
        WriteCommandAction.runWriteCommandAction(project, () -> {
            method.delete();
        });
    }


    /**
     * Helper method that checks whether statement in method needs to be inserted while wanted.refactoring.
     *
     * @return true if statement needs insertion.
     */
    private boolean isInsertStatement(PsiStatement statement) {
        if (statement instanceof PsiAssertStatement ||
                statement instanceof PsiThrowStatement ||
                statement instanceof PsiExpressionStatement ||
                statement instanceof PsiYieldStatement ||
                statement instanceof PsiSynchronizedStatement)
            return true;
        else if (statement instanceof PsiIfStatement)
            return isInsertStatement(((PsiIfStatement) statement).getThenBranch()) ||
                    isInsertStatement(((PsiIfStatement) statement).getElseBranch());
        else if (statement instanceof PsiLabeledStatement)
            return isInsertStatement(((PsiLabeledStatement) statement).getStatement());
        else if (statement instanceof PsiLoopStatement)
            return isInsertStatement(((PsiLoopStatement) statement).getBody());
        else if (statement instanceof PsiSwitchStatement){
            if (((PsiSwitchStatement) statement).getBody() == null) return false;

            for (PsiStatement innerStatement : ((PsiSwitchStatement) statement).getBody().getStatements()) {
                if (isInsertStatement(innerStatement) ||
                        innerStatement instanceof PsiBreakStatement ||
                        innerStatement instanceof PsiContinueStatement)
                    return true;
            }
            return false;
        }
        else if (statement instanceof PsiBlockStatement){
            for (PsiStatement innerStatement : ((PsiBlockStatement) statement).getCodeBlock().getStatements()) {
                if (isInsertStatement(innerStatement) ||
                        innerStatement instanceof PsiBreakStatement ||
                        innerStatement instanceof PsiContinueStatement)
                    return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Helper method that checks whether candidate method is refactorable using 'Inline Method'.
     *
     * Every candidate methods should follow these two requisites:
     * 1. Methods which is not defined in subclasses
     * 2. Methods with 1 statement.
     *
     * @return true if method is refactorable
     */
    private boolean isCandidate(@NotNull PsiMethod method) {
        // MethodHierarchyTreeStructure treeStructure = new MethodHierarchyTreeStructure(project, method, null);
        List<PsiClass> subclassList =
                new ArrayList<>(
                    ClassInheritorsSearch.search(targetClass, GlobalSearchScope.allScope(project), false).findAll());

        for (PsiClass subclass : subclassList) {
            for (PsiMethod method_sub : subclass.getMethods()){
                if (MethodSignatureUtil.areSignaturesEqual(method, method_sub))
                    return false;
            }

            PsiCodeBlock body = method.getBody();
            if (body == null) return false;

            // Choosing Methods with One Statement
            if (body.getStatementCount() > 1) return false;
        }

        return true;
    }

}
