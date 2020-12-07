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
        // TODO: TBA
    }

    /* EV: Extract Variable */
    private void setBefore_EV() {
        String beforeCodeBlock =
                "    return order.quantity * order.itemPrice -\n" +
                "      Math.max(0, order.quantity - 500) * order.itemPrice * 0.05 +\n" +
                "      Math.min(order.quantity * order.itemPrice * 0.1, 100);";

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock =
                "    final double extVar1 = order.quantity * order.itemPrice -\n" +
                "      Math.max(0, order.quantity - 500) * order.itemPrice * 0.05 +\n" +
                "      Math.min(order.quantity * order.itemPrice * 0.1, 100);\n" +
                "    return extVar1";

        after.put("EV", afterCodeBlock);
    }

    /* IM: Inline Method */
    private void setBefore_IM() {
        String beforeCodeBlock =
                "class PizzaDelivery {\n" +
                "  // ...\n" +
                "  int getRating() {\n" +
                "    return moreThanFiveLateDeliveries() ? 2 : 1;\n" +
                "  }\n" +
                "  boolean moreThanFiveLateDeliveries() {\n" +
                "    return numberOfLateDeliveries > 5;\n" +
                "  }\n" +
                "}";

        before.put("IM", beforeCodeBlock);
    }
    private void setAfter_IM() {
        String afterCodeBlock =
                "class PizzaDelivery {\n" +
                "  // ...\n" +
                "  int getRating() {\n" +
                "    return numberOfLateDeliveries > 5 ? 2 : 1;\n" +
                "  }\n" +
                "}";

        after.put("IM", afterCodeBlock);
    }

    /*
    private void setBefore_EV() {
        String beforeCodeBlock =
                ""; <- 여기다가 코드 복붙

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock =
                ""; <- 여기다가 코드 복붙

        after.put("EV", afterCodeBlock);
    }
    */
}
