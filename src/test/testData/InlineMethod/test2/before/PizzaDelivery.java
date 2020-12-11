package InlineMethod;

public class PizzaDelivery {
    public final int numberofLateDeliveries;

    public PizzaDelivery (int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }


    int getRating() {
        return moreThanFiveLateDeliveries() ? 2 : 1;
    }

    boolean moreThanFiveLateDeliveries() {
        return numberofLateDeliveries > 5;
    }
}