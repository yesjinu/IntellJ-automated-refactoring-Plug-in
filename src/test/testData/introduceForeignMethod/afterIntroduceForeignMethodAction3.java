import java.util.Date;

class Test {
    Date previousEnd;

    void sendReport() {
        Date nextDay = nextDay(previousEnd);
    }

    private static Date nextDay(Date arg) {
        return new Date(arg.getYear(), arg.getMonth(), 1);
    }
}