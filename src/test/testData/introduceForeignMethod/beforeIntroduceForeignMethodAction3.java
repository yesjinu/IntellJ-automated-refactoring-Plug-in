import java.util.Date;

class Test {
    Date previousEnd;

    void sendReport() {
        Date nextDay = new Date(previousEnd.getYear(), previousEnd.getMonth(), 1);
    }
}