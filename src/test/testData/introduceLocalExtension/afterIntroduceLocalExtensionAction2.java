import java.util.Date;

class ModifiedDate extends Date {
    public ModifiedDate (int year, int month, int date) {
        super(year, month, date);
    }

    Date nextDay() {
        return new Date(getYear(), getMonth(), getDate() + 1);
    }
}

class Test {
    ModifiedDate previousEnd = new ModifiedDate(2020, 11, 1);

    void sendReport() {
        Date nextDay = previousEnd.nextDay();
    }
    PsiLiteralExpression
}
