package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        boolean par1 = true;
        boolean res2 = (numberofLateDeliveries > 5 && par1);
        boolean par2 = b;
        return (numberofLateDeliveries > 5 && par2) ? 2 : 1;
    }

}