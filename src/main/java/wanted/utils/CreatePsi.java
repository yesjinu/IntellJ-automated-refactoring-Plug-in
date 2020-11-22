package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.istack.NotNull;

/**
 * Class to create Psi Elements
 * @author seha Park
 */
public class CreatePsi {
    /**
     * Create setter method for given member
     * @param project factory context
     * @param member member to build setter
     * @return PsiMethod with name setMember
     */
    public static PsiMethod createSetMethod(@NotNull Project project, @NotNull PsiField member, String accessModifier)
    {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String type = member.getType().toString().substring(8); // erase PsiType:
        String newName = capitalize(member);

        PsiMethod newMethod = factory.createMethodFromText(
                        accessModifier + " void " + "set" + newName + "(" + type + " newValue) {\n"
                            + member.getName() + " = newValue;\n}",
                            null);

        return newMethod;
    }

    /**
     * Create getter method for given member
     * @param project factory context
     * @param member member to build getter
     * @return PsiMethod with name getMember
     */
    public static PsiMethod createGetMethod(@NotNull Project project, @NotNull PsiField member, String accessModifier)
    {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String type = member.getType().toString().substring(8); // erase PsiType:
        String newName = capitalize(member);

        PsiMethod newMethod = factory.createMethodFromText(
                        accessModifier + " " + type + " get" + newName + "() {\n"
                            + "return " + member.getName() + ";\n}",
                            null);

        return newMethod;
    }

    /**
     * Create MethodCallExpression for given method and parameter
     * Method can have only one parameter
     * @param project factory context
     * @param method method to call
     * @param par parameter of method
     *        null if there's no parameter
     * @return  Method Call expression. ex) method(par)
     */
    public static PsiMethodCallExpression createMethodCall(@NotNull Project project, @NotNull PsiMethod method, PsiElement par)
    {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        String param = "";

        if(par!=null){ // parse identifier from PsiElement:
            param = par.toString();
            int ind = param.indexOf(':');
            param = param.substring(ind+1);
        }

        PsiExpression expression = factory.createExpressionFromText(
                                method.getName()+"("+param+")",
                                null);

        return (PsiMethodCallExpression)expression;
    }

    /* capitalize name of given member */
    public static String capitalize(PsiField member)
    {
        String name = member.getName(); // make first character uppercase
        String newName = name.substring(0, 1).toUpperCase()+name.substring(1);
        return newName;
    }

    public static PsiStatement copyStatement(@NotNull Project project, PsiStatement statement) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement newStatement = factory.createStatementFromText(statement.getText(), null);
        return newStatement;
    }

    public static PsiExpression createMergeCondition(@NotNull Project project, PsiExpression Left, PsiExpression Right, boolean isFirstTime) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String par;
        if (isFirstTime) par = "(" + Left.getText() + ")" + " || " + "(" + Right.getText() + ")";
        else par = Left.getText() + " || " + "(" + Right.getText() + ")";

        PsiExpression newExpression = factory.createExpressionFromText(par, null);
        return newExpression;
    }
}
