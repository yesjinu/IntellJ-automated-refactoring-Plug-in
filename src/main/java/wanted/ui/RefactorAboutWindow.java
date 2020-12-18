// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package wanted.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wanted.refactoring.BaseRefactorAction;

import javax.swing.*;
import java.awt.*;

/**
 * Class that composes Refactor Technique Information Window.
 *
 * @author Mintae Kim
 * @author Copyright 2000-2015 JetBrains s.r.o.
 */
public class RefactorAboutWindow extends DialogWrapper {
    private JPanel dialogPanel;
    private JPanel examplePanel;
    private JPanel paragraphPanel;
    private JPanel descriptionPanel;
    private JPanel preconditionPanel;

    private JLabel title;
    private JLabel description_title;
    private JLabel description;
    private JLabel precondition_title;
    private JLabel precondition;

    private BaseRefactorAction refactorAction;

    public RefactorAboutWindow(BaseRefactorAction refactorAction) {
        super(true);

        this.refactorAction = refactorAction;

        init();
        setTitle("About Refactoring Technique: " + refactorAction.storyName());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        dialogPanel = new JPanel(new BorderLayout(10, 10));

        title = new JLabel("Refactoring Technique: " + refactorAction.storyName());
        title.setFont(new Font("Roboto Thin", Font.PLAIN, 27));
        dialogPanel.add(title, BorderLayout.NORTH);

        // Center: Code
        examplePanel = new JPanel(new BorderLayout(10, 10));

        EditorFactory ef = EditorFactory.getInstance();
        ExampleCodeBlock cb = ExampleCodeBlock.getInstance();

        Document docBefore = ef.createDocument(cb.getBeforeCode(refactorAction.storyID()));
        Document docAfter = ef.createDocument(cb.getAfterCode(refactorAction.storyID()));
        JComponent editBefore = ef.createViewer(docBefore).getComponent();
        JComponent editAfter = ef.createViewer(docAfter).getComponent();

        // editBefore.setPreferredSize(new Dimension(500, 300));
        // editAfter.setPreferredSize(new Dimension(500, 300));
        examplePanel.add(editBefore, BorderLayout.WEST);
        examplePanel.add(editAfter, BorderLayout.EAST);

        // Center: Image
        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Roboto Thin", Font.PLAIN, 100));
        arrow.setPreferredSize(new Dimension(80, 100));
        examplePanel.add(arrow, BorderLayout.CENTER);

        dialogPanel.add(examplePanel, BorderLayout.CENTER);


        // Bottom
        paragraphPanel = new JPanel(new GridLayout(1, 2));
        descriptionPanel = new JPanel(new BorderLayout(10, 10));
        preconditionPanel = new JPanel(new BorderLayout(10, 10));

        preconditionPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        // Bottom: Description
        description_title = new JLabel("@ensures");
        description_title.setFont(new Font("Felix Titling", Font.PLAIN, 18));
        description_title.setForeground(JBColor.BLUE);
        descriptionPanel.add(description_title, BorderLayout.NORTH);

        description = new JLabel(refactorAction.description());
        description.setFont(new Font("Calibri", Font.PLAIN, 16));
        descriptionPanel.add(description, BorderLayout.WEST);

        // Bottom: Precondition
        precondition_title = new JLabel("@precondition");
        precondition_title.setFont(new Font("Felix Titling", Font.PLAIN, 18));
        precondition_title.setForeground(JBColor.BLUE);
        preconditionPanel.add(precondition_title, BorderLayout.NORTH);

        precondition = new JLabel(refactorAction.precondition());
        precondition.setFont(new Font("Calibri", Font.PLAIN, 16));
        preconditionPanel.add(precondition, BorderLayout.WEST);

        paragraphPanel.add(descriptionPanel);
        paragraphPanel.add(preconditionPanel);
        dialogPanel.add(paragraphPanel, BorderLayout.SOUTH);

        return dialogPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = getHelpAction();
        return helpAction == myHelpAction && getHelpId() == null ?
                new Action[]{getOKAction()} :
                new Action[]{getOKAction(), helpAction};
    }
}
