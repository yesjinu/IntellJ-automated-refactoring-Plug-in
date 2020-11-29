package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.ResolveScopeManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.tree.IElementType;
import wanted.utils.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//TODO: write docs
/**
 * Class to provide refactoring: 'Replace Magic Number'
 *
 * @author seha Park
 */
public class ReplaceMagicNumber extends BaseRefactorAction{
    private Project project;
    private PsiClass targetClass;
    private PsiLiteralExpression literal;
    private List<PsiLiteralExpression> expressions;

    @Override
    public String storyName()
    {
        return "Replace Magic Number";
    }

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        targetClass = navigator.findClass();
        literal = navigator.findLiteral();

        return refactorValid(project, literal);
    }

    /**
     * Refactoring condition:
     * string - not whitespace or empty string
     * numerical - not 0, 1, 2 (0.0, 1.0, 2.0 is ok)
     * - int, long, float, double, short(int)
     * char - not whitespace
     * @param project
     * @param literal
     * @return
     */
    //TODO: short ??
    public static boolean refactorValid(Project project, PsiLiteralExpression literal) {
        if(literal==null){ return false; }

        IElementType literalType = ((PsiLiteralExpressionImpl)literal).getLiteralElementType();

        PsiType[] types = {PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE};
        List<PsiType> numericalTypes = new ArrayList<>(); // PsiTypes representing numerical types
        numericalTypes.addAll(Arrays.asList(types));
        // decide whether literal is worth to refactor
        if(ElementType.STRING_LITERALS.contains(literalType)) // string
        {
            if(literal.getValue().equals("")||literal.getValue().equals(" ")){ return false; }
            else { return true; }
        } else if(literal.getType().equals(PsiType.CHAR)) // char
        {
            if(literal.getValue().equals(' ')){ return false; }
            else { return true; }
        } else if(numericalTypes.contains(literal.getType())) // numerical value
        {
            Object val = literal.getValue();
            if(val.equals(0)||val.equals(1)||val.equals(2)){ return false; }
            else{ return true; }
        }
        else { return false; } // do not refactor others (ex. user-defined class, boolean ... )
    }

    @Override
    protected void refactor(AnActionEvent e)
    {
        // find expression with same value (it can be numeric value or string)
        expressions = FindPsi.findLiteralUsage(targetClass, literal);

        // build symbolic constant with name constant#N (need to check duplicate)
        int num = 1;
        for(PsiField f : targetClass.getFields())
        {
            if(f.getName().equals("CONSTANT"+num)){ num++; }
        }

        // find if there is constant with same value
        // if not, create constant
        String[] modifiers = {PsiModifier.STATIC, PsiModifier.FINAL };
        PsiField newField = CreatePsi.createField(project, modifiers, literal.getType(), "CONSTANT"+num, literal.getText());
        List<PsiElement> addList = new ArrayList<>();
        addList.add(newField);

        WriteCommandAction.runWriteCommandAction(project, ()->{
            // introduce constant
            AddPsi.addField(targetClass, addList);
            // replace values
        });
    }
}
