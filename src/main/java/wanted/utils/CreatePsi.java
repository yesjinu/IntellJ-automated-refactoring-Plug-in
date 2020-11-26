package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.istack.NotNull;

/**
 * Class to create Psi Elements.
 *
 * @author seha Park
 * @author Mintae Kim
 * @author seungjae yoo
 */
public class CreatePsi {
    /**
     * Create setter method for given member
     *
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
     *
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
     *
     * @param project factory context
     * @param method method to call
     * @param par parameter of method, null if there's no parameter
     * @param qualifier qualifier of method, null if there's no qualifier
     * @return  Method Call expression. ex) qualifier.method(par)
     */
    public static PsiMethodCallExpression createMethodCall(@NotNull Project project, @NotNull PsiMethod method, PsiElement par, PsiElement qualifier)
    {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        String param = "";

        if(par!=null){ // parse identifier from PsiElement:
            param = par.toString();
            int ind = param.indexOf(':');
            param = param.substring(ind+1);
        }

        String qual = "";
        if(qualifier!=null){ // parse identifier of qualifier
            qual = qualifier.getText();
            int ind = qual.indexOf(':');
            qual = qual.substring(ind+1) + ".";
        }

        PsiExpression expression = factory.createExpressionFromText(
                                    qual + method.getName()+"("+param+")",
                                    null);

        return (PsiMethodCallExpression)expression;
    }

    /**
     * Return same statement which is copied
     *
     * @param project factory context
     * @param statement the original version of the statement
     * @return newStatement which is same with statement
     */
    public static PsiStatement copyStatement(@NotNull Project project, PsiStatement statement) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement newStatement = factory.createStatementFromText(statement.getText(), null);
        return newStatement;
    }

    /**
     * Return merged conditionExpression with || symbol
     *
     * @param project factory context
     * @param Left the conditional expression it would be left side
     * @param Right the conditional expression it would be right side
     * @param isFirstTime check boolean parameter that this function was used before for this ifStatement
     * @return newExpression which is "Left || Right"
     */
    public static PsiExpression createMergeCondition(@NotNull Project project, PsiExpression Left, PsiExpression Right, boolean isFirstTime) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String par;
        if (isFirstTime) par = "(" + Left.getText() + ")" + " || " + "(" + Right.getText() + ")";
        else par = Left.getText() + " || " + "(" + Right.getText() + ")";

        PsiExpression newExpression = factory.createExpressionFromText(par, null);
        return newExpression;
    }

    /**
     * Method which creates new Duplicate PsiExpression object for replacement.
     *
     * @param project Project
     * @param psiExpression Target PsiExpression to duplicate
     * @return Newly copied PsiExpression Object
     */
    public static PsiExpression createDuplicateExpression (@NotNull Project project, @NotNull PsiExpression psiExpression) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression newElement = factory.createExpressionFromText(psiExpression.getText(), null);
        return newElement;
    }


    /**
     * Method that capitalize name of given member
     *
     * @param member PsiField object
     * @return new name with its letter capitalized
     */
    public static String capitalize(PsiField member)
    {
        String name = member.getName(); // make first character uppercase
        String newName = name.substring(0, 1).toUpperCase()+name.substring(1);
        return newName;
    }

    /**
     * Return empty block statement
     *
     * @param project target context
     * @return Newly created Empty PsiBlockStatement
     */
    public static PsiStatement createEmptyBlockStatement(@NotNull Project project) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement newStatement = factory.createStatementFromText("{}", null);
        return newStatement;
    }
}
