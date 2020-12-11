public class Caller {
    ParamContainer p = new ParamContainer();
    int result = TestClass1.testMethod1(p);
    int result2 = TestClass2.testMethod2(p);
}


class TestClass1 {
    public static int testMethod1(ParamContainer p) {
        int a = p.getA();
        boolean b = p.getB();
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
        char c = p.getC();
        short d = p.getD();
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
    private short d = 2;

    public int getA() {return this.a;}
    public boolean getB() {return this.b;}
    public char getC() {return this.c;}
    public short getD() {return this.d;}
}