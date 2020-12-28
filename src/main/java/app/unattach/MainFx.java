package app.unattach;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Constants;
import app.unattach.view.Scenes;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MainFx extends Application {
  private static final Logger LOGGER = Logger.getLogger(MainFx.class.getName());

  @Override
  public void start(Stage stage) {
    try {
      LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
      LOGGER.info("Starting " + Constants.PRODUCT_NAME + " .. (max memory in bytes: " + Runtime.getRuntime().maxMemory() + ")");
      Scenes.init(stage);
      Controller controller = ControllerFactory.getDefaultController();
      if (controller.getConfig().getSignInAutomatically()) {
        controller.signIn();
        Scenes.setScene(Scenes.MAIN);
      } else {
        Scenes.setScene(Scenes.SIGN_IN);
      }
    } catch (Throwable t) {
      LOGGER.log(Level.SEVERE, "Failed to start.", t);
    }
  }

  public static void main(String[] args) {
    Application.launch(MainFx.class, args);
  }
}
