// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package wanted.ui;

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
    private JPanel diffPanel;

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

        // TODO: Opening

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
