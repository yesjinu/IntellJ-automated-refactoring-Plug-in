package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import wanted.utils.FindPsi;
import wanted.utils.NavigatePsi;

import java.util.HashSet;
import java.util.Set;

public class IntroduceAssertion extends BaseRefactorAction {

    private Project project;
    private PsiClass targetClass;

    private PsiIfStatement ifStatement;

    /**
     * Returns the story name as a string format, for message.
     *
     * @return story name as a string format
     * @see BaseRefactorAction#storyName()
     */
    @Override
    public String storyName() {
        return "Introduce Assertion";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Introduce Assertion'.
     *
     * @param e AnActionevent
     * @return true if method is refactorable
     * @see BaseRefactorAction#refactorValid(AnActionEvent)
     */
    @Override
    public boolean refactorValid(AnActionEvent e) {
        NavigatePsi navigator = NavigatePsi.NavigatorFactory(e);

        project = navigator.findProject();
        targetClass = navigator.findClass();
        if(targetClass==null) return false;

        int offset = e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset();
        ifStatement = FindPsi.findIfStatement(targetClass, offset);
        if (ifStatement == null) return false;
        while (ifStatement.getParent() instanceof PsiIfStatement) ifStatement = (PsiIfStatement) ifStatement.getParent();

        return refactorValid(ifStatement);
    }

    /**
     * Determine whether PsiIfStatement object can refactor
     *
     * @param s the target which should be validated
     * @return true if s is valid to refactor
     */
    public static boolean refactorValid(PsiIfStatement s) {
        PsiStatement thenStatement = s.getThenBranch();
        PsiStatement elseStatement = s.getElseBranch();
        if (elseStatement instanceof PsiIfStatement) return false;

        Set<PsiReferenceExpression> thenSet = getReferenceSet(thenStatement);
        Set<PsiReferenceExpression> elseSet = getReferenceSet(elseStatement);

        if (thenSet.size() + elseSet.size() > 0) return true;
        else return false;
    }

    /**
     * Method that performs refactoring: 'Introduce Assertion'
     *
     * @param e AnActionEvent
     * @see BaseRefactorAction#refactor(AnActionEvent)
     */
    @Override
    protected void refactor(AnActionEvent e) {
        PsiStatement thenStatement = ifStatement.getThenBranch();
        PsiStatement elseStatement = ifStatement.getElseBranch();

        Set<PsiReferenceExpression> thenSet = getReferenceSet(thenStatement);
        Set<PsiReferenceExpression> elseSet = getReferenceSet(elseStatement);

    }

    /**
     * Method that get ReferenceExpression set by given statement
     * However, if one is parent of another, excluded.
     *
     * @param s given statement
     * @return set of PsiReferenceExpression
     */
    private static Set<PsiReferenceExpression> getReferenceSet(PsiStatement s) {
        Set<PsiReferenceExpression> referenceSet = FindPsi.findReferenceExpression(s);
        Set<PsiReferenceExpression> newSet = new HashSet<>();

        for (PsiReferenceExpression me : referenceSet) {
            boolean putThis = true;
            for (PsiReferenceExpression other : referenceSet) {
                if (me.equals(other)) continue;
                if (me.getTextRange().contains(other.getTextRange())) {
                    putThis = false;
                    break;
                }
            }
            if (putThis) newSet.add(me);
        }

        return newSet;
    }
}
