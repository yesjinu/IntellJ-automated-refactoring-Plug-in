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
    cc = d.getC();

    public void testMethod(DummyContainer d) {
        a_param = d.getA();
        b_param = d.getB();
        if (b_param) {
            return a_param += 1;
        } else {
            return a_param -= 1;
        }
    }
}