public class Caller {
    ParamContainer p = new ParamContainer();
    int a = p.getA();
    boolean b = p.getB();
    char c = p.getC();
    int result = TestClass.testMethod(a, c);
}


class TestClass {
    public static int testMethod(int a, char c) {
        if (b) {
            a += 1;
        } else {
            a -= 1;
        }
        return 1;
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