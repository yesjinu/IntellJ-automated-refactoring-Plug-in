public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        i<caret>f (i == 1) {
            k = 1;
            sum();
            k = 1;
        }
        else if (i == 2) {
            k = 1;
            sum();
            k = 1;
        }
        else {
            k = 1;
            sum();
            k = 1;
        }
    }
}