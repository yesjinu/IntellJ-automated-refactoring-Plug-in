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
    private Project project;
    private PsiFile file;

    private static PsiMethodCallExpression targetMethodCallExp;
    private static PsiClass targetRefactorClass;
    private static PsiClass targetClass;
    private static PsiType targetType;
    private static String reference;

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
    public String description() {
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

        project = navigator.findProject();
        if (project == null) return false;

        file = navigator.findFile();
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
     * @param _targetClass PsiField Object
     * @return true if method is refactorable
     */
    public static boolean refactorValid(@NotNull PsiClass _targetClass) {
        targetMethodCallExp = null;
        targetRefactorClass = null;
        targetClass = null;
        targetType = null;

        // PsiAssignmentExpression case
        List<PsiAssignmentExpression> aexpList = FindPsi.findPsiAssignmentExpressions(_targetClass);
        for (PsiAssignmentExpression aexp: aexpList) {
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(aexp);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp, _targetClass)) return true;
        }

        // PsiDeclarationStatement case
        List<PsiDeclarationStatement> dstmList = FindPsi.findPsiDeclarationStatements(_targetClass);
        for (PsiDeclarationStatement dstm : dstmList) {
            List<PsiLocalVariable> lvList = FindPsi.findChildPsiLocalVariables(dstm);
            if (lvList.size() != 1) continue;

            PsiLocalVariable lvar = lvList.get(0);
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(lvar);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp, _targetClass)) return true;
        }

        // PsiField case
        List<PsiField> fList = FindPsi.findPsiFields(_targetClass);
        for (PsiField f : fList) {
            List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(f);
            if (mcexpList.size() != 1) continue;

            PsiMethodCallExpression mcexp = mcexpList.get(0);
            // Check the MethodCallExp is suitable for refactoring
            if (isDoubleMethodCallExp(mcexp, _targetClass)) return true;
        }

        return false;
    }

    /**
     * Check if MethodCallExp is suitable for refactoring.
     * The refactorable format is like "A.getB().getC()"
     *
     * @param _targetExp PsiMethodCallExpression
     * @param _targetClass PsiClass
     * @return true if the PsiMethodCallExpression is suitable format
     */
    private static boolean isDoubleMethodCallExp(PsiMethodCallExpression _targetExp, PsiClass _targetClass) {
        List<PsiMethodCallExpression> mcexpList;
        List<PsiReferenceExpression> rexpList;
        PsiMethodCallExpression mcexp;
        PsiReferenceExpression rexp;

        // Firt Method Call Expression Check: A.getB().getC()
        if (_targetExp.getType() == null) return false;
        if (isReturnTypeExistedAsClass(_targetExp.getType(), _targetClass) == null) return false;
        if (!isMethodOnlyReturnStatement(_targetExp, _targetClass)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(_targetExp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(_targetExp).get(0).getText().equals("()")) return false;

        rexp = rexpList.get(0);
        mcexpList = FindPsi.findChildPsiMethodCallExpressions(rexp);
        if (mcexpList.size() != 1) return false;

        // Seconde Method Call Expression Check: A.getB()
        mcexp = mcexpList.get(0);
        if (_targetExp.getType() == null) return false;
        if (isReturnTypeExistedAsClass(mcexp.getType(), _targetClass) == null) return false;
        if (!isMethodOnlyReturnStatement(mcexp, _targetClass)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(mcexp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(mcexp).get(0).getText().equals("()")) return false;

        // Last Reference Expression: A
        rexp = rexpList.get(0);
        rexpList = FindPsi.findChildPsiReferenceExpressions(rexp);
        if (rexpList.size() != 1) return false;
        if (rexpList.get(0).getType() == null) return false;

        PsiElement elem1 = rexpList.get(0).getOriginalElement();
        PsiReference elem2 = rexpList.get(0).getReference();


        // find targetRefactorClass
        if (isReturnTypeExistedAsClass(rexpList.get(0).getType(), _targetClass) == null) return false;

        targetRefactorClass = isReturnTypeExistedAsClass(rexpList.get(0).getType(), _targetClass);
        targetMethodCallExp = _targetExp;
        targetClass = _targetClass;
        targetType = rexpList.get(0).getType();

        String[] splited = targetMethodCallExp.getText().replace("()", "").split("\\.");
        for (PsiMethod m : targetRefactorClass.getMethods()) {
            if (splited[1].equals(m.getName())) {
                reference = m.getBody().getStatements()[0].getText();
                reference = reference.substring(0, reference.length() - 1);
            }
        }

        return true;
    }

    /**
     * Check if the method pointed by MethodCallExp consists of only PsiReturnStatement.
     *
     * @param _targetExp PsiMethodCallExpression
     * @param _targetClass PsiClass
     * @return true if the method pointed by MethodCallExp consists of only PsiReturnStatement
     */
    private static boolean isMethodOnlyReturnStatement(PsiMethodCallExpression _targetExp, PsiClass _targetClass) {
        String ReturnType = _targetExp.getMethodExpression().getQualifierExpression().getType().getPresentableText();
        String[] MethodNames = _targetExp.getText().replace("()", "").split("\\.");
        String LastMethodName = MethodNames[MethodNames.length - 1];

        PsiFile file = _targetClass.getContainingFile();
        if (file == null) return false;

        List<PsiClass> classList = new ArrayList<>();
        for (PsiFile f : _targetClass.getContainingFile().getContainingDirectory().getFiles()) {
            if (f.equals(file)) continue;
            else {
                for (PsiClass c : ((PsiClassOwner) f).getClasses()) classList.add(c);
            }
        }

        for (PsiClass cls : classList) {
            if (ReturnType.equals(cls.getName())) {
                for (PsiMethod m : cls.getMethods()) {
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
     * @param _type PsiType
     * @param _targetClass PsiClass
     * @return PsiClass if the class type exists in classList
     */
    private static PsiClass isReturnTypeExistedAsClass(PsiType _type, PsiClass _targetClass) {
        PsiFile file = _targetClass.getContainingFile();
        if (file == null) return null;

        List<PsiClass> classList = new ArrayList<>();
        for (PsiFile f : _targetClass.getContainingFile().getContainingDirectory().getFiles()) {
            if (f.equals(file)) continue;
            else {
                for (PsiClass c : ((PsiClassOwner) f).getClasses()) classList.add(c);
            }
        }

        for (PsiClass cls : classList) {
            if (_type.getPresentableText().equals(cls.getName()))
                return cls;
        }
        return null;
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
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        String[] splited = targetMethodCallExp.getText().replace("()", "").split("\\.");
        String replaced = splited[0] + "." + splited[2] + "()";
        String type = targetMethodCallExp.getType().getPresentableText();
        PsiExpression exp = factory.createExpressionFromText(replaced, null);

        String Method = "public " + type + " " + splited[2] + "() {\n" +
                reference + "." + splited[2] + "();\n" +
                "}";

        PsiMethod method = factory.createMethodFromText(Method, null);
        PsiClass cls = isReturnTypeExistedAsClass(targetType, targetClass);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            targetMethodCallExp.replace(exp);
            cls.addBefore(method, cls.getLastChild());
        });
    }
}
