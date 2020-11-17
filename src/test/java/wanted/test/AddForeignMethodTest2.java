package wanted.test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;


public class AddForeignMethodTest2 extends BasePlatformTestCase {

    public void testProjectTreeModelRoot() {
        Project project = getProject();
        TreeModel model = ProjectTreeModelFactory.createProjectTreeModel(project);
        assertNotNull(model);
        Object root = model.getRoot();
        assertNotNull(root);
        assertTrue(root instanceof DefaultMutableTreeNode);
        assertEquals(project, ((DefaultMutableTreeNode) root).getUserObject());
    }

}
