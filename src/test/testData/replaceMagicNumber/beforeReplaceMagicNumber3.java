public class basic {
    public void method1(String str1)
    {
        System.out.println("Course: " + "CSED332");
        System.out.println("Course: CSED332");

        if(str1.equals("CSED3<caret>32"))
        {
            System.out.println("correct");
        }
    }

    public void CSED332()
    {
        System.out.println("correct");
    }
}