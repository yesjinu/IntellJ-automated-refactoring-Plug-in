package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        boolean par2 = true;
        boolean res2 = (numberofLateDeliveries > 5 && par2);
        boolean par1 = b;
        return (numberofLateDeliveries > 5 && par1) ? 2 : 1;
    }

}