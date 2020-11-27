public class SEF_shadowing {
    private int mem<caret>ber;

    public void method1()
    {
        System.out.println(member);
        double member = 0.0;
        System.out.println(member);
    }

}