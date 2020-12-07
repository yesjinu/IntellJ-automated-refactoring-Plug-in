// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package wanted.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wanted.refactoring.BaseRefactorAction;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

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
        title = new JLabel("Refactoring Technique: " + refactorAction.storyName());
        title.setFont(new Font("Vivaldi", Font.PLAIN, 30));
        dialogPanel.add(title, BorderLayout.NORTH);

        // Center
        examplePanel = new JPanel(new BorderLayout(10, 10));

        EditorFactory ef = EditorFactory.getInstance();

        Document docBefore = ef.createDocument(readFromFile(refactorAction.getBeforeJavaPath()));
        Document docAfter = ef.createDocument(readFromFile(refactorAction.getAfterJavaPath()));

        examplePanel.add(ef.createEditor(docBefore).getComponent(), BorderLayout.WEST);
        // TODO: Insert Image?

        examplePanel.add(ef.createEditor(docAfter).getComponent(), BorderLayout.EAST);

        dialogPanel.add(examplePanel, BorderLayout.CENTER);

        // Bottom: Description
        description = new JLabel(refactorAction.descripton());
        title.setFont(new Font("Jetbrains mono", Font.PLAIN, 15));
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
