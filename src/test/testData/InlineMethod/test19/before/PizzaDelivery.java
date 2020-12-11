package InlineMethod;

class PizzaDelivery {
    final int numberofLateDeliveries;

    public PizzaDelivery(int numberofLateDeliveries) {
        this.numberofLateDeliveries = numberofLateDeliveries;
    }

    int getRating() {
        boolean b = false;
        boolean res2 = moreThanFiveLateDeliveries(true);
        return moreThanFiveLateDeliveries(b) ? 2 : 1;
    }

    boolean moreThanFiveLateDeliveries(boolean bx) {
        return (numbe<caret>rofLateDeliveries > 5 && bx);
    }
}