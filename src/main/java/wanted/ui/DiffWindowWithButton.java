/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wanted.ui;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.impl.CacheDiffRequestChainProcessor;
import com.intellij.diff.impl.DiffRequestProcessor;
import com.intellij.diff.impl.DiffWindow;
import com.intellij.diff.impl.DiffWindowBase;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapperBuilder;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wanted.refactoring.BaseRefactorAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * a Code Comparison window that contains a scrollable comparison view for changes.
 *
 * @see com.intellij.diff.impl.DiffWindow
 * @see com.intellij.diff.impl.DiffWindowBase
 * @author Copyright 2000-2015 JetBrains s.r.o.
 * @author Mintae Kim
 * @author seungjae Yoo
 */
public class DiffWindowWithButton extends DiffWindow {

    private DiffRequestProcessor myProcessor;
    private WindowWrapper myWrapper;
    @NotNull private final DiffRequestChain myRequestChain;

    private Map<PsiFile, String> changeMap;
    private BaseRefactorAction refactorAction;

    public DiffWindowWithButton(@Nullable Project project, @NotNull DiffRequestChain requestChain, @NotNull DiffDialogHints hints,
                                Map<PsiFile, String> changeMap, BaseRefactorAction refactorAction) {
        super(project, requestChain, hints);

        this.changeMap = changeMap;
        this.refactorAction = refactorAction;
        myRequestChain = requestChain;
    }

    /**
     * Method that initializes window by:
     *
     * 1. Composing WindowWrapper & RequestProcessor
     * 2. Send to Disposer and wait until turn comes.
     *
     * @see DiffWindowBase#init()
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "203.4203.26")
    protected void init() {
        if (myWrapper != null) return;

        myProcessor = createProcessor();

        String dialogGroupKey = myProcessor.getContextUserData(DiffUserDataKeys.DIALOG_GROUP_KEY);
        if (dialogGroupKey == null) dialogGroupKey = "DiffContextDialog";

        myWrapper = new WindowWrapperBuilder(DiffUtil.getWindowMode(myHints), new DiffPanel(myProcessor.getComponent()))
                .setProject(myProject)
                .setParent(myHints.getParent())
                .setDimensionServiceKey(dialogGroupKey)
                .setPreferredFocusedComponent(() -> myProcessor.getPreferredFocusedComponent())
                .setOnShowCallback(() -> myProcessor.updateRequest())
                .build();

        myWrapper.setImages(DiffUtil.Lazy.DIFF_FRAME_ICONS);

        Disposer.register(myWrapper, myProcessor);

        Consumer<WindowWrapper> wrapperHandler = myHints.getWindowConsumer();
        if (wrapperHandler != null) wrapperHandler.consume(myWrapper);
    }

    /**
     * Method that initializes window, and shows it.
     *
     * @see DiffWindowBase#show()
     */
    @Override
    public void show() {
        init();
        myWrapper.show();
    }

    /**
     * Class that composes GUIs (Panel), and Button Click Listeners.
     * With changeMap, we can revert all changes from previous refactoring.
     *
     * @see com.intellij.diff.impl.DiffWindowBase
     */
    private class DiffPanel extends JPanel {
        DiffPanel(@NotNull JComponent content) {
            super(new BorderLayout(5, 0));

            // Content
            add(content, BorderLayout.CENTER);

            // Button
            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            JPanel buttonPanel_sub = new JPanel(new GridLayout(1, 2));

            JButton applyButton = new JButton("Apply");
            JButton cancelButton = new JButton("Cancel");

            // Apply Listener
            applyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myWrapper.close();
                    PsiFile[] ff = changeMap.keySet().toArray(new PsiFile[changeMap.size()]);
                    for (PsiFile f : ff) {
                        WriteCommandAction.runWriteCommandAction(myProject, ()-> {
                            Document document = PsiDocumentManager.getInstance(myProject).getDocument(f);
                            document.setText(changeMap.get(f));
                        });
                    }
                }
            });

            // Cancel Listener
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myWrapper.close();
                }
            });

            buttonPanel_sub.add(applyButton);
            buttonPanel_sub.add(cancelButton);
            buttonPanel.add(buttonPanel_sub, BorderLayout.EAST);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension windowSize = DiffUtil.getDefaultDiffWindowSize();
            Dimension size = super.getPreferredSize();
            return new Dimension(Math.max(windowSize.width, size.width), Math.max(windowSize.height, size.height));
        }
    }

    /**
     * Method that creates processor.
     * (myProcessor: Originally Overrided through {@link DiffWindow})
     *
     * @return Request Processor
     * @see DiffWindow#createProcessor()
     */
    @Override
    @NotNull
        protected DiffRequestProcessor createProcessor() {
        return new RefactorChainProcessor(myProject, myRequestChain);
    }

    /**
     * Class for a Chain Processor.
     *
     * @see com.intellij.diff.impl.DiffWindow
     */
    private class RefactorChainProcessor extends CacheDiffRequestChainProcessor {
        RefactorChainProcessor(@Nullable Project project, @NotNull DiffRequestChain requestChain) {
            super(project, requestChain);
        }

        @Override
        protected void setWindowTitle(@NotNull String title) {
            myWrapper.setTitle("Refactoring Preview: " + refactorAction.storyName()
                    + " [" + title + "]");
        }

        @Override
        protected void onAfterNavigate() {
            DiffUtil.closeWindow(myWrapper.getWindow(), true, true);
        }
    }
}
