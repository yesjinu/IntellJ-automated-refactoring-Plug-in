public class basic {
    public int x = 3.141592;

    double area(double radius) { return radius * radius * 3.141<caret>592; }

    double circumference(double radius) { return 2 * 3.141592 * radius;}
}