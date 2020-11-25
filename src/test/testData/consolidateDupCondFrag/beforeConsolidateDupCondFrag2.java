public class CDCFdata {
    public void CDCF() {
        int i = 1;
        int j, k;
        if (i == 1) {
            j = 1;
            k = 1;
        }
        else if (i == 2) {
            j = <caret>2;
            k = 1;
        }
        else {
            j = 3;
            k = 1;
        }
    }
}