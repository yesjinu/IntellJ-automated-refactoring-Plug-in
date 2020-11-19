package codes.encapsulatefield.test1.output;

public class EF_other {

    public void method1() {
        EF_owner o = new EF_owner();
        System.out.println(o.getCount());
    }

    public void method2()
    {
        EF_owner o = new EF_owner();
        o.setCount(2);
        System.out.println(o.getCount());
    }
}
