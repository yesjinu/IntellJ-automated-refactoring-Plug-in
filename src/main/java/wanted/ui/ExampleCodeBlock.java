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
        setBefore_SEF(); setAfter_SEF();
        setBefore_EF(); setAfter_EF();
        setBefore_RMN(); setAfter_RMN();
        setBefore_RPA(); setAfter_RPA();
        setBefore_PWO(); setAfter_PWO();
        setBefore_CCE(); setAfter_CCE();
        setBefore_CDCF(); setAfter_CDCF();
        setBefore_INA(); setAfter_INA();
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

    /* SEF: Self Encap Field */
    private void setBefore_SEF() {
        String beforeCodeBlock =
                "class basic {\n" +
                        "  private int count;\n" +
                        "  public void method1() { System.out.println(count); }\n" +
                        "  public void method2(int x) { count = x; }\n" +
                        "}\n";

        before.put("SEF", beforeCodeBlock);
    }
    private void setAfter_SEF() {
        String afterCodeBlock =
                "class basic {\n" +
                        "  private int count;\n" +
                        "  public void method1() { System.out.println(getCount()); }\n" +
                        "  public void method2(int x) { setCount(x); }\n" +
                        "\n" +
                        "  /* new methods */\n" +
                        "  protected int getCount() { return count; }\n" +
                        "  protected void setCount(int x) { count = x; }\n" +
                        "}\n";

        after.put("SEF", afterCodeBlock);
    }

    /* EF: Encap Field*/
    private void setBefore_EF() {
        String beforeCodeBlock =
                "class owner {\n" +
                        "  public int count;\n" +
                        "  public owner(){ count = 0; }\n"+
                        "}\n" +
                        "\n" +
                        "class other{\n" +
                        "  public void method1() {\n" +
                        "    owner o = new owner();\n" +
                        "    System.out.println(o.count); \n" +
                        "  }\n" +
                        "  public void method2() {\n" +
                        "    owner o = new owner();\n" +
                        "    o.count = 2; \n" +
                        "    System.out.println(o.count);\n" +
                        "  }\n" +
                        "}\n";

        before.put("EF", beforeCodeBlock);
    }
    private void setAfter_EF() {
        String afterCodeBlock =
                "class owner {\n" +
                        "  private int count;\n" +
                        "  public owner(){ count = 0; }\n"+
                        "  public int getCount() { return count; }\n" +
                        "  public void setCount(int x) { count = x; }\n" +
                        "}\n" +
                        "\n" +
                        "class other{\n" +
                        "  public void method1() {\n" +
                        "    owner o = new owner();\n" +
                        "    System.out.println(o.getCount()); // print 0\n" +
                        "  }\n" +
                        "  public void method2() {\n" +
                        "    owner o = new owner();\n" +
                        "    o.setCount(2)\n" +
                        "    System.out.println(o.getCount()); // print 2\n" +
                        "  }\n" +
                        "}\n";

        after.put("EF", afterCodeBlock);
    }

    /* RMN: Replace Magic Number */
    private void setBefore_RMN() {
        String beforeCodeBlock =
                "double area(double radius) {\n" +
                        "  return radius * radius * 3.141592;\n" +
                        "}\n";

        before.put("RMN", beforeCodeBlock);
    }
    private void setAfter_RMN() {
        String afterCodeBlock =
                "static final double PI_CONSTANT = 3.141592;\n" +
                        "\n" +
                        "double area(double radius) {\n" +
                        "  return radius * radius * PI_CONSTANT;\n" +
                        "}\n";

        after.put("RMN", afterCodeBlock);
    }

    /* RPA: Remove unused Parameter Action */
    private void setBefore_RPA() {
        String beforeCodeBlock =
                "public void addTwo(int a, int b, int c, int d) {\n" +
                        "  return a + b;\n" +
                        "}\n";

        before.put("RPA", beforeCodeBlock);
    }
    private void setAfter_RPA() {
        String afterCodeBlock =
                "public void addTwo(int a, int b) {\n" +
                        "  return a + b;\n" +
                        "}\n";

        after.put("RPA", afterCodeBlock);
    }

    /* PWO: Parameterize Whole Object*/
    private void setBefore_PWO() {
        String beforeCodeBlock =
                "int low = priceList.getLow();\n" +
                "int high = priceList.getHigh();\n" +
                "boolean withinBudget = budget.withinRange(low, high);";

        before.put("PWO", beforeCodeBlock);
    }
    private void setAfter_PWO() {
        String afterCodeBlock =
                "boolean withinBudget = budget.withinRange(priceList)";

        after.put("PWO", afterCodeBlock);
    }

    /* CCE : Consolidate Conditional Expression */
    private void setBefore_CCE() {
        String beforeCodeBlock =
                "if (num == 1) return true;\n" +
                "else if (num == 2) return true;\n" +
                "else return false;";

        before.put("CCE", beforeCodeBlock);
    }
    private void setAfter_CCE() {
        String afterCodeBlock =
                "if ((num == 1) || (num == 2)) return true;\n" +
                "else return false;";

        after.put("CCE", afterCodeBlock);
    }

    /* CDCF : Consolidate Duplicate Conditional Fragments */
    private void setBefore_CDCF() {
        String beforeCodeBlock =
                "if (i ==<caret> 1) {\n" +
                "    j = 1;\n" +
                "    k = 1;\n" +
                "}\n" +
                "else {\n" +
                "    j = 1;\n" +
                "    k = 3;\n" +
                "}";

        before.put("CDCF", beforeCodeBlock);
    }
    private void setAfter_CDCF() {
        String afterCodeBlock =
                "j = 1;\n" +
                "if (i == 1) {\n" +
                "    k = 1;\n" +
                "}\n" +
                "else {\n" +
                "    k = 3;\n" +
                "}";

        after.put("CDCF", afterCodeBlock);
    }

    /* INA : Introduce Assertion */
    private void setBefore_INA() {
        String beforeCodeBlock =
                "if (num == <caret>0) p.setX(0);\n" +
                "else num = 0;";

        before.put("INA", beforeCodeBlock);
    }
    private void setAfter_INA() {
        String afterCodeBlock =
                "assert (!(num == 0) || (p != null));\n" +
                "if (num == <caret>0) p.setX(0);\n" +
                "else num = 0;";

        after.put("INA", afterCodeBlock);
    }

    // TODO: COPY AND DO YOUR WORK
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
