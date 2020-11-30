public class INAdata {
    private x;
    private y;

    public void INA(INAdata p, int num) {
        i<caret>f (num == 0) {
            p.setX(0);
        }
    }

    public void setX(int num) {
        this.x = num;
    }
}