package model.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

/* Class to find specific Psi element */
public class FindPsi {
    private Project focusProject;
    private PsiFile focusFile;
    private PsiClass focusClass;

    /* get focusProject, File, Class from given event */
    public FindPsi(AnActionEvent e)
    {
        focusProject = e.getData(PlatformDataKeys.PROJECT);
        focusFile = e.getData(LangDataKeys.PSI_FILE);
        // assume file always contains one class
        focusClass = ((PsiClassOwner)focusFile).getClasses()[0];
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

}
