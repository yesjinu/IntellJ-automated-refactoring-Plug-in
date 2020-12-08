public class A {
    public void func() {
        int for2 = 5;

        doSomethingWhileUseless(for2);
        int tempVar1 = for2;
        while (tempVar1 > 3) {
            tempVar1 -= 2;
        }
    }

    public void doSomethingWhileUseless(int param) {
        while (param > 3) {
            param++;
            param -= 2;
        }
    }

}