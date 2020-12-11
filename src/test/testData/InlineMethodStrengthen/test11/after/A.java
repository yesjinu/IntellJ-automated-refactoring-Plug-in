public class A {
    public void func() {
        B b = new B();

        doSomething1(b.bb1);
        int[] par1 = b.bb2;
        par1[0] = 325;
        doSomething3(b.bb3);
        b.doSomethingB(b.bb1);
        b.doSomethingBArray(b.bb2);
        b.doSomethingBClass(b.bb3);
    }

    public void doSomething1(int param) {
        param = 30;
    }

    public void doSomething3(C param) {
        param = new C(4);
    }

    public void noReference(int param) {
        param = 15;
    }
}