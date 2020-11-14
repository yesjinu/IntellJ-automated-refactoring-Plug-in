package codes.inlinemethod.test2.input;

public class HawaiianPizzaDelivery extends PizzaDelivery {
    public HawaiianPizzaDelivery(int numberofLateDeliveries) {
        super (numberofLateDeliveries);
    }

    @Override
    boolean moreThanFiveLateDeliveries() {
        return numberofLateDeliveries > 12;
    }
}
