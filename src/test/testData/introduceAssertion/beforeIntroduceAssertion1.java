public class INAdata {
    public void INA(Point p, int num) {
        if (num == <caret>0) {
            p.setX(0);
        }
        else {
            num = 0;
        }
    }
}