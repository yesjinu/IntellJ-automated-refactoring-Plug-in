public class A {
    public void func() {
        B b = new B();

        doSomething1(b.bb1);
        doSomething2(b.bb2);
        C par1 = b.bb3;
        par1 = new C(4);
        b.doSomethingB(b.bb1);
        b.doSomethingBArray(b.bb2);
        b.doSomethingBClass(b.bb3);
    }

    public void doSomething1(int param) {
        param = 30;
    }
    public void doSomething2(int[] param) {
        param[0] = 325;
    }

    public void noReference(int param) {
        param = 15;
    }
}