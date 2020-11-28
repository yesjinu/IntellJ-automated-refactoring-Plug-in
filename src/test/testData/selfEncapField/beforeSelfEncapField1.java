public class SEF_basic {
    private int me<caret>mber;

    public SEF_basic()
    {
        member = 0;
    }

    public void method1()
    {
        System.out.println(member);
    }

    public void method2(int x)
    {
        member = x;
    }
}



