public class Caller {
    ParamContainer p = new ParamContainer();
    char cc = p.getC();
    int result = TestClass.testMethod(p);
}


class TestClass {
    public static void testMethod(paramContainer p) {
        int a_param = p.getA();
        boolean b_param = p.getB();
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