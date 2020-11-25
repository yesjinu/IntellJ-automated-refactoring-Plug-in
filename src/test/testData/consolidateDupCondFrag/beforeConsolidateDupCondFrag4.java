public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        if (i == 1) {
            sum();
            j = 1;
            for (k = 0; k < 10; k++) {
                sum();
            }
        }
        else if (i == 2) {
            sum();
            j = 1;
            for (k = 0; k < 10; k++) {
                sum();
            }
        }
        else {
            sum();
            j = 2;<caret>
            for (k = 0; k < 10; k++) {
                sum();
            }
        }
    }
}