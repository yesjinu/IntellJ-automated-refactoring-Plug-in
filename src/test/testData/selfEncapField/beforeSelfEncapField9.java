public class dummy {
    private int out<caret>er;
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
        System.out.println(outer);
        System.out.println(d);
    }

    public void method2()
    {
        outer = 0;
        d = new dummy2();
    }
}