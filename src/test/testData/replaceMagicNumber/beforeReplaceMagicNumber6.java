public class basic {
    public void method1(String str1)
    {
        System.out.println("Course: " + "CSED332");
        System.out.println("Course: CSED332");

        if(str1.equals("CSED312"))
        {
            Sys<caret>tem.out.println("correct");
        }
    }

    public void CSED312()
    {
        System.out.println("correct");
    }
}