package app.unattach;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Constants;
import app.unattach.utils.Logger;
import app.unattach.view.Scenes;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFx extends Application {
  private static final Logger logger = Logger.get();

  @Override
  public void start(Stage stage) {
    try {
      logger.info("Starting " + Constants.PRODUCT_NAME + " .. (max memory in bytes: " + Runtime.getRuntime().maxMemory() + ")");
      Scenes.init(stage);
      Controller controller = ControllerFactory.getDefaultController();
      if (controller.getConfig().getSignInAutomatically()) {
        controller.signIn();
        Scenes.setScene(Scenes.MAIN);
      } else {
        Scenes.setScene(Scenes.SIGN_IN);
      }
    } catch (Throwable t) {
      logger.error("Failed to start.", t);
    }
  }

  public static void main(String[] args) {
    Application.launch(MainFx.class, args);
  }
}
