public class A {
    public void func() {
        B b = new B();

        doSomething(b.bb1);
        doSomething2(b.bb2);
        doSomething3(b.bb3);
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
    public void doSomething3(C param) {
        param = new C(4);
    }

    public void noRef<caret>erence(int param) {
        param = 15;
    }
}