package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        for (int i = 0; i < 5; i++) doSomething(i);
    }

    void doSomething(int int_random)
    {
        return;
    }
}