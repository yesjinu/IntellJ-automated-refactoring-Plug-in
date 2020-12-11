public class B {
    public int bb1;
    public int[] bb2;
    public C bb3;

    public B() {
        bb1 = 3;
        bb2 = new int[4]{1, 2, 3, 4};
        bb3 = new C(5);
    }

    public void doSomet<caret>hingBClass(C param) {
        param.cc1 = 32;
    }
}