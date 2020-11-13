package testData.unit.composingmethods.inlinemethod.test2;

public class HawaiianPizzaDelivery extends PizzaDelivery {
    public HawaiianPizzaDelivery(int numberofLateDeliveries) {
        super (numberofLateDeliveries);
    }

    @Override
    boolean moreThanFiveLateDeliveries() {
        return numberofLateDeliveries > 12;
    }
}
