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
    }

    public void method1()
    {
        System.out.println(outer);
        System.out.println(getD());
    }

    public void method2()
    {
        outer = 0;
        setD(new dummy2());
    }

    protected dummy2 getD() {
        return d;
    }

    protected void setD(dummy2 newValue)
    {
        d = newValue;
    }
}