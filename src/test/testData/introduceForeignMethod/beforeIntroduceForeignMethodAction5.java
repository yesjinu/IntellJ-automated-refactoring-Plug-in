import java.util.Date;

class Test {
    Date previousEnd;

    void sendReport() {
        Date nextDay = new Date(previousEnd.getYear() + 2, previousEnd.getMonth() + 3, previousEnd.getDate() + 1);
    }
}