package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        boolean res2 = (numberofLateDeliveries > 5 && true);
        return (numberofLateDeliveries > 5 && b) ? 2 : 1;
    }

}