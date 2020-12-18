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

    /* Init Method */
    private void init() {
        setBefore_EV(); setAfter_EV();
        setBefore_IM(); setAfter_IM();
        setBefore_IMS(); setAfter_IMS();
        setBefore_SEF(); setAfter_SEF();
        setBefore_EF(); setAfter_EF();
        setBefore_RMN(); setAfter_RMN();
        setBefore_RPA(); setAfter_RPA();
        setBefore_PWO(); setAfter_PWO();
        setBefore_CCE(); setAfter_CCE();
        setBefore_CDCF(); setAfter_CDCF();
        setBefore_INA(); setAfter_INA();
        setBefore_IFM(); setAfter_IFM();
        setBefore_ILE(); setAfter_ILE();
        setBefore_HD(); setAfter_HD();
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

    /* IFM : Introduce Foreign Method */
    private void setBefore_IFM() {
        String beforeCodeBlock =
                "import java.util.Date;\n" +
                        "\n" +
                        "class Test {\n" +
                        "    Date previousEnd;\n" +
                        "\n" +
                        "    void sendReport() {\n" +
                        "        Date nextDay = new Date(previousEnd.getYear(), previousEnd.getMonth(), previousEnd.getDate() + 1);\n" +
                        "    }\n" +
                        "}";

        before.put("IFM", beforeCodeBlock);
    }
    private void setAfter_IFM() {
        String afterCodeBlock =
                "import java.util.Date;\n" +
                        "\n" +
                        "class Test {\n" +
                        "    Date previousEnd;\n" +
                        "\n" +
                        "    void sendReport() {\n" +
                        "        Date nextDay = nextDay(previousEnd);\n" +
                        "    }\n" +
                        "\n" +
                        "    private static Date nextDay(Date arg) {\n" +
                        "        return new Date(arg.getYear(), arg.getMonth(), arg.getDate() + 1);\n" +
                        "    }\n" +
                        "}";

        after.put("IFM", afterCodeBlock);
    }

    /* IFM : Introduce Local Extension */
    private void setBefore_ILE() {
        String beforeCodeBlock =
                "import java.util.Date;\n" +
                        "\n" +
                        "class Test {\n" +
                        "    Date previousEnd = new Date(2020, 11, 1);\n" +
                        "\n" +
                        "    void sendReport() {\n" +
                        "        Date nextDay = new Date(previousEnd.getYear(), previousEnd.getMonth(), previousEnd.getDate() + 1);\n" +
                        "    }\n" +
                        "}";

        before.put("ILE", beforeCodeBlock);
    }
    private void setAfter_ILE() {
        String afterCodeBlock =
                "import java.util.Date;\n" +
                        "\n" +
                        "class ModifiedDate extends Date {\n" +
                        "    public ModifiedDate(int arg1, int arg2, int arg3) {\n" +
                        "        super(arg1, arg2, arg3);\n" +
                        "    }\n" +
                        "\n" +
                        "    ModifiedDate nextDay() {\n" +
                        "        return new ModifiedDate(getYear(), getMonth(), getDate() + 1);\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "class Test {\n" +
                        "    ModifiedDate previousEnd = new ModifiedDate(2020, 11, 1);\n" +
                        "\n" +
                        "    void sendReport() {\n" +
                        "        ModifiedDate nextDay = previousEnd.nextDay();\n" +
                        "    }\n" +
                        "}";

        after.put("ILE", afterCodeBlock);
    }

    private void setBefore_HD() {
        String beforeCodeBlock =
                "class Department {\n" +
                        "    private Person _manager;\n" +
                        "\n" +
                        "    public Department(Person manager) {\n" +
                        "        _manager = manager;\n" +
                        "    }\n" +
                        "\n" +
                        "    public Person getManager() {\n" +
                        "        return _manager;\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "class Person {\n" +
                        "    Department _department;\n" +
                        "\n" +
                        "    public Department getDepartment() {\n" +
                        "        return _department;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setDepartment(Department arg) {\n" +
                        "        _department = arg;\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "class Test {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Person john = new Person();\n" +
                        "        Person manager;\n" +
                        "\n" +
                        "        manager = john.getDepartment().getManager();\n" +
                        "    }\n" +
                        "}";

        before.put("HD", beforeCodeBlock);
    }
    private void setAfter_HD() {
        String afterCodeBlock =
                "class Department {\n" +
                        "    private Person _manager;\n" +
                        "\n" +
                        "    public Department(Person manager) {\n" +
                        "        _manager = manager;\n" +
                        "    }\n" +
                        "\n" +
                        "    public Person getManager() {\n" +
                        "        return _manager;\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "class Person {\n" +
                        "    Department _department;\n" +
                        "\n" +
                        "    public Department getDepartment() {\n" +
                        "        return _department;\n" +
                        "    }\n" +
                        "    \n" +
                        "    public void setDepartment(Department arg) {\n" +
                        "        _department = arg;\n" +
                        "    }\n" +
                        "\n" +
                        "    public Person getDepartment() {\n" +
                        "        return _department.getManager();\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "class Test {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Person john = new Person();\n" +
                        "        Person manager;\n" +
                        "\n" +
                        "        manager = john.getManager();\n" +
                        "    }\n" +
                        "}";

        after.put("HD", afterCodeBlock);
    }
}
