public class A {
    public void func() {
        int for2 = 5;

        doSomethingWhileUseless(for2);
        while (for2 > 3) {
            for2 -= 2;
        }
    }

    public void doSomethingWhileUseless(int param) {
        while (param > 3) {
            param++;
            param -= 2;
        }
    }

}