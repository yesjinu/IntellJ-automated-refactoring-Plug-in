package wanted.refactoring;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import wanted.utils.CreatePsi;
import wanted.utils.FindPsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to provide refactoring: 'Inline Method (Strengthen)'
 *
 * @author Mintae Kim
 */
public class InlineMethodStrengthen extends InlineMethod {
    private static int inVarNum = 0;
    private static int parNum = 0;

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
    public String description() {
        return "<html>When a method body is more obvious than the method itself, <br/>" +
                "Replace calls to the method with the method's content and delete the method itself.<br/><br/>" +
                "Plus, Inline Method (Strengthen) thoroughly considers about<br/>" +
                "inner variables and parameters which are passed by value in JAVA.<br/>" +
                "This refactoring allows you to keep your code consistent after refactoring,<br/>" +
                "As a result, your code will give you same result <br/>" +
                "compared to the ones before refactoring.</html>";
    }

    /* Initialize Number */
    public boolean refactorValid(AnActionEvent e) {
        initVarNum();
        return super.refactorValid(e);
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

        Comparator<PsiReference> comparator = new Comparator<PsiReference>() {
            @Override
            public int compare(PsiReference a, PsiReference b) {
                return a.getElement().getParent().getText().compareTo(b.getElement().getParent().getText());
            }
        };

        List<PsiReference> references = new ArrayList<>(ReferencesSearch.search(method).findAll());
        Collections.sort(references, comparator);

        // Fetching element to replace
        PsiStatement methodStatementOrigin = method.getBody().getStatements()[0];
        // Fetching Method Parameter: Replace
        PsiParameterList paramList = method.getParameterList();

        if (!references.isEmpty()) {
            for (PsiReference reference : references) {
                // Copy Statement from origin
                PsiStatement methodStatement = CreatePsi.copyStatement(project, methodStatementOrigin);

                // Step 1. Introduce Inner Variable
                introduceInnerVariable(methodStatement);

                // Fetching Replace Element
                PsiElement replaceElement = fetchReplaceElement(methodStatement);
                if (replaceElement == null) continue;

                // Fetching Reference Element
                PsiElement refElement = reference.getElement().getParent();
                assert refElement instanceof PsiMethodCallExpression;

                // Step 2. Introduce Temporary Variable
                List<PsiStatement> declarations = new ArrayList<>();
                PsiExpression[] newParamArray =
                        introduceTemporaryVariable(paramList, declarations);

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

    /**
     * Method that makes local variables into inline variable array and replace original variable with them.
     *
     * @param statement PsiStatement
     */
    private void introduceInnerVariable(PsiStatement statement) {
        List<PsiLocalVariable> localVarList = FindPsi.findPsiLocalVariables(statement);

        // Re-constructing paramList
        String[] paramNameArray = new String[localVarList.size()];
        String[] paramRefNameArray = new String[localVarList.size()];

        for (int i = 0; i < localVarList.size(); i++) {
            paramNameArray[i] = localVarList.get(i).getName();
            paramRefNameArray[i] = "inVar" + Integer.toString(++inVarNum);
        }

        ReplacePsi.replaceVariable(project, statement, paramNameArray, paramRefNameArray);
    }


    /**
     * Method that create new parameter array which is used for temporary time.
     *
     * @param paramList PsiParameterList
     * @param declarations List<PsiStatement>
     * @return PsiExpression[]
     */
    private PsiExpression[] introduceTemporaryVariable(PsiParameterList paramList, List<PsiStatement> declarations) {

        // Introduce new declarations statements
        PsiParameter[] paramArray = paramList.getParameters();
        for (int i = 0; i < paramList.getParametersCount(); i++) {
            declarations.add(
                    CreatePsi.createStatement(
                            project,
                            paramArray[i].getType().getPresentableText() + " " +
                            "par" + Integer.toString(parNum + i + 1) + " = " + paramArray[i].getName() + ";"));
        }

        // Re-constructing paramList
        PsiExpression[] newParamArray = new PsiExpression[paramList.getParametersCount()];
        for (int i = 0; i < paramList.getParametersCount(); i++) {
            newParamArray[i] =
                    CreatePsi.createExpression(project, "par" + Integer.toString(++parNum));
        }

        return newParamArray;
    }

    /**
     * Method that initiate variable number.
     */
    @VisibleForTesting
    public static void initVarNum() {
        parNum = 0;
        inVarNum = 0;
    }
}
