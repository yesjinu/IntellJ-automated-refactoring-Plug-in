public class Caller {
    ParamContainer p = new ParamContainer();
    boolean b = p.getB();
    int result = TestClass.testMethod(p);
}


class TestClass {
    public static int testMethod(ParamContainer p) {
        int a = p.getA();
        char c = p.getC();
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