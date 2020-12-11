public class A {
    public void func() {
        B b = new B();

        int par1 = b.bb1;
        par1 = 30;
        doSomething2(b.bb2);
        doSomething3(b.bb3);
        b.doSomethingB(b.bb1);
        b.doSomethingBArray(b.bb2);
        b.doSomethingBClass(b.bb3);
    }

    public void doSomething2(int[] param) {
        param[0] = 325;
    }
    public void doSomething3(C param) {
        param = new C(4);
    }

    public void noReference(int param) {
        param = 15;
    }
}