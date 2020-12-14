package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to provide refactoring: 'Hide Delegate'
 * - Create a new method in class A that delegates the call to object B.
 * - Now the client doesn’t know about, or depend on, class B.
 *
 * @author Chanyoung Kim
 */
public class HideDelegateAction extends BaseRefactorAction {
    private static List<PsiClass> classList = new ArrayList<>();
    private static PsiMethodCallExpression targetMethodCallExp;
    private static String targetClassName;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "HD";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Hide Delegate";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String descripton() {
        return "<html>Create a new method in class A that delegates the call to object B. <br/>" +
                "Now the client doesn’t know about, or depend on, class B. </html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html> The method call that returns an object is called twice in series. <br/>" +
                "Both method calls have no parameters, only return statements. <br/>" +
                "The object returned by the method call must be defined as a class. </html>";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Hide Delegate'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        Project project = navigator.findProject();
        if (project == null) return false;

        PsiFile file = navigator.findFile();
        if (file == null) return false;

        PsiClass targetClass = navigator.findClass();
        if (targetClass == null) return false;

        return refactorValid(targetClass);
    }

    /**
     * Static method that checks whether candidate method is refactorable
     * using 'Hide Delegate'.
     *
     * When does this refactoring activiate,
     * -> a utility object is declared in a function inside a class, using existing utility object as a parameter
     *
     * How to implement this refactoring
     * - In (Assignment, Declaration, Field) cases, we check the existence of methodcallexp that can be refactored.
     *
     * @param targetClass PsiField Object
     * @return true if method is refactorable
     */
    public static boolean refactorValid(@NotNull PsiClass targetClass) {
        classList.clear();
        targetMethodCallExp = null;
        targetClassName = null;


        PsiFile file = targetClass.getContainingFile();
        if (file == null) return false;

        for (PsiFile f : targetClass.getContainingFile().getContainingDirectory().getFiles()) {
            if (f.equals(file)) continue;
            else {
                for (PsiClass c : ((PsiClassOwner) f).getClasses()) classList.add(c);
            }
        }

        // PsiAssignmentExpression case
        List<PsiAssignmentExpression> aexpList = FindPsi.findPsiAssignmentExpressions(targetClass);
        for (PsiAssignmentExpression aexp: aexpList) {
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(aexp);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp)) return true;
        }

        // PsiDeclarationStatement case
        List<PsiDeclarationStatement> dstmList = FindPsi.findPsiDeclarationStatements(targetClass);
        for (PsiDeclarationStatement dstm : dstmList) {
            List<PsiLocalVariable> lvList = FindPsi.findChildPsiLocalVariables(dstm);
            if (lvList.size() != 1) continue;

            PsiLocalVariable lvar = lvList.get(0);
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(lvar);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp)) return true;
        }

        // PsiField case
        List<PsiField> fList = FindPsi.findPsiFields(targetClass);
        for (PsiField f : fList) {
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(f);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp)) return true;
        }

        return false;
    }

    /**
     * Check if MethodCallExp is suitable for refactoring.
     * The refactorable format is like "A.getB().getC()"
     *
     * @param _mcexp PsiMethodCallExpression
     * @return true if the PsiMethodCallExpression is suitable format
     */
    private static boolean isDoubleMethodCallExp(PsiMethodCallExpression _mcexp) {
        List<PsiMethodCallExpression> mcexpList;
        List<PsiReferenceExpression> rexpList;
        PsiMethodCallExpression mcexp;
        PsiReferenceExpression rexp;

        // Firt Method Call Expression Check: A.getB().getC()
        if (!isReturnTypeExistedAsClass(_mcexp.getType())) return false;
        if (!isMethodOnlyReturnStatement(_mcexp)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(_mcexp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(_mcexp).get(0).getText().equals("()")) return false;

        rexp = rexpList.get(0);
        mcexpList = FindPsi.findChildPsiMethodCallExpressions(rexp);
        if (mcexpList.size() != 1) return false;

        // Seconde Method Call Expression Check: A.getB()
        mcexp = mcexpList.get(0);
        if (!isReturnTypeExistedAsClass(mcexp.getType())) return false;
        if (!isMethodOnlyReturnStatement(mcexp)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(mcexp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(mcexp).get(0).getText().equals("()")) return false;

        // Last Reference Expression: A
        rexp = rexpList.get(0);
        rexpList = FindPsi.findChildPsiReferenceExpressions(rexp);
        if (rexpList.size() != 1) return false;


        targetMethodCallExp = _mcexp;
        targetClassName = rexpList.get(0).getType().getPresentableText();
        return true;
    }

    /**
     * Check if the method pointed by MethodCallExp consists of only PsiReturnStatement.
     *
     * @param mcexp PsiMethodCallExpression
     * @return true if the method pointed by MethodCallExp consists of only PsiReturnStatement
     */
    private static boolean isMethodOnlyReturnStatement(PsiMethodCallExpression mcexp) {
        String ReturnType = mcexp.getMethodExpression().getQualifierExpression().getType().getPresentableText();
        String[] MethodNames = mcexp.getText().replace("()", "").split("\\.");
        String LastMethodName = MethodNames[MethodNames.length - 1];

        for (PsiClass cls : classList) {
            if (ReturnType.equals(cls.getName())) {
                for (PsiMethod m : FindPsi.findPsiMethods(cls)) {
                    if (m.getName().equals(LastMethodName)) {
                        PsiCodeBlock cb = m.getBody();
                        if (cb.getStatementCount() == 1 && cb.getStatements()[0] instanceof PsiReturnStatement)
                            return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if the class type exists in classList.
     *
     * @param type PsiType
     * @return true if the class type exists in classList
     */
    private static boolean isReturnTypeExistedAsClass(PsiType type) {
        for (PsiClass cls : classList) {
            if (type.getPresentableText().equals(cls.getName()))
                return true;
        }
        return false;
    }


    /**
     * Method that performs refactoring: 'Hide Delegate'
     *
     * How to implement this refactoring:
     * 1. transfer targetMethodCallExp into calling once method call.
     * 2. Add a new method about getting at the end of the class to targetClassName
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    public void refactor(AnActionEvent e)
    {
        Project project = e.getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            String[] splited = targetMethodCallExp.getText().replace("()", "").split("\\.");
            String replaced = splited[0] + "." + splited[2] + "()";
            String type = targetMethodCallExp.getType().getPresentableText();
            PsiExpression exp = factory.createExpressionFromText(replaced, null);
            targetMethodCallExp.replace(exp);

            PsiClass targetClass = null;
            String referenceName = null;
            for (PsiClass cls : classList) {
                if (targetClassName.equals(cls.getName())) {
                    for (PsiMethod m : FindPsi.findPsiMethods(cls)) {
                        if (splited[1].equals(m.getName())) {
                            referenceName = m.getBody().getStatements()[0].getText();
                            targetClass = cls;
                        }
                    }
                }
            }

            referenceName = referenceName.substring(0, referenceName.length() - 1);


            String Method = "public " + type + " " + splited[2] + "() {\n" +
                            referenceName + "." + splited[2] + "();\n" +
                            "}";

            PsiMethod method = factory.createMethodFromText(Method, null);

            targetClass.addBefore(method, targetClass.getLastChild());

        });
    }
}
