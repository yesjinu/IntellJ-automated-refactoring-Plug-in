package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        moreThanFiveLateDeliveries(32);
    }

    void moreThanFiveLateDeli<caret>veries(int int_rand) {
        for (int i = 0; i < 5; i++) int p = 4;
    }

    void doSomething(int int_random)
    {
        return;
    }
}