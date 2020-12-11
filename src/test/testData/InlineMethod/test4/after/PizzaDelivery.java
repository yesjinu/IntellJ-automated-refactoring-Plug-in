package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        int c = 124;
        String temp = null;
        return (numberofLateDeliveries > 5 && b && c != 124 && temp != null) ? 2 : 1;
    }

}