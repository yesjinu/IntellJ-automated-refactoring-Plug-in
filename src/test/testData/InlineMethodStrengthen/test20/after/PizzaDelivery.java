package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    void getRating() {
        int par1 = 32;
        for (int inVar1 = 0; inVar1 < 5; inVar1++)
            for (int inVar2 = 0; inVar2 < 21; inVar2++) {
                int inVar3 = doSomething(inVar1, par1);
            }
    }

    int doSomething(int int_random1, int int_random2)
    {
        return (int_random1 + int_random2) / 2;
    }
}