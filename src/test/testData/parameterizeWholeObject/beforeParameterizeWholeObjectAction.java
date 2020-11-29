public class Caller {
    ParamContainer p = new ParamContainer();
    ParamContainer q = new ParamContainer();
    int c = 9;
    int aa = p.getA();
    boolean bb = p.getB();
    char cc = q.getC();
    int result1 = TestClass.testMethod(c, aa, bb);
    int result1 = TestClass.testMethod(aa, bb, cc);
}


class TestClass {
    public static int testMethod(int a_param, boolean b_param) {
        if (b_param) {
            a_param += 1;
        } else {
            a_param -= 1;
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