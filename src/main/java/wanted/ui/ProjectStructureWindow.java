package wanted.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * a Project Structure tool window that contains a scrollable tree view for a java project
 *
 * @author CSED332 2020 TAs
 */
class ProjectStructureWindow {

    private final JScrollPane topContainer;
    private final JTree tree;

    /**
     * Creates a plugin component
     */
    public ProjectStructureWindow() {
        Project project = getActiveProject();
        tree = new ProjectStructureTree(project);
        topContainer = new JBScrollPane(tree);
    }

    /**
     * Returns the top-level container of this plugin
     *
     * @return the top-level component
     */
    @NotNull
    public JComponent getContent() {
        return topContainer;
    }


    /**
     * Returns the open project of the current IntelliJ IDEA window
     *
     * @return the project
     */
    @NotNull
    private Project getActiveProject() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive())
                return project;
        }
        // if there is no active project, return an arbitrary project (the first)
        return ProjectManager.getInstance().getOpenProjects()[0];
    }
}
