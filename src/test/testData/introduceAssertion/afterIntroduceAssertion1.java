public class INAdata {
    public void INA(Point p, int num) {
        assert (((num == 0) && (p != null)) || !(num == 0));
        if (num == 0) {
            p.setX(0);
        }
        else {
            num = 0;
        }
    }
}