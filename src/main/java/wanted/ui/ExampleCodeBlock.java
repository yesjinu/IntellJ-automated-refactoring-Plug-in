package wanted.ui;

import com.intellij.psi.codeStyle.CodeStyleManager;

import java.util.HashMap;
import java.util.Map;

public class ExampleCodeBlock {
    private Map<String, String> before, after;

    // Design Pattern: Singleton
    private static ExampleCodeBlock codeBlock = null;

    protected ExampleCodeBlock() {
        before = new HashMap<>();
        after = new HashMap<>();

        init();
    }
    public static ExampleCodeBlock getInstance() {
        if (codeBlock == null)
            codeBlock = new ExampleCodeBlock();

        return codeBlock;
    }

    public char[] getBeforeCode(String id) {
        return before.getOrDefault(id, "Wrong ID or No Example Code.").toCharArray();
    }
    public char[] getAfterCode(String id) {
        return after.getOrDefault(id, "Wrong ID or No Example Code.").toCharArray();
    }

    // Init Method
    private void init() {
        setBefore_EV(); setAfter_EV();
        setBefore_IM(); setAfter_IM();
        // TBA
    }

    /* EV: Extract Variable */
    private void setBefore_EV() {
        String beforeCodeBlock = "Code";

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock = "Code";
        after.put("EV", afterCodeBlock);
    }

    /* IM: Inline Method */
    private void setBefore_IM() {
        String beforeCodeBlock = "Code";

        before.put("IM", beforeCodeBlock);
    }
    private void setAfter_IM() {
        String afterCodeBlock = "Code";
        after.put("IM", afterCodeBlock);
    }

    /*
    private void setBefore_EV() {
        String beforeCodeBlock = "Code";

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock = "Code";
        after.put("EV", afterCodeBlock);
    }
    */
}
