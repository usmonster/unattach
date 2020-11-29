package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Constants;
import app.unattach.model.DonationOption;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInViewController {
  private static final Logger LOGGER = Logger.getLogger(SignInViewController.class.getName());

  private Controller controller;
  @FXML
  private ComboBox<DonationOption> buyCoffeeComboBox;
  @FXML
  private ComboBox<String> currencyComboBox;
  @FXML
  private Label signInMessageLabel;
  @FXML
  private Button howUnattachWorksButton;
  @FXML
  private Button privacyPolicyButton;
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
    List<DonationOption> donationOptions = DonationViewUtils.getDonationOptions();
    DonationViewUtils.configureDonationControls(controller, donationOptions, buyCoffeeComboBox, currencyComboBox);
    signInMessageLabel.setText("The first time you sign in, you will be asked to give the app permissions to your Gmail.\n" +
            "Click on 'How Unattach Works' to see why this is required and how your privacy is protected.");
    Platform.runLater(() -> signInButton.requestFocus());
    Platform.runLater(this::checkLatestVersion);
  }

  @FXML
  private void onHowUnattachWorksButtonPressed() {
    controller.openWebPage(Constants.HOW_UNATTACH_WORKS_URL);
  }

  public void onPrivacyPolicyButtonPressed() {
    controller.openWebPage(Constants.PRIVACY_POLICY_URL);
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
    howUnattachWorksButton.setDisable(true);
    privacyPolicyButton.setDisable(true);
    termsAndConditionsButton.setDisable(true);
    signInButton.setDisable(true);
    subscribeToUpdatesCheckBox.setDisable(true);
  }

  private void resetControls() {
    howUnattachWorksButton.setDisable(false);
    privacyPolicyButton.setDisable(false);
    termsAndConditionsButton.setDisable(false);
    signInButton.setDisable(false);
    subscribeToUpdatesCheckBox.setDisable(false);
  }
}
