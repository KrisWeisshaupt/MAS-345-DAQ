// 
// Decompiled by Procyon v0.5.36
// 

package about;

import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.Desktop;
import java.net.URI;
import javafx.scene.Cursor;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import window.ClientController;
import javafx.fxml.Initializable;

public class AboutController implements Initializable, ClientController
{
    @FXML
    private Hyperlink fontLink;
    @FXML
    private Label nooleanLabel;
    @FXML
    private Hyperlink nooleanLink;
    @FXML
    private Label rxtxLabel;
    @FXML
    private Hyperlink rxtxLink;
    @FXML
    private ImageView donate;
    private Stage stage;
    
    public void fontFired(final ActionEvent event) {
        this.openLink(this.fontLink.getText());
        event.consume();
    }
    
    public void nooleanFired(final ActionEvent event) {
        this.openLink(this.nooleanLink.getText());
        event.consume();
    }
    
    public void rxtxFired(final ActionEvent event) {
        this.openLink(this.rxtxLink.getText());
        event.consume();
    }
    
    public void donateFired(final MouseEvent event) {
        this.openLink("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=E5MDKJHH8ZBHU&lc=US&item_name=Kristofer%20Weisshaupt%20%5bMAS%2d345%20DAQ%5d&item_number=MAS%2d345%20DAQ&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted");
        event.consume();
    }
    
    public void emailFired(final ActionEvent event) {
        this.openLink("mailto:nooleanbot@gmail.com?Subject=MAS-345%20DAQ");
        event.consume();
    }
    
    public void initialize(final URL fxmlFileLocation, final ResourceBundle resources) {
        assert this.nooleanLabel != null : "fx:id=\"nooleanLabel\" was not injected: check your FXML file 'About.fxml'.";
        assert this.nooleanLink != null : "fx:id=\"nooleanLink\" was not injected: check your FXML file 'About.fxml'.";
        assert this.rxtxLabel != null : "fx:id=\"rxtxLabel\" was not injected: check your FXML file 'About.fxml'.";
        assert this.rxtxLink != null : "fx:id=\"rxtxLink\" was not injected: check your FXML file 'About.fxml'.";
        this.donate.setCursor(Cursor.HAND);
        this.nooleanLabel.setText("Contact the Developer:\nKristofer Weisshaupt");
        this.rxtxLabel.setText("RXTX binary builds provided courtesy of \nMfizz Inc.");
    }
    
    private void openLink(final String url) {
        try {
            final URI u = new URI(url);
            Desktop.getDesktop().browse(u);
        }
        catch (URISyntaxException ex) {}
        catch (IOException ex2) {}
    }
    
    public void close() {
    }
    
    public void setStage(final Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return this.stage;
    }
}
