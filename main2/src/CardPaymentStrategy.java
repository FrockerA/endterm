class CardPaymentStrategy implements PaymentStrategy {
    private String cardNumber;
    private String expiryDate;

    public CardPaymentStrategy(String cardNumber, String expiryDate) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " with card ending in " + cardNumber.substring(cardNumber.length() - 4));
    }
}