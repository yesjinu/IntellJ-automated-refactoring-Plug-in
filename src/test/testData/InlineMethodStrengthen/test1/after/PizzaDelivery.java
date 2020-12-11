package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        return numberofLateDeliveries > 5 ? 2 : 1;
    }

}