package wanted.ui;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.impl.CacheDiffRequestChainProcessor;
import com.intellij.diff.impl.DiffRequestProcessor;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapperBuilder;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiffWindowWithButton {
    @NotNull private final DiffRequestChain myRequestChain;

    @Nullable protected final Project myProject;
    @NotNull protected final DiffDialogHints myHints;

    private DiffRequestProcessor myProcessor;
    private WindowWrapper myWrapper;

    public DiffWindowWithButton(@Nullable Project project, @NotNull DiffRequestChain requestChain, @NotNull DiffDialogHints hints) {
        myProject = project;
        myHints = hints;
        myRequestChain = requestChain;
    }

    protected void init() {
        if (myWrapper != null) return;

        myProcessor = createProcessor();

        String dialogGroupKey = myProcessor.getContextUserData(DiffUserDataKeys.DIALOG_GROUP_KEY);
        if (dialogGroupKey == null) dialogGroupKey = "DiffContextDialog";

        myWrapper = new WindowWrapperBuilder(DiffUtil.getWindowMode(myHints), new MyPanel(myProcessor.getComponent()))
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

    public void show() {
        init();
        myWrapper.show();
    }

    protected WindowWrapper getWrapper() {
        return myWrapper;
    }

    protected DiffRequestProcessor getProcessor() {
        return myProcessor;
    }

    private class MyPanel extends JPanel {
        MyPanel(@NotNull JComponent content) {
            super(new BorderLayout());
            add(content, BorderLayout.CENTER);


            JPanel buttonPanel = new JPanel();

            JButton applyButton = new JButton("Apply");
            JButton cancelButton = new JButton("Cancel");

            applyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myWrapper.close();
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myWrapper.close();
                }
            });

            buttonPanel.add(applyButton,0);
            buttonPanel.add(cancelButton, 1);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension windowSize = DiffUtil.getDefaultDiffWindowSize();
            Dimension size = super.getPreferredSize();
            return new Dimension(Math.max(windowSize.width, size.width), Math.max(windowSize.height, size.height));
        }
    }

    @NotNull
    protected DiffRequestProcessor createProcessor() {
        return new DiffWindowWithButton.MyCacheDiffRequestChainProcessor(myProject, myRequestChain);
    }

    private class MyCacheDiffRequestChainProcessor extends CacheDiffRequestChainProcessor {
        MyCacheDiffRequestChainProcessor(@Nullable Project project, @NotNull DiffRequestChain requestChain) {
            super(project, requestChain);
        }

        @Override
        protected void setWindowTitle(@NotNull String title) {
            getWrapper().setTitle(title);
        }

        @Override
        protected void onAfterNavigate() {
            DiffUtil.closeWindow(getWrapper().getWindow(), true, true);
        }
    }
}
