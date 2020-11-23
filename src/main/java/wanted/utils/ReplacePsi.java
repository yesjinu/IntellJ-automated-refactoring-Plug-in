package wanted.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to replace specific Psi Elements.
 *
 * @author seha park
 * @author Mintae Kim
 */
public class ReplacePsi {
    /**
     * Replace getter() and setter()
     *
     * @param project context
     * @param getter getter PsiMethod
     * @param setter setter PsiMethod
     * @param expressions Statements that refers 'member'
     * @param member field to encapsulate
     */
    public static void encapFied(Project project, PsiMethod getter, PsiMethod setter, List<PsiReferenceExpression> expressions, PsiField member)
    {
        PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null);

        for(PsiReferenceExpression old :expressions)
        {
            if(old.getParent() instanceof PsiAssignmentExpression)
            {
                PsiAssignmentExpression assignment = (PsiAssignmentExpression) old.getParent();
                if(assignment.getLExpression().isEquivalentTo(old)) // assignment to member
                {
                    PsiElement newValue = assignment.getRExpression();
                    PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue);
                    (old.getParent()).replace(callSetter);
                }
                else
                {
                    old.replace(callGetter);
                }
            }
            else
            {
                old.replace(callGetter);
            }
        }
    }

    /**
     * Replace vars in paramList to vars in paramRefList for PsiElement element.
     *
     * @param element Target PsiElement to refactor
     * @param paramList List of PsiMethod parameters
     * @param paramRefList List of expressions for calling target PsiMethod
     * @return PsiElement with altered PsiTree
     */
    public static PsiElement replaceParamToArgs(Project project, PsiElement element, PsiParameterList paramList, PsiExpressionList paramRefList) {
        assert paramList.getParametersCount() == paramRefList.getExpressionCount();
        PsiParameter [] paramArray = paramList.getParameters();
        PsiExpression [] paramRefArray = paramRefList.getExpressions();

        List<PsiElement> resolveList = new ArrayList<>();
        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            final List<PsiElement> resolveList_inner = resolveList;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PsiIdentifier) return;
                for (int i = 0; i < paramList.getParametersCount(); i++) {
                    if (element.getText().equals(paramArray[i].getName())) {
                        resolveList_inner.add(element);
                        break;
                    }
                }
                super.visitElement(element);
            }
        };

        element.accept(visitor);

        for (PsiElement resolveEntry : resolveList) {
            for (int i = 0; i < paramList.getParametersCount(); i++) {
                if (resolveEntry.getText().equals(paramArray[i].getName())) {
                    PsiExpression newExp = CreatePsi.createDuplicateExpression(project, paramRefArray[i]);
                    resolveEntry.replace(newExp);
                    break;
                }
            }
        }
        return element;
    }
}
