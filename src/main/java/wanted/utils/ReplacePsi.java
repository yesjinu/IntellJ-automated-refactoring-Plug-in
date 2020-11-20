/**
 * Class to replace specific Psi Elements.
 *
 * @author seha park
 * @author Mintae Kim
 */
package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReplacePsi {
    /**
     * Replace getter() and setter()
     * @param project
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
                if(assignment.getLExpression().isEquivalentTo(old)) // define member
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


/*
            if(((PsiExpressionStatement) old).getExpression() instanceof PsiAssignmentExpression)
            {
                PsiAssignmentExpression assignment= (PsiAssignmentExpression) ((PsiExpressionStatement)old).getExpression();
                PsiElement newValue = assignment.getRExpression();
                PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue);
                old.replace(callSetter);
            }
            else {
                PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null);
                List<PsiReferenceExpression> ref = FindPsi.findReference(old);
                for(PsiReferenceExpression r : ref)
                {
                    // ? assume no synonym
                    if(r.getText().contains(member.getName()))
                    {
                        r.replace(callGetter);
                    }
                }
            }*/

        }
    }

    /**
     * Replace vars in paramList to vars in paramRefList for PsiElement element.
     *
     * @param element Target PsiElement to refactor
     * @param paramList List of PsiMethod parameters
     * @param paramRefList List of expressions for calling target PsiMethod
     */
    public static void replaceParamToArgs(PsiElement element, PsiParameterList paramList, PsiExpressionList paramRefList) {
        assert paramList.getParametersCount() == paramRefList.getExpressionCount();
        PsiParameter [] paramArray = paramList.getParameters();
        PsiExpression [] paramRefArray = paramRefList.getExpressions();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                for (int i = 0; i < paramList.getParametersCount(); i++) {
                    if (element.isEquivalentTo(paramArray[i])) {
                        element.replace(paramRefArray[i]);
                        break;
                    }
                }
            }
        };

        element.accept(visitor);
    }
}
