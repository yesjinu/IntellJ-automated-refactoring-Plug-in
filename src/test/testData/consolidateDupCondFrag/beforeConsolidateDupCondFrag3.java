public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        if (i == 1) {
            sum();
            k = 1;<caret>
            j = 1;
        }
        else if (i == 2) {
            sum();
            k = 1;
            j = 2;
        }
        else {
            sum();
            k = 1;
            j = 1;
        }
    }
}