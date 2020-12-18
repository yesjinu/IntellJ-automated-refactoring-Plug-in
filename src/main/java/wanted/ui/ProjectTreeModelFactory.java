package wanted.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import wanted.refactoring.*;
import wanted.utils.FindPsi;
import wanted.utils.TraverseProjectPsi;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.*;

/**
 * Factory Class responsible for creation of Project Structure Tree: Refactoring Techinques
 *
 * @author Mintae Kim
 * @author seungjae yoo
 * @author seha Park
 * @author Jinu Noh
 * @author Chanyoung Kim
 * @author CSED332 2020 TAs
 */
class ProjectTreeModelFactory {

    /**
     * Create a tree model that describes the 'Refactoring' structure of a java project.
     * This method use JavaRecursiveElementVisitor to traverse the whole project with the Java hierarchy
     * from each root package in the source directory to the one tiny single statement,
     * and finds out whether every particular 'Refactoring Techinque' is applicable or not.
     *
     * Instance of {@link DefaultMutableTreeNode} that can have a user object. The user object of root is the project
     * itself, and other nodes have corresponding instances of 'Refactoring Techinque's,
     * which has corresponding 'PsiElement's as a child. (refactoring applicable)
     *
     * @param project a project
     * @return a tree model to describe the structure of project
     */
    public static TreeModel createProjectTreeModel(Project project) {
        // the root node of the tree
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(project);
        final Map<String, DefaultMutableTreeNode> rootRef = new HashMap<>();

        // traverse
        final JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {

            @Override
            public void visitPackage(PsiPackage pack) {
                for (PsiPackage subPack : pack.getSubPackages()) subPack.accept(this);
                for (PsiClass subClass : pack.getClasses()) subClass.accept(this);
            }

            /**
             * function that is executed when visiting a PsiClass node while traversing the tree
             *
             * @param psiClass PsiClass
             */
            @Override
            public void visitClass(PsiClass psiClass) {
                super.visitClass(psiClass);
                // IFM
                if(IntroduceForeignMethodAction.refactorValid(project, psiClass)) {
                    addTreeNodes(root, rootRef, "IFM", psiClass);
                }

                // ILE
                if(IntroduceLocalExtensionAction.refactorValid(project, psiClass)) {
                    addTreeNodes(root, rootRef, "ILE", psiClass);
                }

                // HD
                if(HideDelegateAction.refactorValid(psiClass)) {
                    addTreeNodes(root, rootRef, "HD", psiClass);
                }

                // RMN
                if(psiClass instanceof PsiAnonymousClass || (psiClass.getContainingClass()!=null)){ return; }
                Set<String> literals = new HashSet<>();
                FindPsi.findPsiLiteralExpressions(psiClass).forEach(e -> {
                    if (!literals.contains(e.getText()) && ReplaceMagicNumber.refactorValid(project, e)) { // if new refactorable literal
                        addTreeNodes(root, rootRef, "RMN", e);
                        literals.add(e.getText());
                    }
                });

                // PWO
                if(ParameterizeWholeObjectAction.refactorValid(project, psiClass)) {
                    addTreeNodes(root, rootRef, "PWO", psiClass);
                }

            }

            /**
             * function that is executed when visiting a PsiField node while traversing the tree
             *
             * @param field PsiField
             */
            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                // SEF
                if(SelfEncapField.refactorValid(project, field)) {
                    addTreeNodes(root, rootRef, "SEF", field);
                }
                // EF
                else if(EncapField.refactorValid(project, field)){
                    addTreeNodes(root, rootRef, "EF", field);
                }
            }

            /**
             * function that is executed when visiting a PsiMethod node while traversing the tree
             *
             * @param method PsiMethod
             */
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                // IM
                if (InlineMethod.refactorValid(project, method)) {
                    addTreeNodes(root, rootRef, "IM", method);
                }
                // IMS
                if (InlineMethodStrengthen.refactorValid(project, method)) {
                    addTreeNodes(root, rootRef, "IMS", method);
                }
                // RPA
                if (RemoveUnusedParameterAction.refactorValid(project, method)) {
                    addTreeNodes(root, rootRef, "RPA", method);
                }
            }

            @Override
            public void visitStatement(PsiStatement statement) {

                // EV
                if (ExtractVariable.refactorValid(statement)) {
                    addTreeNodes(root, rootRef, "EV", statement);
                }
                super.visitStatement(statement);
            }

            /**
             * function that is executed when visiting a PsiIfStatement node while traversing the tree
             *
             * @param ifStatement PsiIfStatement
             */
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                super.visitIfStatement(ifStatement);

                // CCE
                if (ConsolidateCondExpr.refactorValid(ifStatement)) {
                    addTreeNodes(root, rootRef, "CCE", ifStatement);
                }

                // CDCF
                PsiElement s = ifStatement;
                while (s.getParent() instanceof PsiIfStatement) s = s.getParent();
                if (ConsolidateDupCondFrag.refactorValid((PsiIfStatement) s)) {
                    boolean adding = true;
                    if (rootRef.get("CDCF") != null) {
                        for (int i = 0; i < rootRef.get("CDCF").getChildCount(); i++) {
                            if (((DefaultMutableTreeNode)rootRef.get("CDCF").getChildAt(i)).getUserObject() == s) {
                                adding = false;
                                break;
                            }
                        }
                    }
                    if (adding) addTreeNodes(root, rootRef, "CDCF", s);
                }

                // INA
                s = ifStatement;
                while (s.getParent() instanceof PsiIfStatement) s = s.getParent();
                if (IntroduceAssertion.refactorValid((PsiIfStatement) s)) {
                    boolean adding = true;
                    if (rootRef.get("INA") != null) {
                        for (int i = 0; i < rootRef.get("INA").getChildCount(); i++) {
                            if (((DefaultMutableTreeNode)rootRef.get("INA").getChildAt(i)).getUserObject() == s) {
                                adding = false;
                                break;
                            }
                        }
                    }
                    if (adding) addTreeNodes(root, rootRef, "INA", s);
                }
            }
        };

        TraverseProjectPsi.getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        TraverseProjectPsi.getRootClasses(project).forEach(aClass -> aClass.accept(visitor));
        return new DefaultTreeModel(root);
    }

    /**
     * Adds new DefaultMutableTreeNode (Category) if missing,
     * and Adds new DefaultMutableTreeNode (PsiElement).
     *
     * @param root Root node of this JTree
     * @param rootRef Map with ID Keys and corresponding 'Refactoring Technique' Nodes
     * @param id Refactoring ID
     * @param psiElement Target PsiElement to add
     */
    private static void addTreeNodes (
            DefaultMutableTreeNode root,
            Map<String, DefaultMutableTreeNode> rootRef,
            String id,
            PsiElement psiElement) {

        DefaultMutableTreeNode rootRefNode = rootRef.get(id);
        if (rootRefNode == null)
            addRefactoringTechniques(root, rootRef, id);

        rootRefNode = rootRef.get(id);

        rootRefNode.add(
                new DefaultMutableTreeNode (psiElement));
    }

    /**
     * Adds new DefaultMutableTreeNode (Category) and connect to the root.
     *
     * @param root Root node of this JTree
     * @param rootRef Map with ID Keys and corresponding 'Refactoring Technique' Nodes
     * @param id Refactoring ID
     */
    private static void addRefactoringTechniques (
            DefaultMutableTreeNode root,
            Map<String, DefaultMutableTreeNode> rootRef,
            String id) {

        DefaultMutableTreeNode rootRefNode =
                new DefaultMutableTreeNode (
                        BaseRefactorManager.getInstance().getRefactorActionByID(id));
        rootRef.put(id, rootRefNode);
        root.add(rootRefNode);
    }
}

