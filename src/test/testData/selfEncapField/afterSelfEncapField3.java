public class SEF_shadowing {
    private int member;

    public void method1()
    {
        System.out.println(getMember());
        double member = 0.0;
        System.out.println(member);
    }

    protected int getMember()
    {
        return member;
    }

    protected void setMember(int newValue)
    {
        member = newValue;
    }
}