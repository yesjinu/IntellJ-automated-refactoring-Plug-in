package wanted.ui;

import wanted.refactoring.BaseRefactorAction;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class that composes Right Click Popup.
 *
 * @author Mintae Kim
 * @author Ahmed Ashour
 * @author jjinguy
 */
public class RefactorPopUp extends JPopupMenu {
    JMenuItem anItem;

    public RefactorPopUp(Object object) {
        if (object != null) {
            if (object instanceof BaseRefactorAction) {
                anItem = new JMenuItem("About " + ((BaseRefactorAction) object).storyName());
                anItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        new RefactorAboutWindow((BaseRefactorAction) object).show();
                    }
                });
                add(anItem);
            }
        }
        // TBD (Plugin Version)
    }
}
