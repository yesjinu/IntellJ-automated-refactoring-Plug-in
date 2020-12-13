package wanted.refactoring;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UContinueExpression;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.*;

/**
 * Class to provide refactoring: 'Introduce Local Extension'
 * - Create new class that inherits the utility,
 * - Add the method required by the user in the class,
 *
 * @author Chanyoung Kim
 */
public class HideDelegateAction extends BaseRefactorAction {
    private static List<PsiClass> classList = new ArrayList<>();
    private static PsiMethodCallExpression firstMethodCall;
    private static PsiMethodCallExpression secondMethodCall;

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String descripton() {
        return "<html>, <br/>" +
                ".</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>, <br/>" +
                ".</html>";
    }


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

        return refactorValid(project, targetClass);
    }


    public static boolean refactorValid(Project project, @NotNull PsiClass targetClass) {
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
        if (!aexpList.isEmpty()) {
            for (PsiAssignmentExpression aexp: aexpList) {
                // Firt Method Call Expression Check: A.getB().getC()
                List<PsiMethodCallExpression> mcexpList = FindPsi.findChildPsiMethodCallExpressions(aexp);
                if (mcexpList.size() != 1) continue;

                PsiMethodCallExpression mcexp = mcexpList.get(0);
                if (!isDoubleMethodCallExp(mcexp)) continue;
            }
        }

        

        // PsiDeclarationStatement case
        List<PsiDeclarationStatement> dsttList = FindPsi.findPsiDeclarationStatements(targetClass);
        if (!dsttList.isEmpty()) {

        }
        return false;
    }

    private static boolean isDoubleMethodCallExp(PsiMethodCallExpression _mcexp) {
        List<PsiMethodCallExpression> mcexpList;
        List<PsiReferenceExpression> rexpList;
        PsiMethodCallExpression mcexp;
        PsiReferenceExpression rexp;
        PsiType first;
        PsiType second;

        // Firt Method Call Expression Check: A.getB().getC()
        mcexp = _mcexp;
        first = mcexp.getType();
        if (!isTypeExistedAsClass(first)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(mcexp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(mcexp).get(0).getText().equals("()")) return false;

        rexp = rexpList.get(0);
        mcexpList = FindPsi.findChildPsiMethodCallExpressions(rexp);
        if (mcexpList.size() != 1) return false;

        // Seconde Method Call Expression Check: A.getB()
        mcexp = mcexpList.get(0);
        second = mcexp.getType();
        if (!isTypeExistedAsClass(second)) return false;

        rexpList = FindPsi.findChildPsiReferenceExpressions(mcexp);
        if (rexpList.size() != 1) return false;
        if (!FindPsi.findChildPsiExpressionLists(mcexp).get(0).getText().equals("()")) return false;

        // Last Reference Expression: A
        rexp = rexpList.get(0);
        rexpList = FindPsi.findChildPsiReferenceExpressions(rexp);
        if (rexpList.size() != 1) return false;

        // Check the subject type same with the first Type
        if (!rexpList.get(0).getType().equals(first)) return false;

        firstMethodCall = _mcexp;
        secondMethodCall = mcexp;

        return true;
    }

    private static boolean isTypeExistedAsClass(PsiType type) {
        for (PsiClass cls : classList) {
            if (type.getPresentableText().equals(cls.getName()))
                return true;
        }

        return false;
    }

    /**
     * Method that performs refactoring: 'Introduct Local Extension'
     *
     * How to implement this refactoring:
     * 1. Change it to a new PsiDeclarationStatement.
     * 2. Add a new method at the end of the class to which the method belongs.
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

        });
    }
}
