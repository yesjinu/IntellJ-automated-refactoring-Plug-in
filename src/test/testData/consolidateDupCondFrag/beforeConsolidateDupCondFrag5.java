public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        if (i == 1) {
            j = 2;
            sum();
            k = 1;

        }
        else if (i == 2) {
            sum();
            k = 1;
        }
        else {
            sum();
            k = 1;<caret>
        }
    }
}