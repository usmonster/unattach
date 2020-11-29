package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.model.DonationOption;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;

import static app.unattach.model.Constants.CURRENCIES;
import static app.unattach.model.Constants.DEFAULT_CURRENCY;

public class DonationViewUtils {
    static List<DonationOption> getDonationOptions() {
        return Arrays.asList(
                new DonationOption("Espresso", 2, DEFAULT_CURRENCY),
                new DonationOption("Cappuccino", 5, DEFAULT_CURRENCY),
                new DonationOption("Caramel Machiato", 10, DEFAULT_CURRENCY),
                new DonationOption("Bag of Coffee", 25, DEFAULT_CURRENCY),
                new DonationOption("Coffee Machine", 50, DEFAULT_CURRENCY),
                new DonationOption("A Truck of Coffee", 0, DEFAULT_CURRENCY)
        );
    }

    static void configureDonationControls(Controller controller, final List<DonationOption> donationOptions,
                                          ComboBox<DonationOption> buyCoffeeComboBox, ComboBox<String> currencyComboBox) {
        currencyComboBox.getItems().setAll(CURRENCIES);
        String initialCurrency = DEFAULT_CURRENCY;
        currencyComboBox.getSelectionModel().select(initialCurrency);
        for (DonationOption donationOption : donationOptions) {
            donationOption.setCurrency(initialCurrency);
        }
        buyCoffeeComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<DonationOption> call(ListView<DonationOption> p) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(DonationOption item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.toString());
                            getStyleClass().add("buy-coffee-item");
                        }
                    }
                };
            }
        });
        buyCoffeeComboBox.getItems().setAll(donationOptions);
        buyCoffeeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DonationOption item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Buy Developers a Coffee ☕");
                } else {
                    setText(item.toString());
                }
            }
        });
        buyCoffeeComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            controller.donate(newValue.getName(), newValue.getAmount(), newValue.getCurrency());
            Platform.runLater(() -> buyCoffeeComboBox.getSelectionModel().clearSelection());
        });
        currencyComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldValue, newValue) -> {
            buyCoffeeComboBox.getItems().clear();
            for (DonationOption donationOption : donationOptions) {
                donationOption.setCurrency(newValue);
            }
            buyCoffeeComboBox.getItems().addAll(donationOptions);
        });
    }
}
