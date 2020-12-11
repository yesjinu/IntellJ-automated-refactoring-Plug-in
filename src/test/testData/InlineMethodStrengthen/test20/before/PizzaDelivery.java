package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    void getRating() {
        moreThanFiveLateDeliveries(32);
    }

    void moreThanFiveLateDeli<caret>veries(int int_rand) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 21; j++) {
                int p = doSomething(i, int_rand);
            }
    }

    int doSomething(int int_random1, int int_random2)
    {
        return (int_random1 + int_random2) / 2;
    }
}