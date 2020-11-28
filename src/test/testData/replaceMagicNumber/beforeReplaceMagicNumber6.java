public class basic {
    static final double CONSTANT1 = 3.141592;

    double area(double radius) {
        return radius * radius * CONSTANT1;
    }

    double circumference(double radius) { return 2 * CONSTANT1 * radius;}

    String method1(String input) { return "Good" + input; }

    String method2(String input) { return "Not" + "Go<caret>od" + input; }
}