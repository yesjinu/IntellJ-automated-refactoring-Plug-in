package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import wanted.utils.FindPsi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Add the method to a client class and pass an object of the utility class to it as an argument.
 *
 * When does this refactoring activiate
 * 1. 클래스 내부 함수에서 유틸리티 객체가 선언되고, 선언될 때 파라미터로 온전히 그 객체가 사용된 경우
 *
 * How to implement this refactoring
 * 1.
 *
 * @author Chanyoung Kim
 */
public class IntroduceForeignMethodAction extends BaseRefactorAction {
    PsiFile psiFile;
    List<PsiClass> psiClasses;
    Map<PsiMethod, PsiClass> psiMethodMap;
    Map<PsiDeclarationStatement, PsiMethod> psiDclStateMap;

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        Project project = e.getProject();

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        psiFile = documentManager.getPsiFile(editor.getDocument());
        if (psiFile == null) return false;

        psiClasses = FindPsi.findPsiClasses(psiFile);
        if (psiClasses.isEmpty()) return false;

        psiMethodMap = new HashMap<>();
        for (PsiClass cls : psiClasses)
            Arrays.stream(cls.getMethods()).forEach(method -> psiMethodMap.put(method, cls));
        if (psiMethodMap.isEmpty()) return false;

        // 모든 메소드 내에 PsiDeclarationStatement를 찾아 HashMap에 저장한다.
        psiDclStateMap = new HashMap<>();
        for (PsiMethod method : psiMethodMap.keySet()) {
            List<PsiDeclarationStatement> psiDclStateList = FindPsi.findPsiDeclarationStatements(method);
            if (!psiDclStateList.isEmpty())
                psiDclStateList.forEach(psiDclState -> psiDclStateMap.put(psiDclState, method));
        }
        if (psiDclStateMap.isEmpty()) return false;

        /**
         * 각 PsiDeclarationStatement에서
         * 1. PsiTypeElement가 PsiJavaCodeReferenceElement인지 확인한다. (PsiKeyword는 제외)
         *    그리고 해당 Reference Type을 저장한다.
         *
         * 2. PsiNewExpression의 존재를 확인한다.
         *    PsiNewExpression 내부의 PsiExpressionList 내
         * */
        for (PsiDeclarationStatement statement : psiDclStateMap.keySet()) {
            PsiLocalVariable localVariable = FindPsi.findChildPsiLocalVariables(statement).get(0);
            PsiTypeElement typeElement =
        }

        return true;
    }


    @Override
    protected void refactor(AnActionEvent e)
    {
        Project project = e.getProject();

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

    }


    @Override
    public String storyName()
    {
        return "Introduce Foreign Method";
    }
}
