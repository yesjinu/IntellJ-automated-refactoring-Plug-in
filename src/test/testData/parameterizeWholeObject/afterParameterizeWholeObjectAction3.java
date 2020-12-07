public class Caller {
    ParamContainer p = new ParamContainer();
    int a = p.getA();
    int result = TestClass1.testMethod1(a);
    int result2 = TestClass2.testMethod2(p);
}


class TestClass1 {
    public static int testMethod1(int a) {
        if (b) {
            a += 1;
        } else {
            a -= 1;
        }
        return 1;
    }
}

class TestClass2 {
    public static int testMethod2(ParamContainer p) {
        boolean b = p.getB();
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