public class INAdata {
    private x;
    private y;

    public void INA(INAdata p, INAdata q, int num) {
        if (num == 1) num += 1;
        if (num == 0) {
            num = 1;
        }
        else {<caret>
            p.setX(0);
            q.setX(1);
        }
        num = 1;
    }

    public void setX(int num) {
        this.x = num;
    }
}