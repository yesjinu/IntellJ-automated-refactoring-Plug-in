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
        doSomething(int_rand);
    }

    void doSomething(int int_random)
    {
        return;
    }
}