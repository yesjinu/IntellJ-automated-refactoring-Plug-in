public class SEF_alreadyDone {
    private int member;

    public SEF_ex_alreadyDone()
    {
        member=0;
    }

    public void method1()
    {
        System.out.println(getMember());
    }

    public void method2(int x)
    {
        member=x;
    }

    public int getMember()
    {
        return member;
    }

}