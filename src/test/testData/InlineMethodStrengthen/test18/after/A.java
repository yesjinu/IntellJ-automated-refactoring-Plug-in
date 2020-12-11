public class A {
    public void func() {
        int for2 = 5;

        doSomethingWhileUseless(for2);
        int par1 = for2;
        while (par1 > 3) {
            par1 -= 2;
        }
    }

    public void doSomethingWhileUseless(int param) {
        while (param > 3) {
            param++;
            param -= 2;
        }
    }

}