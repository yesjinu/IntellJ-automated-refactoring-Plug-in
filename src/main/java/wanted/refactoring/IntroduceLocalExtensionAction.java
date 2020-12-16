package wanted.refactoring;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
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
public class IntroduceLocalExtensionAction extends BaseRefactorAction {
    private static Project project;
    private static PsiFile file;
    private static PsiClass targetClass;

    private static Map<PsiMethod, PsiClass> psiMethodMap = new HashMap<>();
    private static Map<PsiDeclarationStatement, PsiMethod> psiDclStateMap = new HashMap<>();

    private static String variableName;
    private static String utilityClassType;
    private static String utilityClassName;
    private static List<String> params = new ArrayList<>();
    private static Integer paramCounts;
    private static PsiDeclarationStatement targetStm;
    private static List<String> utilityClassParamsType = new ArrayList<>();

    /* Returns the story ID. */
    @Override
    public String storyID() {
        return "ILE";
    }

    /* Returns the story name as a string format, for message. */
    @Override
    public String storyName() {
        return "Introduce Local Extension";
    }

    /* Returns the description of each story. (in html-style) */
    @Override
    public String description() {
        return "<html>When a utility class is newly declared as a utility class variable inside the user class, <br/>" +
                "Add a method that receives a utility class in the user class, transforms it, and returns it.</html>";
    }

    /* Returns the precondition of each story. (in html-style) */
    @Override
    public String precondition() {
        return "<html>Make sure that when declaring a utility class through new expression, <br/>" +
                "literal variable or an existing utility class variable must be used as a parameter.</html>";
    }

    /**
     * Method that checks whether candidate method is refactorable
     * using 'Introduce Foreign Method'.
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

        targetClass = navigator.findClass();
        if (targetClass == null) return false;

        return refactorValid(project, targetClass);
    }


    /**
     * Static method that checks whether candidate method is refactorable
     * using 'Introduce Local Extension'.
     *
     * When does this refactoring activiate,
     * -> a utility object is declared in a function inside a class, using existing utility object as a parameter
     *
     * How to implement this refactoring
     * 1. PsiClass -> PsiMethod -> PsiDeclarationStatement -> PsiLocalVariable : Indexing
     * 2. PsiLocalVariable -> PsiTypeElement : Check the presence of PsiJavaCodeReferenceElement for determine Utility class
     *                     -> PsiNewExpression : Check the presence of PsiNewExpression
     * 3. PsiNewExpression -> PsiJavaCodeReferenceElement : Check the Reference is same with before TypeElement
     *                     -> PsiExpressionList : Check if there is more than 1 parameter
     * 4. PsiExpressionList -> PsiExpression : Check whether each parameter is valid
     *
     * 해당 클래스 내부에 유틸리티 클래스에 대한 선언뿐만 아니라 초기화에 대한 정보도 있어야 한다.
     * Constructor 추가를 위해서 클래스 내부에서 모든 PsiNewExpression을 검사해야 한다.
     * 해당하는 것들을 모두 constructor로써 추가해준다.
     *
     * @param project Project
     * @param targetClass PsiField Object
     * @return true if method is refactorable
     */
    public static boolean refactorValid(Project project, @NotNull PsiClass targetClass) {
        psiMethodMap.clear();
        psiDclStateMap.clear();


        Arrays.stream(targetClass.getMethods()).forEach(method -> psiMethodMap.put(method, targetClass));
        if (psiMethodMap.isEmpty()) return false;

        // Find and save PsiDeclarationStatement in all class methods in the project.
        for (PsiMethod method : psiMethodMap.keySet()) {
            List<PsiDeclarationStatement> psiDclStateList = FindPsi.findPsiDeclarationStatements(method);
            if (!psiDclStateList.isEmpty())
                psiDclStateList.forEach(psiDclState -> psiDclStateMap.put(psiDclState, method));
        }
        if (psiDclStateMap.isEmpty()) return false;


        // Determine whether refactoring is possible for each PsiDeclarationStatement.
        for (PsiDeclarationStatement stm : psiDclStateMap.keySet()) {
            variableName = null;
            utilityClassName = null;
            utilityClassType = null;
            paramCounts = null;
            targetStm = null;
            params.clear();
            utilityClassParamsType.clear();

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

            paramCounts = eList.size();

            // Checks whether the parameter is valid, and if it is valid, it is stored in a global variable.
            for (PsiExpression exp : eList) {
                if(!isVaildParameter(exp, stm, targetClass)) break;
            }

            // 클래스 전체에서 해당 DeclarationStatement에 대해서 선언문이 있는지 확인한다.
            List<PsiNewExpression> newExpressionList = FindPsi.findPsiNewExpressions(targetClass);

            for (PsiNewExpression nexp : newExpressionList) {

                List<PsiJavaCodeReferenceElement> JCREList = FindPsi.findChildPsiJavaCodeReferenceElements(nexp);
                if (JCREList.size() != 1) continue;

                PsiJavaCodeReferenceElement JCRE = JCREList.get(0);
                if (!JCRE.getText().equals(utilityClassType)) continue;

                List<PsiExpressionList> ELList = FindPsi.findChildPsiExpressionLists(nexp);
                if (ELList.size() != 1) continue;

                PsiExpressionList EL = ELList.get(0);
                List<PsiExpression> EList = FindPsi.findChildPsiExpressions(EL);
                if (EList.size() != paramCounts) continue;
                List<PsiLiteralExpression> LEList = FindPsi.findChildPsiLiteralExpressions(EL);
                if (LEList.size() != paramCounts) continue;

                int count = 1;
                for (PsiExpression exp : LEList) {
                    utilityClassParamsType.add(exp.getType().getCanonicalText() + " arg" + Integer.toString(count++));
                }

                break;
            }

            // Determine whether the number of input parameters and the number of valid parameters are the same,
            // Check if an existing utility class is used.
            if (params.size() == paramCounts && utilityClassName != null && !(utilityClassParamsType.isEmpty())) {
                targetStm = stm;
                break;
            }
        }

        return !(targetStm == null);
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
    public void refactor(AnActionEvent e) {
        Project project = e.getProject();

        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiDeclarationStatement stm = targetStm;
            PsiMethod mtd = psiDclStateMap.get(stm);
            PsiClass cls = psiMethodMap.get(mtd);

            String className = "Modified" + utilityClassType;
            String classParams = StringUtils.join(utilityClassParamsType, ", ");
            int count = 1;
            List<String> temp = new ArrayList<>();
            for (int i = 0; i < utilityClassParamsType.size(); i++) {
                temp.add("arg" + Integer.toString(count++));
            }
            String superParams = StringUtils.join(temp, ", ");
            String newParams = StringUtils.join(params, ", ");
            newParams = newParams.replace("arg.", "");

            String insertClass = "class " + className + " extends " + utilityClassType +" {" +
                    "    public " + className + " (" + classParams + ") {" +
                    "        super(" + superParams + ");" +
                    "    }" +
                    "" +
                    "    " + className + " " + variableName + "() {" +
                    "        return new " + className + "(" + newParams + ");" +
                    "    }" +
                    "}";

            PsiFile javaFile = PsiFileFactory.getInstance(project).createFileFromText(Language.findLanguageByID("JAVA"), insertClass);
            PsiClass newTempClass = getFirstClassFromFile(javaFile);

            List<PsiJavaCodeReferenceElement> jcreList = FindPsi.findPsiJavaCodeReferenceElements(cls);
            for (PsiJavaCodeReferenceElement jcre : jcreList) {
                if (jcre.getText().equals(utilityClassType)) {
                    PsiJavaCodeReferenceElement replace = factory.createReferenceFromText(className, null);
                    jcre.replace(replace);
                }
            }

            file.add(newTempClass);

            // Create and replace a new PsiDeclarationStatement corresponding to the refactoring result.
            String statement = className + " " + variableName + " = " + utilityClassName
                    + "." + variableName + "();";
            PsiStatement _stm = factory.createStatementFromText(statement, null);
            stm.replace(_stm);



        });
    }

    /**
     * Method that return the first class of file
     *
     * @param psiFile PsiFile
     * @return psiElement that appears first in file, otherwier return null
     */
    private PsiClass getFirstClassFromFile(PsiFile psiFile) {
        for (PsiElement psiElement: psiFile.getChildren()) {
            if (psiElement instanceof PsiClass) {
                return (PsiClass) psiElement;
            }
        }

        return null;
    }

    /**
     * Helper Method that Check if Expression is in a form that can be refactored
     * Since each PsiExpression is a different processing method, it is processed recursively.
     *
     * @param exp PsiExpression
     * @param stm PsiDeclarationStatement
     * @param cls PsiClass
     * @return true if the parameter is valid
     */
    private static boolean isVaildParameter(PsiExpression exp, PsiDeclarationStatement stm, PsiClass cls) {
        if (exp instanceof PsiMethodCallExpression) {
            return isVaildParameter((PsiMethodCallExpression) exp, stm, cls);
        }
        else if (exp instanceof PsiBinaryExpression) {
            return isVaildParameter((PsiBinaryExpression) exp, stm, cls);
        }
        else if (exp instanceof PsiReferenceExpression) {
            return isVaildParameter((PsiReferenceExpression) exp, stm, cls);
        }
        else if (exp instanceof PsiLiteralExpression) {
            return isVaildParameter((PsiLiteralExpression) exp, stm, cls);
        }

        return false;
    }

    /**
     * Helper Method that Check if Expression is in a form that can be refactored
     *
     * It is possible Only example.method() form
     *
     * @param exp PsiMethodCallExpression
     * @param stm PsiDeclarationStatement
     * @param cls PsiClass
     * @return true if the parameter is valid
     */
    private static boolean isVaildParameter(PsiMethodCallExpression exp, PsiDeclarationStatement stm, PsiClass cls) {
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
        if (isVaildParameter(BackRe, stm, cls)) {
            utilityClassName = BackRe.getText();
            String param = FindPsi.findChildPsiIdentifiers(frontRe).get(0).getText();
            params.add("arg." + param + "()");
            return true;
        }
        return false;
    }

    /**
     * Helper Method that Check if Expression is in a form that can be refactored
     *
     * It is possible only when the reference is the same as the utility class.
     *
     * @param exp PsiReferenceExpression
     * @param stm PsiDeclarationStatement
     * @param cls PsiClass
     * @return true if the parameter is valid
     */
    private static boolean isVaildParameter(PsiReferenceExpression exp, PsiDeclarationStatement stm, PsiClass cls) {
        List<PsiField> fList = FindPsi.findPsiFields(cls);
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

    /**
     * Helper Method that Check if Expression is in a form that can be refactored
     *
     * Examine the left and right sides respectively.
     *
     * @param exp PsiBinaryExpression
     * @param stm PsiDeclarationStatement
     * @param cls PsiClass
     * @return true if the parameter is valid
     */
    private static boolean isVaildParameter(PsiBinaryExpression exp, PsiDeclarationStatement stm, PsiClass cls) {
        List<PsiExpression> expList = FindPsi.findChildPsiExpressions(exp);
        PsiJavaToken token = FindPsi.findChildPsiJavaTokens(exp).get(0);

        PsiExpression left = expList.get(0);
        PsiExpression right = expList.get(1);

        String param = "";

        if (isVaildParameter(left, stm, cls)) {
            param += params.remove(params.size() - 1) + token.getText();
            if (isVaildParameter(right, stm, cls)) {
                param += params.remove(params.size() - 1);
                params.add(param);
                return true;
            }
        }
        return false;
    }

    /**
     * Helper Method that Check if Expression is in a form that can be refactored
     *
     * Always true
     *
     * @param exp PsiLiteralExpression
     * @param stm PsiDeclarationStatement
     * @param cls PsiClass
     * @return true if the parameter is valid
     */
    private static boolean isVaildParameter(PsiLiteralExpression exp, PsiDeclarationStatement stm, PsiClass cls) {
        params.add(exp.getText());
        return true;
    }
}
