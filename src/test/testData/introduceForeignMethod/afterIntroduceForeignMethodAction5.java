import java.util.Date;

class Test {
    Date previousEnd;

    void sendReport() {
        Date nextDay = nextDay(previousEnd);
    }

    private static Date nextDay(Date arg) {
        return new Date(arg.getYear() + 2, arg.getMonth() + 3, arg.getDate() + 1);
    }
}