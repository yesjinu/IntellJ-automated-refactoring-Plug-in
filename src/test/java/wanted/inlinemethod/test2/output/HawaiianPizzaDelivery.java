package wanted.inlinemethod.test2.output;

public class HawaiianPizzaDelivery extends PizzaDelivery {
    public HawaiianPizzaDelivery(int numberofLateDeliveries) {
        super (numberofLateDeliveries);
    }

    @Override
    boolean moreThanFiveLateDeliveries() {
        return numberofLateDeliveries > 12;
    }
}
