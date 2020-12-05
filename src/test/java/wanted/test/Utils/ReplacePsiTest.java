package wanted.test.Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.FindPsi;
import wanted.utils.ReplacePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for utils/ReplacePsi
 *
 * @author seha Park
 * @author Jinu Noh
 */
public class ReplacePsiTest extends AbstractLightCodeInsightTestCase {
    public void testEncapField() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* encapsulation for field: double targetField */
        PsiField target = factory.createField("targetField", PsiType.DOUBLE);

        PsiMethod setter = factory.createMethod("method1", PsiType.DOUBLE);
        PsiMethod getter = factory.createMethod("method2", PsiType.VOID);

        PsiStatement base1 = factory.createStatementFromText("targetField = 1.0;", target);
        PsiExpression base2 = factory.createExpressionFromText("x = targetField", target);
        PsiExpression base3 = factory.createExpressionFromText("System.out.println(targetField)", target);
        PsiElement[] baseExps = {base1, base2, base3};

        List<PsiReferenceExpression> expressions = new ArrayList<>();
        for (PsiElement base : baseExps) {
            FindPsi.findReferenceExpression(base).forEach(
                    (r) -> {
                        if (r.isReferenceTo(target)) {
                            expressions.add(r);
                        }
                    });
        }

        ReplacePsi.encapField(project, getter, setter, expressions); // encapsulate with getter and setter

        String expected1 = "method1(1.0);";
        String expected2 = "x = method2()";
        String expected3 = "System.out.println(method2())";

        Assertions.assertTrue(base1.isValid() && base2.isValid() && base3.isValid());
        Assertions.assertEquals(expected1, base1.getText());
        Assertions.assertEquals(expected2, base2.getText());
        Assertions.assertEquals(expected3, base3.getText());
    }

    /* mergeCondStatement test1: when there are two branches */
    public void testMergeCondStatement1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* IfStatement to merge */
        String ifString = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else if(x==2){ return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondStatement(project, ifStatement); // apply merge conditional statement

        String expected = "if((x==1) || (x==2))\n" +
                "{ return true; }\n";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* mergeCondStatement test 2: when there are three or more branches */
    public void testMergeCondStatement2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* IfStatement to merge */
        String ifString = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else if(x==2){ return true; }\n" +
                "else { return false; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondStatement(project, ifStatement);

        String expected = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else { return false; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* removeCondStatement test 1 : when if statement is complete */
    public void testRemoveCondStatement1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* Parent method that contains IfStatement to remove */
        String parentString = "public boolean dummy() {\n"
                + "int x = 1;\n"
                + "if(x==1){\n"
                + "return true;\n"
                + "} else{\n"
                + "return true;\n"
                + "}\n"
                + "}";
        PsiMethod parent = factory.createMethodFromText(parentString, null);
        Assertions.assertTrue(parent.isValid());

        /* IfStatement to modify */
        PsiIfStatement ifStatement = (PsiIfStatement) parent.getChildren()[9].getChildren()[4];

        ReplacePsi.removeCondStatement(project, ifStatement); // apply remove conditional statement

        String expected = "public boolean dummy() {\n"
                + "int x = 1;\n"
                + "{\n"
                + "return true;\n"
                + "}\n"
                + "}";

        Assertions.assertEquals(expected, parent.getText());
    }

    /* removeCondStatement test 2 : when if statement is incomplete */
    public void testRemoveCondStatement2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* Parent method that contains IfStatement to remove */
        String parentString = "public boolean dummy() {\n"
                + "int x = 1;\n"
                + "if(x==1)\n"
                + "}";
        PsiMethod parent = factory.createMethodFromText(parentString, null);

        /* IfStatement to modify */
        PsiIfStatement ifStatement = (PsiIfStatement) parent.getChildren()[9].getChildren()[4];

        ReplacePsi.removeCondStatement(project, ifStatement);

        String expected = "public boolean dummy() {\n"
                + "int x = 1;\n"
                + "}";

        Assertions.assertEquals(expected, parent.getText());
    }

    /* mergeCondExpr test 1: when condition hasn't been merged yet */
    public void testMergeCondExpr1() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* IfStatement to merge */
        String ifString = "if(x==1)\n" +
                "{ return true; }\n" +
                "else if(x==2) { return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondExpr(project, ifStatement, true); // apply merge conditional expression

        String expected = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else if(x==2) { return true; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    /* mergeCondExpr test 2: when condition has been merged before */
    public void testMergeCondExpr2() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* IfStatement to merge */
        String ifString = "if((x==1) || (x==2))\n" +
                "{ return true; }\n" +
                "else if(x==3) { return true; }";
        PsiIfStatement ifStatement = (PsiIfStatement) factory.createStatementFromText(ifString, null);

        ReplacePsi.mergeCondExpr(project, ifStatement, false); // apply merge conditional expression

        String expected = "if((x==1) || (x==2) || (x==3))\n" +
                "{ return true; }\n" +
                "else if(x==3) { return true; }";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, ifStatement.getText());
    }

    public void testReplaceParamToArgs() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* expression to replace variables */
        PsiElement element = factory.createExpressionFromText("x + y", null);

        String[] params = {"x", "y"};
        PsiType[] types = {PsiType.INT, PsiType.INT};
        PsiParameterList paramList = factory.createParameterList(params, types);

        PsiExpressionListStatement statement = (PsiExpressionListStatement) factory.createStatementFromText("a, b", null);
        PsiExpressionList paramRefList = statement.getExpressionList();

        // apply replace parameter to arguments
        PsiElement replaceElement = ReplacePsi.replaceParamToArgs(project, element, paramList, paramRefList);

        String expected = "a + b";

        Assertions.assertTrue(replaceElement.isValid());
        Assertions.assertEquals(expected, replaceElement.getText());
    }

    public void testChangeModifier() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* field to change modifier: final static public int testField */
        PsiField member = factory.createField("testField", PsiType.INT);
        member.getModifierList().setModifierProperty(PsiModifier.FINAL, true);
        member.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
        member.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        Assertions.assertEquals("public static final int testField;", member.getText());

        List<String> removeValues = new ArrayList<>();
        removeValues.add(PsiModifier.PUBLIC);
        removeValues.add(PsiModifier.FINAL);
        List<String> addValues = new ArrayList<>();
        addValues.add(PsiModifier.ABSTRACT);
        addValues.add(PsiModifier.PRIVATE);

        ReplacePsi.changeModifier(member, removeValues, addValues); // change modifier

        String expected = "private static abstract int testField;";

        Assertions.assertTrue(member.isValid());
        Assertions.assertEquals(expected, member.getText());
    }

    public void testPulloutFirstCondExpr() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* parent method that holds if statements to pull out */
        String parentString = "public void dummy() {\n"
                + "int x = 1;\n"
                + "int a, b;\n"
                + "if(x==1){\n"
                + "a = 1;\n"
                + "b = 1;\n"
                + "} else if(x==2){\n"
                + "a = 1;\n"
                + "b = 2;\n"
                + "}\n"
                + "else a = 1;\n"
                + "}";
        PsiMethod parent = factory.createMethodFromText(parentString, null);
        Assertions.assertTrue(parent.isValid());

        /* ifStatement to modify */
        PsiIfStatement ifStatement = (PsiIfStatement) parent.getChildren()[9].getChildren()[6];

        List<PsiStatement> statementList = new ArrayList<>();
        statementList.add(ifStatement.getThenBranch());
        statementList.add(((PsiIfStatement) ifStatement.getElseBranch()).getThenBranch());
        statementList.add(((PsiIfStatement) ifStatement.getElseBranch()).getElseBranch());

        ReplacePsi.pulloutFirstCondExpr(project, ifStatement, statementList); // pull out first conditional expression

        String expected = "public void dummy() {\n"
                + "int x = 1;\n"
                + "int a, b;\n"
                + "a = 1;if(x==1){\n"
                + "b = 1;\n"
                + "} else if(x==2){\n"
                + "b = 2;\n"
                + "}\n"
                + "else {}\n"
                + "}";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, parent.getText());
    }

    public void testPulloutLastCondExpr() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* parent method that holds if statements to pull out */
        String parentString = "public void dummy() {\n"
                + "int x = 1;\n"
                + "int a, b;\n"
                + "if(x==1){\n"
                + "a = 1;\n"
                + "b = 1;\n"
                + "} else if(x==2){\n"
                + "a = 2;\n"
                + "b = 1;\n"
                + "}\n"
                + "else b = 1;\n"
                + "}";
        PsiMethod parent = factory.createMethodFromText(parentString, null);
        Assertions.assertTrue(parent.isValid());

        /* ifStatement to modify */
        PsiIfStatement ifStatement = (PsiIfStatement) parent.getChildren()[9].getChildren()[6]; // get ifStatement inside method

        List<PsiStatement> statementList = new ArrayList<>();
        statementList.add(ifStatement.getThenBranch());
        statementList.add(((PsiIfStatement) ifStatement.getElseBranch()).getThenBranch());
        statementList.add(((PsiIfStatement) ifStatement.getElseBranch()).getElseBranch());

        ReplacePsi.pulloutLastCondExpr(project, ifStatement, statementList); // pull out last conditional expression

        String expected = "public void dummy() {\n"
                + "int x = 1;\n"
                + "int a, b;\n"
                + "if(x==1){\n"
                + "a = 1;\n"
                + "} else if(x==2){\n"
                + "a = 2;\n"
                + "}\n"
                + "else {}b = 1;\n"
                + "}";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, parent.getText());
    }

    public void testRemoveUselessCondition() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        /* parent method that holds if statements to pull out */
        String parentString = "public void dummy() {\n"
                + "int x = 1;\n"
                + "if(x==1){\n"
                + "x = 2;\n"
                + "}\n"
                + "else if(x==2){\n"
                + "}\n"
                + "else{\n"
                + "}\n"
                + "}";
        PsiMethod parent = factory.createMethodFromText(parentString, null);
        Assertions.assertTrue(parent.isValid());

        /* ifStatement to modify */
        PsiIfStatement ifStatement = (PsiIfStatement) parent.getChildren()[9].getChildren()[4]; // get ifStatement inside method

        ReplacePsi.removeUselessCondition(project, ifStatement); // remove useless conditional branch

        String expected = "public void dummy() {\n"
                + "int x = 1;\n"
                + "if(x==1){\n"
                + "x = 2;\n"
                + "}\n"
                + "}";

        Assertions.assertTrue(ifStatement.isValid());
        Assertions.assertEquals(expected, parent.getText());
    }
}
