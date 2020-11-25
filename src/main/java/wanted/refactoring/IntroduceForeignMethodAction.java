package wanted.refactoring;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import wanted.utils.FindPsi;

import java.util.*;

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

    String variableName;
    String utilityClassType;
    String utilityClassName;
    Map<PsiDeclarationStatement, List<String>> params;
    Map<PsiDeclarationStatement, Integer> paramCounts;
    List<PsiDeclarationStatement> possible;

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
        params = new HashMap<>();
        paramCounts = new HashMap<>();
        possible = new ArrayList<>();
        for (PsiDeclarationStatement stm : psiDclStateMap.keySet()) {
            List<PsiLocalVariable> lvList = FindPsi.findChildPsiLocalVariables(stm);
            if (lvList.size() != 1) continue;

            PsiLocalVariable lv = lvList.get(0);
            List<PsiTypeElement> teList = FindPsi.findChildPsiTypeElements(lv);
            if (teList.size() != 1) continue;

            List<PsiIdentifier> iList = FindPsi.findChildPsiIdentifiers(lv);
            if (iList.size() != 1) continue;
            variableName = iList.get(0).getText();

            PsiTypeElement te = teList.get(0);
            if (FindPsi.findChildPsiJavaCodeReferenceElements(te).size() != 1) continue;
            utilityClassType = te.getText();

            List<PsiNewExpression> neList = FindPsi.findChildPsiNewExpressions(lv);
            if (neList.size() != 1) continue;

            PsiNewExpression ne = neList.get(0);
            List<PsiJavaCodeReferenceElement> jcreList = FindPsi.findChildPsiJavaCodeReferenceElements(te);
            if (jcreList.size() != 1) continue;
            PsiJavaCodeReferenceElement jcre = jcreList.get(0);
            if (!jcre.getText().equals(utilityClassType)) continue;
            List<PsiExpressionList> elList = FindPsi.findChildPsiExpressionLists(ne);
            if (elList.size() != 1) continue;

            PsiExpressionList el = elList.get(0);
            List<PsiExpression> eList = FindPsi.findChildPsiExpressions(el);
            if (eList.size() == 0) continue;

            paramCounts.put(stm, eList.size());
            params.put(stm, new ArrayList<>());
            for (PsiExpression exp : eList) {
                if(!isVaildParameter(exp, stm)) break;
            }

            if (params.get(stm).size() == paramCounts.get(stm) && utilityClassName != null) {
                possible.add(stm);
            }
        }

        return !possible.isEmpty();
    }

    private boolean isVaildParameter(PsiExpression exp, PsiDeclarationStatement stm) {
        if (exp instanceof PsiMethodCallExpression) {
            return isVaildParameter((PsiMethodCallExpression) exp, stm);
        }
        else if (exp instanceof PsiBinaryExpression) {
            return isVaildParameter((PsiBinaryExpression) exp, stm);
        }
        else if (exp instanceof PsiReferenceExpression) {
            return isVaildParameter((PsiReferenceExpression) exp, stm);
        }
        else if (exp instanceof PsiLiteralExpression) {
            return isVaildParameter((PsiLiteralExpression) exp, stm);
        }

        return false;
    }


    private boolean isVaildParameter(PsiMethodCallExpression exp, PsiDeclarationStatement stm) {
        List<PsiReferenceExpression> frontReList = FindPsi.findChildPsiReferenceExpressions(exp);
        if (frontReList.size() != 1) return false;

        List<PsiExpressionList> elList = FindPsi.findChildPsiExpressionLists(exp);
        if (elList.size() != 1) return false;

        PsiReferenceExpression frontRe = frontReList.get(0);
        List<PsiReferenceExpression> behindReList = FindPsi.findChildPsiReferenceExpressions(frontRe);
        if (behindReList.size() != 1) return false;

        PsiExpressionList el = elList.get(0);
        List<PsiExpression> eList = FindPsi.findChildPsiExpressions(el);
        if (eList.size() != 0) return false;

        PsiReferenceExpression BackRe = behindReList.get(0);
        if (isVaildParameter(BackRe, stm)) {
            utilityClassName = BackRe.getText();
            String param = FindPsi.findChildPsiIdentifiers(frontRe).get(0).getText();
            params.get(stm).add("arg." + param + "()");
            return true;
        }
        return false;
    }

    private boolean isVaildParameter(PsiReferenceExpression exp, PsiDeclarationStatement stm) {
        PsiMethod m = psiDclStateMap.get(stm);
        PsiClass c = psiMethodMap.get(m);

        List<PsiField> fList = FindPsi.findPsiFields(c);
        for (PsiField f : fList) {
            if (!f.getName().equals(exp.getText())) continue;
            else {
                List<PsiTypeElement> innerTeList = FindPsi.findChildPsiTypeElements(f);
                if (innerTeList.size() != 1) return false;

                PsiTypeElement innerTe = innerTeList.get(0);
                if (innerTe.getText().equals(utilityClassType)) {
                    return true;
                }
                else return false;
            }
        }
        return false;
    }

    private boolean isVaildParameter(PsiBinaryExpression exp, PsiDeclarationStatement stm) {
        List<PsiExpression> expList = FindPsi.findChildPsiExpressions(exp);
        PsiJavaToken token = FindPsi.findChildPsiJavaTokens(exp).get(0);

        PsiExpression left = expList.get(0);
        PsiExpression right = expList.get(1);

        String param = "";

        if (isVaildParameter(left, stm)) {
            param += params.get(stm).remove(params.get(stm).size() - 1) + token.getText();
            if (isVaildParameter(right, stm)) {
                param += params.get(stm).remove(params.get(stm).size() - 1);
                params.get(stm).add(param);
            }
        }
        return false;
    }

    private boolean isVaildParameter(PsiLiteralExpression exp, PsiDeclarationStatement stm) {
        params.get(stm).add(exp.getText());
        return true;
    }

    @Override
    protected void refactor(AnActionEvent e)
    {
        Project project = e.getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiDeclarationStatement stm : possible) {
                PsiMethod mtd = psiDclStateMap.get(stm);
                PsiClass cls = psiMethodMap.get(mtd);

                String statement = utilityClassType + " " + variableName + " = " + variableName
                        + "(" + utilityClassName + ");";
                PsiStatement _stm = factory.createStatementFromText(statement, null);
                stm.replace(_stm);

                String strMethod = "private static " + utilityClassType + " " + variableName + "(" + utilityClassType
                                    + " arg) { return new " + utilityClassType + "("
                                    + StringUtils.join(params.get(stm), ", ") + "); }";
                PsiMethod _mtd = factory.createMethodFromText(strMethod, null);
                cls.addBefore(_mtd, cls.getLastChild());
            }
        });


    }


    @Override
    public String storyName()
    {
        return "Introduce Foreign Method";
    }
}
