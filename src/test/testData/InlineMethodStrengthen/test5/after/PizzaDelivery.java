package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        int par1 = 32;
        doSomething(par1);
    }

    void doSomething(int int_random)
    {
        return;
    }
}