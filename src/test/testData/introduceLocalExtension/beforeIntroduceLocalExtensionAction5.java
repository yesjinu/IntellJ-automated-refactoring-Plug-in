import java.util.Date;

class Test {
    Date previousEnd = new Date(2020, 11, 1);

    void sendReport() {
        Date nextDay = new Date(2, previousEnd.getMonth(), 1);
    }
}
