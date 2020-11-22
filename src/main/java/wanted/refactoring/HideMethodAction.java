package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import wanted.utils.*;

import java.util.*;
import java.util.stream.Collectors;

/* class to provide self encapsulate field refactoring */
public class HideMethodAction extends BaseRefactorAction {

    @Override
    public boolean refactorValid(AnActionEvent e)
    {
        Project project = e.getProject();

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        return _isRefactorable(documentManager.getPsiFile(editor.getDocument()));
    }

    boolean _isRefactorable(PsiFile psiFile) {
        if (psiFile == null) {
            return false;
        }

        return !getRefactorableMethods(psiFile).isEmpty();
    }

    private Set<PsiMethod> getRefactorableMethods(PsiFile psiFile) {
        PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();

        Map<PsiMethodCallExpression, PsiClass> callMap = new HashMap<>();
        Map<PsiMethod, PsiClass> methodMap = new HashMap<>();

        for (PsiClass aClass : classes) {
            List<PsiMethodCallExpression> methodCallExpressions = compare.findPsiMethodCallExpression(aClass);

            methodCallExpressions.forEach(methodCallExpression -> callMap.put(methodCallExpression, aClass));

            Arrays.stream(aClass.getMethods()).forEach(method -> methodMap.put(method, aClass));
        }


        for (PsiMethodCallExpression methodCallExpression : callMap.keySet()) {
            PsiMethod method = methodCallExpression.resolveMethod();

            if (methodMap.get(method) != callMap.get(methodCallExpression)) {
                methodMap.remove(method);
            }
        }

        Set<PsiMethod> methodSet = methodMap.keySet().stream().filter(method -> !method.getModifierList().hasModifierProperty("private"))
                .collect(Collectors.toSet());

        return methodSet;
    }

    @Override
    protected void refactor(AnActionEvent e)
    {
        Project project = e.getProject();

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        runRefactoring(documentManager.getPsiFile(editor.getDocument()));
    }

    public void runRefactoring(PsiFile file) {
        Set<PsiMethod> methodSet = getRefactorableMethods(file);

        for (PsiMethod method : methodSet) {
            WriteCommandAction.runWriteCommandAction(method.getProject(), () -> {
                method.getModifierList().setModifierProperty("private", true);
            });
        }
    }

    @Override
    public String storyName()
    {
        return "Self Encapsulation Field";
    }
}
