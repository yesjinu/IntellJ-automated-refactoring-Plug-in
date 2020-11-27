public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        sum();
        if (i == 1) {
            j = 1;
        }
        else if (i == 2) {
            j = 1;
        }
        else {
            j = 2;
        }
        for (k = 0; k < 10; k++) {
            sum();
        }
    }
}