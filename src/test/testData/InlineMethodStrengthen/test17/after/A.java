public class A {
    public void func() {
        int for2 = 5;

        doSomethingWhileUseless(for2);
        doSomethingWhile(for2);
    }

    public void doSomethingWhileUseless(int param) {
        while (param > 3) {
            param++;
            param -= 2;
        }
    }

    public void doSomethingWhile(int param) {
        while (param > 3) {
            param -= 2;
        }
    }
}