package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        int par1 = 32;
        if (true == true) doSomething(33);
        else doSomething(par1);
    }

    void doSomething(int int_random)
    {
        return;
    }
}