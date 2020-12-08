public class beforeParameterize<caret>WholeObject {
    ParamContainer p = new ParamContainer();
    int a = p.getA();
    boolean b = p.getB();
    char c = p.getC();
    int result = TestClass1.testMethod1(a, b);
    boolean result2 = TestClass2.testMethod2(b, c);
}

class TestClass1 {
    public static int testMethod1(int a, boolean b) {
        if (b) {
            a += 1;
        } else {
            a -= 1;
        }
        return 1;
    }
}

class TestClass2 {
    public static boolean testMethod2(boolean b, char c) {
        return true;
    }
}

class ParamContainer {
    private int a = 1;
    private boolean b = false;
    private char c = 'c';

    public int getA() {return this.a;}
    public boolean getB() {return this.b;}
    public char getC() {return this.c;}
}