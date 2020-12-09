import java.util.Date;

class Test {
    Date previousEnd = new Date(2020, 11, 1);

    void sendReport() {
        Date nextDay = new Date(previousEnd.getYear() + 2, previousEnd.getMonth() + 3, 1);
    }
}
