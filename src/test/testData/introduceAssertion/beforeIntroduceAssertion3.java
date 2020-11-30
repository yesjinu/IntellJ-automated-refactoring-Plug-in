public class INAdata {
    private int x;
    private int y;

    public void INA(INAdata p, INAdata q, INAdata[] r, int num) {
        if (num == 1) num += 1;
        if (num == 0) {
            q.setX(1);
            r[1].se<caret>tX(0);
        }
        else {
            p.setX(0);
        }
        num = 1;
    }

    public void setX(int num) {
        this.x = num;
    }
}