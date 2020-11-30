public class INAdata {
    private int x;
    private int y;

    public void INA(INAdata p, int num) {
        if (num == <caret>0) {
            num = 1;
        }
        else {
            num = 0;
        }
    }

    public void setX(int num) {
        this.x = num;
    }
}