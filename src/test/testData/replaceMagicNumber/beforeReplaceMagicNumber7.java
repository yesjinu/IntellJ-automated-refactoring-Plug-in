public class basic {
    static final double CONSTANT1 = 3.141592;

    double area(double radius) {
        return radius * radius * CONSTANT1;
    }

    double circumference(double radius) { return 2 * CONSTANT1 * radius;}

    double sphere_volume(double radius) { return (4/3)*3.14<caret>1592*radius*radius*radius; }

    double sphere_volume(double radius) { return 4*3.141592*radius*radius; }
}