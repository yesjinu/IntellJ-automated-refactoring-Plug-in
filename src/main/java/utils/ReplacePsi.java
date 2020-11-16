package utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import java.util.List;

// Class to replace specific Psi Elements
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
}
