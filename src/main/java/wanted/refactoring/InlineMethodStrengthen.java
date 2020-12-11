package wanted.refactoring;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLocalVariableImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.MethodSignatureUtil;
import org.jetbrains.annotations.NotNull;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Inline Method (Strengthen)'
 *
 * @author Mintae Kim
 */
public class InlineMethodStrengthen extends InlineMethod {

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "IMS";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Inline Method (Strengthen)";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String descripton() {
        // TODO: ADD
        return "<html>When a method body is more obvious than the method itself, <br/>" +
                "Replace calls to the method with the method's content and delete the method itself.</html>";
    }

    /**
     * Method that performs refactoring: 'Inline Method (Strengthen)'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e) {
        assert InlineMethod.refactorValid (project, method);

        List<PsiReference> references = new ArrayList<>(ReferencesSearch.search(method).findAll());

        if (!references.isEmpty()) {
            // Fetching element to replace
            PsiStatement methodStatementOrigin = method.getBody().getStatements()[0];
            PsiStatement methodStatement = CreatePsi.copyStatement(project, methodStatementOrigin);

            // Step 1. Introduce Inner Variable
            introduceInnerVariable(methodStatement);

            PsiElement replaceElement = fetchReplaceElement(methodStatement);
            assert replaceElement != null;

            // Fetching Method Parameter: Replace
            PsiParameterList paramList = method.getParameterList();
            for (PsiReference reference : references) {
                PsiElement refElement = reference.getElement().getParent();
                assert refElement instanceof PsiMethodCallExpression;

                List<PsiStatement> declarations = new ArrayList<>();

                // Step 2. Introduce Temporary Variable
                PsiExpression[] newParamArray =
                        introduceTemporaryVariable(methodStatement, paramList, declarations);

                // Step 3. Replace Parameters (Be Aware of DummyHolder)
                replaceElement =
                        replaceParameters((PsiMethodCallExpression)refElement,
                                declarations, replaceElement,
                                paramList, newParamArray);

                // Step 4. Insert Statement
                insertStatements((PsiMethodCallExpression)refElement, declarations, replaceElement);
            }
        }
        // Delete Original Method
        WriteCommandAction.runWriteCommandAction(project, () -> {
            method.delete();
        });
    }

    private void introduceInnerVariable(PsiStatement statement) {
        List<PsiLocalVariable> localVarList = FindPsi.findPsiLocalVariables(statement);

        // Re-constructing paramList
        PsiType[] paramTypeList = new PsiType[localVarList.size()];
        String[] paramNameList = new String[localVarList.size()];

        for (int i = 0; i < localVarList.size(); i++) {
            paramNameList[i] = "inVar" + Integer.toString(i + 1);
            paramTypeList[i] = localVarList.get(i).getType();
        }
        PsiParameterList newParamList =
                CreatePsi.createMethodParameterListMultiple(project, paramTypeList, paramNameList);

        // Constructing expList
        PsiExpression[] expList = new PsiExpression[localVarList.size()];
        for (int i = 0; i < localVarList.size(); i++) {
            expList[i] = CreatePsi.createExpression(project, paramNameList[i]);
        }

        ReplacePsi.replaceParamToArgs(project, statement, newParamList.getParameters(), expList);
    }

    private PsiExpression[] introduceTemporaryVariable(PsiStatement statement,
                                            PsiParameterList paramList, List<PsiStatement> declarations) {

        // Introduce new declarations statements
        PsiParameter[] paramArray = paramList.getParameters();
        for (int i = 0; i < paramList.getParametersCount(); i++) {
            declarations.add(
                    CreatePsi.createStatement(
                            project,
                            paramArray[i].getType().getPresentableText() + " " +
                            "par" + Integer.toString(i + 1) + " = " + paramArray[i].getName() + ";"));
        }

        // Re-constructing paramList

        PsiExpression[] newParamArray = new PsiExpression[paramList.getParametersCount()];
        for (int i = 0; i < paramList.getParametersCount(); i++) {
            newParamArray[i] =
                    CreatePsi.createExpression(project, "par" + Integer.toString(i + 1));
        }

        return newParamArray;
    }
}
