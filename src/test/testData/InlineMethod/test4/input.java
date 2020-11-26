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
        return moreThanFiveLateDeliveries(b, c, temp) ? 2 : 1;
    }

    boolean moreThanFiveLateDeliveries(boolean bx, int int_rand, String real) {
        return (numbe<caret>rofLateDeliveries > 5 && bx && int_rand != 124 && real != null);
    }
}