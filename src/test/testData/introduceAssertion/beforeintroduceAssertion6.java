public class INAdata {
    private x;
    private y;

    public void INA(INAdata <caret>p, int num) {
        if (num == 0) {
            p.setX(0);
        }
        else {
            num = 0;
        }
    }

    public void setX(int num) {
        this.x = num;
    }
}