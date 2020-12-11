import java.util.Date;

class Te<caret>st {
    int a = 3;
    int b = 10;

    a += 10;
    System.out.println(a);
    Date previousEnd = new Date(2020, 11, 1);

    void sendReport() {
        Date today = new Date(2020, 12, 9);
        Date tomorrow = new Date(2020, 12, 10);
    }
}
