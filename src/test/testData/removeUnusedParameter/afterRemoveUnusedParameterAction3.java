class dummy {
    int a = 0;
    int b = 0;
    public int add<caret>two() {
        a += 1;
        b += 1;
        return a + b;
    }
}