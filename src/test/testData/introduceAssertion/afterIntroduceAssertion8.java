public class INAdata {
    private int x;
    private int y;

    public void INA(INAdata p, int num) {
        assert (!(num == 0) || (p != null));
        if (num == 0) {
            p.setX(0);
        }
    }

    public void setX(int num) {
        this.x = num;
    }
}