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
        boolean par1 = b;
        int par2 = c;
        String par3 = temp;
        return (numberofLateDeliveries > 5 && par1 && par2 != 124 && par3 != null) ? 2 : 1;
    }

}