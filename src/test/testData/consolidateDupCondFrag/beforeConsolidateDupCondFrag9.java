public class CDCFdata {

    public void sum() {
        a = 1;
    }

    public void CDCF() {
        int i = 1;
        int j, k;
        if (i == 1<caret>) {
            for (j = 0; j < 10; j++) {
                sum();
            }
        }
        else if (i == 2) {
            for (j = 0; j < 10; j++) {
                k = 1;
            }
        }
        else {
            for (j = 0; j < 10; j++) {
                k = 2;
            }
        }
    }
}