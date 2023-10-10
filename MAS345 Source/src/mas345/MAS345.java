// 
// Decompiled by Procyon v0.5.36
// 

package mas345;

import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import daq.DAQController;
import javafx.scene.image.Image;
import window.CustomStageBuilder;
import java.io.File;
import javafx.stage.Stage;
import javafx.application.Application;

public class MAS345 extends Application
{
    @Override
    public void start(final Stage primaryStage) {
        try {
            final String OS = System.getProperty("os.name").toLowerCase();
            String library = "";
            if (OS.contains("win")) {
                library = "rxtxSerial.dll";
            }
            else if (OS.contains("mac")) {
                library = "librxtxSerial.jnilib";
            }
            else if (OS.contains("nux")) {
                library = "librxtxSerial.so";
            }
            final Boolean haveRXTX = new File(library).exists();
            if (haveRXTX) {
                final CustomStageBuilder mainBuilder = new CustomStageBuilder(this.getClass().getResource("/daq/DAQ.fxml"), primaryStage);
                mainBuilder.setStageStyleSheet(this.getClass().getResource("/skin/skin.css"));
                mainBuilder.getWindowController().setTitle("MAS-345 Data Acquisition");
                primaryStage.setTitle("MAS-345 Data Acquisition");
                final Image img = new Image("/skin/MAS-345_StageIcon.png");
                mainBuilder.getWindowController().setIcon(img);
                primaryStage.getIcons().removeAll(new Image[0]);
                primaryStage.getIcons().add(img);
                final DAQController daqControl = (DAQController)mainBuilder.getClientController();
                daqControl.setStage(primaryStage);
                mainBuilder.showStage();
            }
            else {
                final Label errorLabel = new Label();
                errorLabel.setText("Could not locate appropriate RXTX library: \n" + library);
                final StackPane root = new StackPane();
                root.getChildren().add(errorLabel);
                final Scene scene = new Scene((Parent)root, 255.0, 80.0);
                primaryStage.initStyle(StageStyle.UTILITY);
                primaryStage.setResizable(false);
                primaryStage.setTitle("Missing RXTX Library");
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        }
        catch (Exception ex) {
            final Label errorLabel2 = new Label();
            errorLabel2.setText("Startup error: \n Check that Java and OS architectures match.");
            final StackPane root2 = new StackPane();
            root2.getChildren().add(errorLabel2);
            final Scene scene2 = new Scene((Parent)root2, 260.0, 80.0);
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Startup Error");
            primaryStage.setScene(scene2);
            primaryStage.show();
        }
    }
    
    public static void main(final String[] args) {
        launch(args);
    }
}
