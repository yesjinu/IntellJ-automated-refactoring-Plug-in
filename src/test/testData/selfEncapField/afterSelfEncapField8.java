public class dummy {
    private int outer;
    private dummy2 d;

    public class dummy2{
        private String inner;

        public void method1()
        {
            System.out.println(getInner());
        }

        public void method2()
        {
            System.out.println(outer);
        }

        protected String getInner()
        {
            return inner;
        }

        protected void setInner(String newValue)
        {
            inner = newValue;
        }
    }

    public void method1()
    {
        System.out.println(outer);
        System.out.println(d);
    }

    public void method2()
    {
        outer = 0;
        d = new dummy2();
    }
}