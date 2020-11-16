package refactoring;

import utils.AddPsi;
import utils.ReplacePsi;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.List;

/* Runnable object of self encapsulate field */
public class SEFRunner implements Runnable{
    private static SEFRunner runner;
    private Project project;
    private PsiClass targetClass;
    private List<PsiElement> addList;
    private List<PsiReferenceExpression> reference;
    private PsiField member;

    /**
     * Initialize runner
     * @param project       focused project
     * @param targetClass   class to refactor
     * @param addList       methods to add
     * @param reference    reference expression to replace
     * @param member        chosen member field
     */
    public SEFRunner(Project project, PsiClass targetClass, List<PsiElement> addList, List<PsiReferenceExpression> reference, PsiField member)
    {
        this.project = project;
        this.targetClass = targetClass;
        this.addList = addList;
        this.reference = reference;
        this.member = member;
    }


    /**
     * WriteCommandAction run() for Self Encapsulate Field
     */
    @Override
    public void run(){
        AddPsi.addMethod(targetClass, addList); // add method in addList to targetClass
        ReplacePsi.encapFied(project, (PsiMethod)addList.get(0), (PsiMethod)addList.get(1), reference, member); // encapsulate with getter and setter
    }
}
