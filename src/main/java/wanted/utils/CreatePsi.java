package wanted.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.istack.NotNull;

import java.util.Set;

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
     * @param project        target project
     * @param member         target PsiField to construct setter
     * @param accessModifier modifier of setter. recommend private or public
     * @return setter PsiMethod of given member with name 'setMember'
     */
    public static PsiMethod createSetMethod(@NotNull Project project, @NotNull PsiField member, @NotNull String accessModifier) {
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
     * @param project        target project
     * @param member         target PsiField to construct getter
     * @param accessModifier modifier of getter. recommend private or public
     * @return getter PsiMethod of given member with name 'getMember'
     */
    public static PsiMethod createGetMethod(@NotNull Project project, @NotNull PsiField member, @NotNull String accessModifier) {
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
     *
     * @param project   target project
     * @param method    target method to create method call expression
     * @param par       parameter of method, null if there's no parameter
     *                  method can have only one parameter
     * @param qualifier qualifier of method, null if there's no qualifier
     * @return Method Call expression. ex) qualifier.method(par)
     */
    public static PsiMethodCallExpression createMethodCall(@NotNull Project project, @NotNull PsiMethod method, PsiElement par, PsiElement qualifier) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        String param = "";

        if (par != null) { // parse identifier from PsiElement:
            param = par.toString();
            int ind = param.indexOf(':');
            param = param.substring(ind + 1);
        }

        String qual = "";
        if (qualifier != null) { // parse identifier of qualifier
            qual = qualifier.getText();
            int ind = qual.indexOf(':');
            qual = qual.substring(ind + 1) + ".";
        }

        PsiExpression newExpression = factory.createExpressionFromText(
                qual + method.getName() + "(" + param + ")",
                null);

        return (PsiMethodCallExpression) newExpression;
    }

    /**
     * Return merged conditionExpression with || symbol
     *
     * @param project     target project
     * @param Left        the conditional expression. It would be left side
     * @param Right       the conditional expression. It would be right side
     * @param isFirstTime parameter to check whether this function was used before for this ifStatement
     * @return newExpression which is "Left || Right"
     */
    public static PsiExpression createMergeCondition(@NotNull Project project, @NotNull PsiExpression Left, @NotNull PsiExpression Right, @NotNull boolean isFirstTime) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String par;
        if (isFirstTime) par = "(" + Left.getText() + ")" + " || " + "(" + Right.getText() + ")";
        else par = Left.getText() + " || " + "(" + Right.getText() + ")";

        PsiExpression newExpression = factory.createExpressionFromText(par, null);
        return newExpression;
    }

    /**
     * Return empty block statement
     *
     * @param project target project
     * @return Newly created Empty PsiBlockStatement
     */
    public static PsiStatement createEmptyBlockStatement(@NotNull Project project) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement newStatement = factory.createStatementFromText("{}", null);
        return newStatement;
    }

    /**
     * Create PsiField with given parameters
     *
     * @param project   target project
     * @param modifiers modifier of PsiField, 'private' modifier is added as default
     * @param type      type of field
     * @param name      name of field
     * @param value     initializer of field, null if initializer is not needed
     *                  user must provided value with correct type
     * @return
     */
    public static PsiField createField(@NotNull Project project, String[] modifiers, @NotNull PsiType type, @NotNull String name, String value) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField newField = factory.createField(name, type);
        for (String m : modifiers) // add modifiers
        {
            newField.getModifierList().setModifierProperty(m, true);
        }

        if (value != null) {
            PsiExpression initialize = factory.createExpressionFromText(value, null); // add initializer
            newField.setInitializer(initialize);
        }

        return newField;
    }

    /**
     * Create Assert Statement that check not null in if statement
     *
     * @param project   target project
     * @param condition condition of ifStatement
     * @param thenSet   set of expressions in then statement that should be check not null
     * @param elseSet   set of expressions in else statement that should be check not null
     *                  either thenSet.size() or elseSet.size() is not empty
     * @return Assert Statement
     */
    public static PsiStatement createAssertStatement(@NotNull Project project, @NotNull PsiExpression condition, @NotNull Set<PsiReferenceExpression> thenSet, @NotNull Set<PsiReferenceExpression> elseSet) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        String context = "";

        if (thenSet.isEmpty()) {
            context = "(" + condition.getText() + ")" + " || " + "(";
            boolean first = true;
            for (PsiReferenceExpression exp : elseSet) {
                if (first) {
                    if (elseSet.size() == 1) context = context + exp.getText() + " != null";
                    else context = context + "(" + exp.getText() + " != null)";
                } else context = context + " && " + "(" + exp.getText() + " != null)";
                first = false;
            }
            context = context + ")";
        } else if (elseSet.isEmpty()) {
            context = "!(" + condition.getText() + ")" + " || " + "(";
            boolean first = true;
            for (PsiReferenceExpression exp : thenSet) {
                if (first) {
                    if (thenSet.size() == 1) context = context + exp.getText() + " != null";
                    else context = context + "(" + exp.getText() + " != null)";
                } else context = context + " && " + "(" + exp.getText() + " != null)";
                first = false;
            }
            context = context + ")";
        } else {
            context = "(" + "(" + condition.getText() + ")";
            for (PsiReferenceExpression exp : thenSet) {
                context = context + " && " + "(" + exp.getText() + " != null)";
            }
            context = context + ")" + " || " + "(" + "!(" + condition.getText() + ")";
            for (PsiReferenceExpression exp : elseSet) {
                context = context + " && " + "(" + exp.getText() + " != null)";
            }
            context = context + ")";
        }

        PsiStatement newStatement = factory.createStatementFromText("assert (" + context + ");", null);
        return newStatement;
    }

    /**
     * Return same statement which is copied
     *
     * @param project   target project
     * @param statement original statement
     * @return newStatement which is same with the statement
     */
    public static PsiStatement copyStatement(@NotNull Project project, @NotNull PsiStatement statement) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiStatement newStatement = factory.createStatementFromText(statement.getText(), null);
        return newStatement;
    }

    /**
     * Return some expression which is copied
     *
     * @param project    target project
     * @param psiElement original element
     * @return new expression which is same with the psiElement
     */
    public static PsiExpression createDuplicateExpression(@NotNull Project project, @NotNull PsiElement psiElement) {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiExpression newExpression = factory.createExpressionFromText(psiElement.getText(), null);
        return newExpression;
    }

    /**
     * Capitalize name of given member
     *
     * @param member PsiField object
     * @return capitalized name of member
     */
    public static String capitalize(@NotNull PsiField member) {
        String name = member.getName(); // make first character uppercase
        String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return newName;
    }
}
