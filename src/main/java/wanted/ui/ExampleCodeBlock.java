package wanted.ui;

import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.util.HashMap;
import java.util.Map;

public class ExampleCodeBlock {

    private Map<String, String> before, after;
    public ExampleCodeBlock() {
        before = new HashMap<>();
        after = new HashMap<>();

        init();
    }

    private void init() {
        setBefore_EV();
        setAfter_EV();
    }
    CodeStyleManager
    private void setBefore_EV() {
        String beforeCodeBlock = null;

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock = ;
        after.put("EV", beforeCodeBlock);
    }

    /*
    private void setBefore_EV() {
        String beforeCodeBlock = null;

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock = ;
        after.put("EV", beforeCodeBlock);
    }
    */
}
