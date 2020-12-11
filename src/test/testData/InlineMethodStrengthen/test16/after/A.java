public class A {
    public void func() {
        int for2 = 32;

        doSomething2(for2);
    }

    public void doSomething2(int param) {
        for (int i = 0; i < 32; i++) {
            int num = 2;
            param = 3;
        }
    }

}