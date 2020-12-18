import java.util.Date;

class Test {
    ModifiedDate previousEnd = new ModifiedDate(2020, 11, 1);

    void sendReport() {
        ModifiedDate nextDay = previousEnd.nextDay();
    }
}

class ModifiedDate extends Date {
    public ModifiedDate(int arg1, int arg2, int arg3) {
        super(arg1, arg2, arg3);
    }

    ModifiedDate nextDay() {
        return new ModifiedDate(getYear(), getMonth(), getDate() + 1);
    }
}
