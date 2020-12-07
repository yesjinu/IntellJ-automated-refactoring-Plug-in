// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package wanted.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
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

    private JLabel title;
    private JLabel description;

    private JComponent beforeEditor, afterEditor;

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

        // Title
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for ( int i = 0; i < fonts.length; i++ )
        {
            System.out.println(fonts[i]);
        }

        title = new JLabel("Refactoring Technique: " + refactorAction.storyName());
        title.setFont(new Font("Roboto Thin", Font.PLAIN, 18));
        dialogPanel.add(title, BorderLayout.NORTH);

        // Center: Code
        examplePanel = new JPanel(new BorderLayout(10, 10));

        EditorFactory ef = EditorFactory.getInstance();
        ExampleCodeBlock cb = ExampleCodeBlock.getInstance();

        Document docBefore = ef.createDocument(cb.getBeforeCode(refactorAction.storyID()));
        Document docAfter = ef.createDocument(cb.getAfterCode(refactorAction.storyID()));
        JComponent editBefore = ef.createEditor(docBefore).getComponent();
        JComponent editAfter = ef.createEditor(docAfter).getComponent();

        editBefore.setPreferredSize(new Dimension(500, 300));
        editAfter.setPreferredSize(new Dimension(500, 300));
        examplePanel.add(editBefore, BorderLayout.WEST);
        examplePanel.add(editAfter, BorderLayout.EAST);

        // Center: Image
        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Roboto Thin", Font.PLAIN, 100));
        examplePanel.add(arrow, BorderLayout.CENTER);

        dialogPanel.add(examplePanel, BorderLayout.CENTER);

        // Bottom: Description
        description = new JLabel(refactorAction.descripton());
        description.setFont(new Font("Jetbrains mono", Font.PLAIN, 15));
        dialogPanel.add(description, BorderLayout.SOUTH);

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
