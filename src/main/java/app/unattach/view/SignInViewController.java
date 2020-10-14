package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Constants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static app.unattach.model.Constants.CURRENCIES;
import static app.unattach.model.Constants.DEFAULT_CURRENCY;

public class SignInViewController {
  private static final Logger LOGGER = Logger.getLogger(SignInViewController.class.getName());

  private Controller controller;
  private List<DonationOption> donationOptions;
  @FXML
  private ComboBox<DonationOption> buyCoffeeComboBox;
  @FXML
  private ComboBox<String> currencyComboBox;
  @FXML
  private Button homepageButton;
  @FXML
  private Button termsAndConditionsButton;
  @FXML
  private Button signInButton;
  @FXML
  private CheckBox subscribeToUpdatesCheckBox;
  @FXML
  public Label versionMessage;

  @FXML
  public void initialize() {
    controller = ControllerFactory.getDefaultController();
    currencyComboBox.getItems().setAll(CURRENCIES);
    String initialCurrency = DEFAULT_CURRENCY;
    currencyComboBox.getSelectionModel().select(initialCurrency);
    donationOptions = Arrays.asList(
            new DonationOption("Espresso", 2, initialCurrency),
            new DonationOption("Cappuccino", 5, initialCurrency),
            new DonationOption("Caramel Machiato", 10, initialCurrency),
            new DonationOption("Bag of Coffee", 25, initialCurrency),
            new DonationOption("Coffee Machine", 50, initialCurrency),
            new DonationOption("A Truck of Coffee", 0, initialCurrency)
    );
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
      controller.donate(newValue.name, newValue.amount, newValue.currency);
      Platform.runLater(() -> buyCoffeeComboBox.getSelectionModel().clearSelection());
    });
    currencyComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldValue, newValue) -> {
      buyCoffeeComboBox.getItems().clear();
      for (DonationOption donationOption : donationOptions) {
        donationOption.currency = newValue;
      }
      buyCoffeeComboBox.getItems().addAll(donationOptions);
    });
    Platform.runLater(() -> signInButton.requestFocus());
    Platform.runLater(this::checkLatestVersion);
  }

  @FXML
  private void onHomepageButtonPressed() {
    controller.openUnattachHomepage();
  }

  @FXML
  protected void onTermsAndConditionsButtonPressed() {
    controller.openTermsAndConditions();
  }

  @FXML
  protected void onSignInWithGoogleButtonPressed() {
    disableControls();
    Task<Void> task = new Task<>() {
      @Override
      protected Void call() throws Exception {
        String emailAddress = controller.signIn();
        if (subscribeToUpdatesCheckBox.isSelected()) {
          LOGGER.info("Subscribing to updates..");
          controller.subscribe(emailAddress);
          LOGGER.info("Subscribing to updates.. successful.");
        }
        return null;
      }

      @Override
      protected void succeeded() {
        try {
          Scenes.setScene(Scenes.MAIN);
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Failed to load the main view.", e);
          e.printStackTrace();
        } finally {
          resetControls();
        }
      }

      @Override
      protected void cancelled() {
        LOGGER.log(Level.WARNING, "Signing in cancelled.");
      }

      @Override
      protected void failed() {
        LOGGER.log(Level.SEVERE, "Failed to process sign in button click.", getException());
        resetControls();
      }
    };

    new Thread(task).start();
  }

  private void checkLatestVersion() {
    DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(Constants.VERSION);
    DefaultArtifactVersion latestVersion = controller.getLatestVersion();
    if (latestVersion == null) {
      versionMessage.setText("Failed to find out the latest version.");
    } else if (currentVersion.compareTo(latestVersion) >= 0) {
      versionMessage.setText("You have the latest version.");
    } else {
      versionMessage.setText("Newer version available: " + latestVersion);
    }
  }

  private void disableControls() {
    homepageButton.setDisable(true);
    termsAndConditionsButton.setDisable(true);
    signInButton.setDisable(true);
    subscribeToUpdatesCheckBox.setDisable(true);
  }

  private void resetControls() {
    homepageButton.setDisable(false);
    termsAndConditionsButton.setDisable(false);
    signInButton.setDisable(false);
    subscribeToUpdatesCheckBox.setDisable(false);
  }

  private static class DonationOption {
    private final String name;
    private final int amount;
    private String currency;

    private DonationOption(String name, int amount, String currency) {
      this.name = name;
      this.amount = amount;
      this.currency = currency;
    }

    @Override
    public String toString() {
      return name + " (" + (amount == 0 ? "custom" : amount + " " + currency) + ")";
    }
  }
}
