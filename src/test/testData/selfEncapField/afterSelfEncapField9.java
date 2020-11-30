public class dummy {
    private int outer;
    private dummy2 d;

    public class dummy2{
        private String inner;

        public void method1()
        {
            System.out.println(inner);
        }

        public void method2()
        {
            System.out.println(outer);
        }

        public void getOuter()
        {

        }
    }

    public void method1()
    {
        System.out.println(getOuter());
        System.out.println(d);
    }

    public void method2()
    {
        setOuter(0);
        d = new dummy2();
    }

    protected int getOuter()
    {
        return outer;
    }

    protected void setOuter(int newValue)
    {
        outer = newValue;
    }
}