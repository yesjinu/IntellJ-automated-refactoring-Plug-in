package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import java.util.List;

/**
 * Class to replace specific Psi Elements
 * @author seha Park
 */
public class ReplacePsi {
    /**
     * Replace getter() and setter()
     * @param project context
     * @param getter getter PsiMethod
     * @param setter setter PsiMethod
     * @param expressions Statements that refers 'member'
     */
    public static void encapFied(Project project, PsiMethod getter, PsiMethod setter, List<PsiReferenceExpression> expressions)
    {
        for(PsiReferenceExpression old :expressions)
        {
            if(old.getParent() instanceof PsiAssignmentExpression)
            {
                PsiAssignmentExpression assignment = (PsiAssignmentExpression) old.getParent();
                if(assignment.getLExpression().isEquivalentTo(old)) // assignment to member
                {
                    PsiElement newValue = assignment.getRExpression();
                    PsiMethodCallExpression callSetter = CreatePsi.createMethodCall(project, setter, newValue, old.getQualifier());
                    (old.getParent()).replace(callSetter);
                }
                else
                {
                    PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                    old.replace(callGetter);
                }
            }
            else
            {
                PsiMethodCallExpression callGetter = CreatePsi.createMethodCall(project, getter, null, old.getQualifier());
                old.replace(callGetter);
            }
        }
    }

    /**
     * Edit modifier of member
     * @param member selected member
     * @param removeValues modifier to be removed
     * @param addValues modifier to be added
     */
    public static void changeModifier(PsiField member, List<String> removeValues, List<String> addValues)
    {
        for(String removeValue :removeValues)
        {
            member.getModifierList().setModifierProperty(removeValue, false);
        }

        for(String addValue :addValues)
        {
            member.getModifierList().setModifierProperty(addValue, true);
        }
    }
}
