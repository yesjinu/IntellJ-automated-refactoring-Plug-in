public class INAdata {
    private int x;
    private int y;

    public void INA(INAdata p, INAdata q, int num) {
        if (num == 1) num += 1;
        assert ((num == 0) || ((p != null) && (q != null)));
        if (num == 0) {
            num = 1;
        }
        else {
            p.setX(0);
            q.setX(1);
        }
        num = 1;
    }

    public void setX(int num) {
        this.x = num;
    }
}