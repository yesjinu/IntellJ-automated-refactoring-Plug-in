package InlineMethod;

public class PizzaDelivery {
    public final int numberofLateDeliveries;

    public PizzaDelivery (int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }


    int getRating() {
        return moreThanFiveLateDeliveries() ? 2 : 1;
    }

    boolean moreThanFiveL<caret>ateDeliveries() {
        return numberofLateDeliveries > 5;
    }
}

public class HawaiianPizzaDelivery extends PizzaDelivery {
    public HawaiianPizzaDelivery(int numberofLateDeliveries) {
        super (numberofLateDeliveries);
    }

    @Override
    boolean moreThanFiveLateDeliveries() {
        return numberofLateDeliveries > 12;
    }
}