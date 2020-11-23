public class DummyContainer {
    private int a = 1;
    private boolean b = false;
    private char c = 'c';

    public int getA() {return this.a;}
    public boolean getB() {return this.b;}
    public char getC() {return this.c;}

}

public class TestClass {
    DummyContainer d = new DummyContainer();
    aa = d.getA();
    bb = d.getB();
    cc = d.getC();

    public void testMethod(int a_param, boolean b_param) {
        if (b_param) {
            return a_param += 1;
        } else {
            return a_param -= 1;
        }
    }
}