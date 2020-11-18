package codes.encapsulatefield.test1.input;

public class EF_other {

    public void method1() {
        EF_owner o = new EF_owner();
        System.out.println(o.count);
    }

    public void method2()
    {
        EF_owner o = new EF_owner();
        o.count = 2;
        System.out.println(o.count);
    }
}
