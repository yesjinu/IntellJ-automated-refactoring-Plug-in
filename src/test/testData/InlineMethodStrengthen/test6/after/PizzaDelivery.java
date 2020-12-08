package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        if (true == true) doSomething(33);
        else doSomething(32);
    }

    void doSomething(int int_random)
    {
        return;
    }
}