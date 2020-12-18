package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.tree.IElementType;
import wanted.utils.*;

import java.util.*;

/**
 * Class to provide refactoring: 'Replace Magic Number'
 *
 * @author seha Park
 * look refactorValid() to find refactoring condition
 */
public class ReplaceMagicNumber extends BaseRefactorAction {
    private Project project;
    private PsiClass targetClass;
    private PsiLiteralExpression literal;
    private List<PsiLiteralExpression> expressions;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "RMN";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Replace Magic Number";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When literal expression are repeatedly used inside class, <br/>" +
                "replace literal expression to constant.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html> For String or Char type literal expression, it shouldn't be empty or whitespace. <br/>" +
                "For Int type expression, it shouldn't be 0, 1, 2. </html>";
    }

    /**
     * Check if literal is valid and it is worth to refactor
     *
     * @param e AnActionevent
     * @return true if it is refotorable, else return false
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        if (project == null) {
            return false;
        }

        literal = navigator.findLiteral();

        return refactorValid(project, literal);
    }

    /**
     * Check if literal is valid and it is worth to refactor
     *
     * @param project not used
     * @param literal literal expression selected by user
     * @return true if it is refotorable, else return false
     * @note refactoring condition
     * - String : not whitespace or empty string
     * - Char : not whitespace
     * - Int : not 0, 1, 2
     * <p>
     * also, due to the PsiType implementation of Intellij,
     * - short and byte literal expressions are treated as integer
     * - for long type literal without L notation, if it's value is between -2^31 ~ 2^31-1 it is treated as integer
     * - for char type literal, if it's value is between -2^31 ~ 2^31-1 it is treated as integer
     */
    public static boolean refactorValid(Project project, PsiLiteralExpression literal) {
        if ((literal == null) || (literal.getType() == null || (literal.getValue() == null))) {
            return false;
        }
        if (FindPsi.getContainingClass(literal) == null) {
            return false;
        }

        IElementType literalType = ((PsiLiteralExpressionImpl) literal).getLiteralElementType();

        List<PsiType> numericalTypes = new ArrayList<>(); // PsiTypes representing numerical types
        PsiType[] types = {PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE};
        numericalTypes.addAll(Arrays.asList(types));

        // decide whether literal is worth to refactor
        if (ElementType.STRING_LITERALS.contains(literalType)) // string
        {
            if ((!literal.getValue().equals("")) && (!literal.getValue().equals(" "))) {
                return true;
            }
        } else if (literal.getType().equals(PsiType.CHAR) && (!literal.getValue().equals(' '))) // refactor non-blank char
        {
            return true;
        } else if (literal.getType().equals(PsiType.INT)) //  for int, bytes, short, and some of long, char that represent int
        {
            Object val = literal.getValue();
            if (!((val.equals(0)) || val.equals(1) || val.equals(2))) {
                return true;
            } // refactor non-trivial values
        } else if (numericalTypes.contains(literal.getType())) {
            return true;
        }

        return false; // do not refactor others (ex. user-defined class, boolean ... )
    }

    @Override
    public void refactor(AnActionEvent e) {
        // find expression with same value (it can be numeric value or string)
        targetClass = FindPsi.getContainingClass(literal);

        expressions = FindPsi.findLiteralUsage(targetClass, literal);

        // find if there is constant with same value or name CONSTANT#N
        PsiField constant = null;
        boolean needNewSymbol = true;
        Set<String> names = new HashSet<>();
        PsiExpression ret;

        for (PsiField f : targetClass.getFields()) {
            if (f.hasInitializer() && f.getInitializer().getText().equals(literal.getText())) // if value is same
            {
                if (f.hasModifierProperty(PsiModifier.STATIC) && f.hasModifierProperty(PsiModifier.FINAL)) // if static final field already exists
                {
                    needNewSymbol = false;
                    constant = f;
                    expressions.remove(FindPsi.findChildPsiLiteralExpressions(f).get(0));
                    break;
                }
            }
            if (f.getName().contains("CONSTANT")) {
                names.add(f.getName());
            }
        }

        // build symbolic constant with name constant#N if needed
        List<PsiField> addList = new ArrayList<>();
        int num = 1;
        if (needNewSymbol) {
            String[] modifiers = {PsiModifier.STATIC, PsiModifier.FINAL};

            while (names.contains("CONSTANT" + num)) {
                num++;
            } // find unreserved constant number

            constant = CreatePsi.createField(project, modifiers, literal.getType(), "CONSTANT" + num, literal.getText());
            constant.getModifierList().setModifierProperty(PsiModifier.PRIVATE, false); // remove default private modifier
            addList.add(constant);

            WriteCommandAction.runWriteCommandAction(project, () -> {
                AddPsi.addField(targetClass, addList);  // add constant to field
            });
            ret = CreatePsi.createExpression(project, "CONSTANT" + num);
        } else {
            ret = CreatePsi.createExpression(project, constant.getName());
        }

        PsiExpression finalRet = ret;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // replace literal expression into constant
            for (PsiLiteralExpression expression : expressions) {
                expression.replace(finalRet);
            }
        });
    }
}
