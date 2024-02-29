class GooglePayPaymentStrategy implements PaymentStrategy {
    private String phoneNumber;

    public GooglePayPaymentStrategy(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Google Pay linked to phone number: " + phoneNumber);
    }
}