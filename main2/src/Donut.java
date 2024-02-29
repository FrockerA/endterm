class Donut extends Product {
    private String flavor;

    public Donut(int id, String name, double price, String flavor) {
        super(id, name, price);
        this.flavor = flavor;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Flavor: " + flavor);
    }
}