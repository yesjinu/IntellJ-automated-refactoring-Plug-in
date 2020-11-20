package wanted.selfencapsulatefield.test1.output;

public class SEF_basic {
    private int member;

    public SEF_basic()
    {
        setMember(0);
    }

    public void method1()
    {
        System.out.println(getMember());
    }

    public void method2(int x)
    {
        setMember(x);
    }

    public int getMember()
    {
        return member;
    }

    public void setMember(int newValue)
    {
        member = newValue;
    }
}