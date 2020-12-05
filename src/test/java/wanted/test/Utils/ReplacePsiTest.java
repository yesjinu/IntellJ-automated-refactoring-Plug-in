package wanted.test.Utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.AddPsi;
import wanted.utils.FindPsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Test class for utils/ReplacePsi
 *
 * @author seha Park
 * @author Jinu Noh
 */
public class ReplacePsiTest extends AbstractLightCodeInsightTestCase {
    public void testEncapField1() // TODO
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* encapsulation for field: double targetField */
        PsiField target = factory.createField("targetField", PsiType.DOUBLE);

        PsiMethod getter = factory.createMethod("getTargetField", PsiType.DOUBLE);
        PsiMethod setter = factory.createMethod("setTargetField", PsiType.VOID);

        PsiStatement base1 = factory.createStatementFromText("targetField = 1.0;", target);
        PsiExpression base2 = factory.createExpressionFromText("x = targetField", target);
        PsiExpression base3 = factory.createExpressionFromText("System.out.println(targetField)", target);
        PsiElement[] baseExps = {base1, base2, base3};

        List<PsiReferenceExpression> expressions = new ArrayList<>();
        for(PsiElement base : baseExps) {
            FindPsi.findReferenceExpression(base).forEach(
                    (r) -> {
                        if (r.isReferenceTo(target)) {
                            expressions.add(r);
                        }
                    });
        }

        ReplacePsi.encapField(project, getter, setter, expressions); // encapsulate with getter and setter

        String expected1 = "setTargetField(1.0);";
        String expected2 = "x = getTargetField()";
        String expected3 = "System.out.println(getTargetField())";

        Assertions.assertTrue(base1.isValid() && base2.isValid() && base3.isValid());
        Assertions.assertEquals(expected1, base1.getText());
        Assertions.assertEquals(expected2, base2.getText());
        Assertions.assertEquals(expected3, base3.getText());
    }

    /* MergeCondStatement test 1: when elseElseStatement is null */
    public void testMergeCondStatement1()
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String ifString = "if((x==1) || (x==2))\n" +
                        "{ return true; }\n" +
                        "else if(x==2){ return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondStatement(project, ifStatement);

        String expected = "if((x==1) || (x==2))\n" +
                        "{ return true; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* MergeCondStatement test 2: when elseElseStatement is not null */
    public void testMergeCondStatement2()
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String ifString = "if((x==1) || (x==2))\n" +
                        "{ return true; }\n" +
                        "else if(x==2){ return true; }\n"+
                        "else { return false; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondStatement(project, ifStatement);

        String expected = "if((x==1) || (x==2))\n" +
                        "{ return true; }\n" +
                        "else { return false; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    public void testRemoveCondStatement() //TODO
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String ifString = "if(x==1)\n" +
                        "{ return true; }\n" +
                        "else { return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.removeCondStatement(project, ifStatement);

        String expected = "{ return true;\n }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* MergeCondExpr test 1: merge condition first time */
    public void testMergeCondExpr1()
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String ifString = "if(x==1)\n" +
                        "{ return true; }\n" +
                        "else if(x==2) { return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondExpr(project, ifStatement, true);

        String expected = "if((x==1) || (x==2))\n" +
                        "{ return true; }\n" +
                        "else if(x==2) { return true; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* MergeCondExpr test 2: condition has been merged once */
    public void testMergeCondExpr2()
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String ifString = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else if(x==3) { return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondExpr(project, ifStatement, false);

        String expected = "if((x==1) || (x==2) || (x==3))\n" +
                "{ return true; }\n" +
                "else if(x==3) { return true; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    public void testReplaceParamToArgs()
    {

    }

    public void testChangeModifier()
    {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        // for field: final static public int testField
        PsiField member = factory.createField("testField", PsiType.INT);
        member.getModifierList().setModifierProperty(PsiModifier.FINAL, true);
        member.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
        member.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        Assertions.assertEquals("public static final int testField;", member.getText());

        List<String> removeValues = new ArrayList<>();
        removeValues.add(PsiModifier.PUBLIC); removeValues.add(PsiModifier.FINAL);
        List<String> addValues = new ArrayList<>();
        addValues.add(PsiModifier.ABSTRACT); addValues.add(PsiModifier.PRIVATE);

        ReplacePsi.changeModifier(member, removeValues, addValues);

        String expected = "private static abstract int testField;";

        Assertions.assertTrue(member.isValid());
        Assertions.assertEquals(expected, member.getText());
    }

    public void testPulloutFirstCondExpr()
    {

    }

    public void testPulloutLastCondExpr()
    {

    }

    public void testRemoveUselessCondition()
    {

    }

    public void testReplacePsi4inString() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);


        final PsiClass targetClass = factory.createClass("Temp");
        PsiField field = factory.createFieldFromText("public int value;", null);
        PsiField newField = factory.createFieldFromText("private int value;", null);
        targetClass.addAfter(field, targetClass.getLBrace());

        System.out.println(targetClass.getText());

        PsiField targetField = null;
        for (PsiElement e : targetClass.getChildren()) {
            if (e instanceof PsiField) {
                targetField = (PsiField) e;
                break;
            }
        }

        List<String> add = new ArrayList<>(); add.add("private");
        List<String> del = new ArrayList<>(); del.add("public");

        PsiField finalTargetField = targetField;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            ReplacePsi.changeModifier(finalTargetField, del, add);
        });

        assertEquals(finalTargetField.getText(), newField.getText());
    }
}
