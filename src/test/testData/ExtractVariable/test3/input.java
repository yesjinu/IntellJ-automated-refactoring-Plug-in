public class Test {
    void renderBanner() {
        int [][] array = new int[50][40];
        for (int i = 0; i < 50; i++){
            if (array[(40 + 50 + 70) % 3 + i - 32][0] > 32 &&
                array[(40 + 50 + 70) % 3 + i - 32][1] > 29)
                continue;

            array[(1<caret> + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10 + 11 + 12 + 13 +
                    14 + 15 + 16 + i - 1) % 50][2] = 1;
        }
    }
}