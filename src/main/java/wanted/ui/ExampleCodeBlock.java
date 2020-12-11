package wanted.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Class with example codes for RefactorAboutWindow.
 *
 * @author Mintae Kim
 * @see RefactorAboutWindow
 */
public class ExampleCodeBlock {
    private Map<String, String> before, after;

    /* Design Pattern: Singleton */
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
        setBefore_IMS(); setAfter_IMS();
        // TODO: ADD NEW METHOD HERE
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
                "    return extVar1;";

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

    /* IMS: Inline Method Strengthen */
    private void setBefore_IMS() {
        String beforeCodeBlock =
                "class PizzaDelivery {\n" +
                "    final int numberofLateDeliveries;\n" +
                "\n" +
                "    public PizzaDelivery(int numberofLateDeliveries) {\n" +
                "        this.numberofLateDeliveries = numberofLateDeliveries;\n" +
                "    }\n" +
                "\n" +
                "    int getRating() {\n" +
                "        moreThanFiveLateDeliveries(32);\n" +
                "    }\n" +
                "\n" +
                "    void moreThanFiveLateDeli<caret>veries(int int_rand) {\n" +
                "        for (int i = 0; i < 5; i++)\n" +
                "            for (int j = 0; j < 21; j++)\n" +
                "                int p = doSomething(i, int_rand);\n" +
                "    }\n" +
                "\n" +
                "    int doSomething(int int_random1, int_random2)\n" +
                "    {\n" +
                "        return (int_random1 + int_randm2) / 2;\n" +
                "    }\n" +
                "}";

        before.put("IMS", beforeCodeBlock);
    }
    private void setAfter_IMS() {
        String afterCodeBlock =
                "class PizzaDelivery {\n" +
                "    final int numberofLateDeliveries;\n" +
                "\n" +
                "    public PizzaDelivery(int numberofLateDeliveries) {\n" +
                "        this.numberofLateDeliveries = numberofLateDeliveries;\n" +
                "    }\n" +
                "\n" +
                "    int getRating() {\n" +
                "        int par1 = 32;\n" +
                "        for (int inVar1 = 0; inVar1 < 5; inVar1++)\n" +
                "            for (int inVar2 = 0; inVar2 < 21; inVar2++)\n" +
                "                int inVar3 = doSomething(inVar1, par1);\n" +
                "    }\n" +
                "\n" +
                "    int doSomething(int int_random1, int_random2)\n" +
                "    {\n" +
                "        return (int_random1 + int_randm2) / 2;\n" +
                "    }\n" +
                "}";

        after.put("IMS", afterCodeBlock);
    }


    // TODO: COPY AND DO YOUR WORK
    /*
    private void setBefore_EV() {
        String beforeCodeBlock = "" + <- 복붙 후 삭제
                ""; <- 여기다가 코드 복붙

        before.put("EV", beforeCodeBlock);
    }
    private void setAfter_EV() {
        String afterCodeBlock = "" + <- 복붙 후 삭제
                ""; <- 여기다가 코드 복붙

        after.put("EV", afterCodeBlock);
    }
    */
}
