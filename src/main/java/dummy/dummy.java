package dummy;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class dummy extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Messages.showInputDialog("dummy", "dummy", null);
    }
}