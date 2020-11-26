package wanted.ui;

import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import wanted.refactoring.ConsolidateCondExpr;
import wanted.refactoring.ConsolidateDupCondFrag;
import wanted.refactoring.InlineMethodAction;
import wanted.refactoring.SelfEncapField;
import wanted.refactoring.EncapField;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.*;

/**
 * TODO: ProjectTreeModelFactory Explanation
 *
 * @author Mintae Kim
 * @author seungjae yoo
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

            // TODO: ADD
            @Override
            public void visitClass(PsiClass psiClass) {
                super.visitClass(psiClass);


            }

            // TODO: ADD
            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                // SEF
                if(SelfEncapField.refactorValid(project, field)) {
                    addTreeNodes(root, rootRef, "SEF", field);
                } else if(EncapField.refactorValid(project, field)){ // EF
                    addTreeNodes(root, rootRef, "EF", field);
                }
            }

            // TODO: ADD
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                // IM
                if (InlineMethodAction.refactorValid(project, method)) {
                    addTreeNodes(root, rootRef, "IM", method);
                }
            }

            // TODO: ADD
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
            }
        };

        getRootPackages(project).forEach(aPackage -> aPackage.accept(visitor));
        getRootClasses(project).forEach(aClass -> aClass.accept(visitor));
        return new DefaultTreeModel(root);
    }

    /**
     * Method that fetches Refactoring Method name by ID.
     *
     * @param id
     * @return Corresponding Refactoring name (story name)
     */
    private static String getNameByID (String id) {
        switch (id) {
            // Scope: Class
            // TODO: ADD

            // Scope: Field
            case "SEF":
                return new SelfEncapField().storyName();
            case "EF":
                return new EncapField().storyName();

            // Scope: Method
            case "IM":
                return new InlineMethodAction().storyName();
            // TODO: ADD

            // Scope: Statement
            case "CCE":
                return new ConsolidateCondExpr().storyName();

            case "CDCF":
                return new ConsolidateDupCondFrag().storyName();
            // TODO: ADD

            default:
                return null;
        }
    }

    /**
     * Create
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
     * Adding new 'Refactoring Techinque' node to the root.
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
                new DefaultMutableTreeNode (getNameByID (id));
        rootRef.put(id, rootRefNode);
        root.add(rootRefNode);
    }


    /**
     * Returns the root package(s) in the source directory of a project. The default package will not be considered, as
     * it includes all Java classes. Note that classes in the default package (i.e., having no package statement) will
     * be ignored for this assignment. To be completed, this case must be separately handled.
     *
     * @param project a project
     * @return a set of root packages
     */
    private static Set<PsiPackage> getRootPackages(Project project) {
        final Set<PsiPackage> rootPackages = new HashSet<>();
        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitDirectory(PsiDirectory dir) {
                final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(dir);
                if (psiPackage != null && !PackageUtil.isPackageDefault(psiPackage))
                    rootPackages.add(psiPackage);
                else
                    Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootPackages;
    }

    /**
     * Returns set of classes in the default package
     * (which is not considered in {@link ProjectTreeModelFactory#getRootPackages(Project)}
     *
     * @param project A Project
     * @return Set of PsiClass containing default package classes
     */
    private static Set<PsiClass> getRootClasses(Project project) {
        final Set<PsiClass> rootClasses = new HashSet<>();

        PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    rootClasses.addAll(Arrays.asList(((PsiJavaFile) file).getClasses()));
                }
            }
            @Override
            public void visitDirectory(PsiDirectory dir) {
                final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(dir);
                if (psiPackage != null && !PackageUtil.isPackageDefault(psiPackage)) {

                }
                else {
                    Arrays.stream(dir.getSubdirectories()).forEach(sd -> sd.accept(this));
                    Arrays.stream(dir.getFiles()).forEach(f -> f.accept(this));
                }
            }
        };

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        Arrays.stream(rootManager.getContentSourceRoots())
                .map(psiManager::findDirectory)
                .filter(Objects::nonNull)
                .forEach(dir -> dir.accept(visitor));

        return rootClasses;
    }
}

