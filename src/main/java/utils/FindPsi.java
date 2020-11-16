package utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.*;

/* Class to find specific Psi element */
public class FindPsi {
    private Project focusProject;
    private PsiFile focusFile;
    private PsiClass focusClass;
    private PsiMethod focusMethod;

    /* get focusProject, File, Class from given event */
    public FindPsi(AnActionEvent e)
    {
        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE);
        // assume file always contains one class which has only one method
        assert focusFile != null;
        focusClass = ((PsiClassOwner)focusFile).getClasses()[0];
        focusMethod = focusClass.getMethods()[0];
    }

    /* returns list of private members from focused class */
    public List<PsiField> findPrivateField()
    {
        List<PsiField> ret = new ArrayList<>();

        // assume class always contains one field
        for(PsiField f : focusClass.getFields())
        {
            if(f.getModifierList().hasModifierProperty("private"))
            {
                ret.add(f);
                break;
            }
        }

        return ret;
    }

    /* returns list of statements referring to given member */
    public List<PsiStatement> findMemberStatement(PsiField member)
    {
        List<PsiStatement> ret = new ArrayList<>();

        // assume class has more than one method
        for(PsiMethod m : focusClass.getMethods())
        {
            PsiCodeBlock c = m.getBody();
            assert c != null;
            for(PsiStatement s : c.getStatements())
            {
                /* TODO: find statements by using reference list */
                if(((PsiExpressionStatement)s).getExpression().toString().contains(member.getName()))
                {
                    ret.add(s);
                }
            }
        }

        return ret;
    }

    /* returns list of parameters that passed to the method */
    public Set<PsiParameter> findParametersOfMethod()
    {
        Set<PsiParameter> result = new HashSet<>();

        // assume class always contains one field
        if (focusMethod.hasParameters()) {
            result.addAll(Arrays.asList(focusMethod.getParameterList().getParameters()));
        }
        return result;
    }


    /* returns list of reference expressions(symbols) in a method */
    // TODO: method 안에서 PsiReferenceExpression을 찾아야 함
   public Set<PsiReferenceExpression> findReferenceUsedInMethod()
    {
        Set<PsiReferenceExpression> result = new HashSet<>();

        PsiCodeBlock codeBlock = focusMethod.getBody();
        assert codeBlock != null;
        for (PsiStatement p : codeBlock.getStatements()) {
            p.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(PsiReferenceExpression expression)
                {
                    super.visitReferenceExpression(expression);
                    result.add(expression);
                }
            });
        }
        return result;
    }

}
