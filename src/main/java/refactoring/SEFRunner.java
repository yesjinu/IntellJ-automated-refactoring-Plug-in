package refactoring;

import utils.AddPsi;
import utils.ReplacePsi;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.List;

/* Runner object of self encapsulate field */
public class SEFRunner implements Runnable{
    private PsiClass targetClass;
    private Project project;
    private List<PsiElement> addList;
    private List<PsiReferenceExpression> statements;
    private PsiField member;

    /**
     * Initialize runner
     * @param project
     * @param targetClass
     * @param addList
     * @param statements
     * @param member
     */
    public SEFRunner(Project project, PsiClass targetClass, List<PsiElement> addList, List<PsiReferenceExpression> statements, PsiField member)
    {
        this.project = project;
        this.targetClass = targetClass;
        this.addList = addList;
        this.statements = statements;
        this.member = member;
    }

    @Override
    public void run(){
        AddPsi.addMethod(targetClass, addList);
        ReplacePsi.encapFied(project, (PsiMethod)addList.get(0), (PsiMethod)addList.get(1), statements, member);
    }
}
