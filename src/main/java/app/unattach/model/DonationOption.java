package app.unattach.model;

public class DonationOption {
    private final String name;
    private final int amount;
    private String currency;

    public DonationOption(String name, int amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return name + " (" + (amount == 0 ? "custom" : amount + " " + currency) + ")";
    }
}
