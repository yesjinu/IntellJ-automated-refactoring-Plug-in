package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        boolean par1 = b;
        return (numberofLateDeliveries > 5 && par1) ? 2 : 1;
    }

}