public class EF_other {

    public void method1() {
        EF_owner o1 = new EF_owner();
        System.out.println(o1.count);
    }

    public void method2()
    {
        EF_owner o2 = new EF_owner();
        o2.count = 2;
        System.out.println(o2.count);
    }
}
