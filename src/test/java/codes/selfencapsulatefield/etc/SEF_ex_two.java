package codes.selfencapsulatefield.etc;

public class SEF_ex_two {
    private int member;
    private int member2;

    public SEF_ex_two()
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

// only refactor first member