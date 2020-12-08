public class CDCFdata {
    public void CDCF() {
        int i = 1;
        int j, k;
        if (i ==<caret> 1) {
            j = 1;
            k = 1;
        }
        else if (i == 2) {
            j = 1;
            k = 2;
        }
        else {
            j = 1;
            k = 3;
        }
    }
}