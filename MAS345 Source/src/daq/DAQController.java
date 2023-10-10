// 
// Decompiled by Procyon v0.5.36
// 

package daq;

import javafx.util.StringConverter;
import java.text.DecimalFormat;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.effect.Effect;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.effect.BlurType;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import window.ClientController;
import javafx.fxml.Initializable;

public class DAQController implements Initializable, ClientController
{
    @FXML
    private MenuItem aboutMenu;
    @FXML
    private Label acLabel;
    @FXML
    private Label capLabel;
    @FXML
    private Label celsiusLabel;
    @FXML
    private MenuItem clearGraphMenu;
    @FXML
    private MenuItem clearTableMenu;
    @FXML
    private ImageView connStatus;
    @FXML
    private ToggleButton connectToggle;
    @FXML
    private ImageView diodeImage;
    @FXML
    private MenuItem exportGraphMenu;
    @FXML
    private MenuItem exportTableMenu;
    @FXML
    private Label faradLabel;
    @FXML
    private Button graphAdd;
    @FXML
    private ToggleButton graphToggle;
    @FXML
    private Label lcdLabel;
    @FXML
    private MenuButton menu;
    @FXML
    private Label ohmLabel;
    @FXML
    private Button pollButton;
    @FXML
    private Button pollDown;
    @FXML
    private TextField pollField;
    @FXML
    private ToggleButton pollToggle;
    @FXML
    private Button pollUp;
    @FXML
    private Label status;
    @FXML
    private Label tempLabel;
    @FXML
    private Label voltLabel;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ScatterChart<Double, Double> graph;
    @FXML
    private ChoiceBox<String> portChoice;
    @FXML
    private TableView<MeterData> table;
    @FXML
    private TableColumn<MeterData, String> modeCol;
    @FXML
    private TableColumn<MeterData, String> normValueCol;
    @FXML
    private TableColumn<MeterData, String> timeCol;
    @FXML
    private TableColumn<MeterData, String> unitsCol;
    @FXML
    private TableColumn<MeterData, String> valueCol;
    @FXML
    private TableColumn<MeterData, String> dateCol;
    private DAQModel model;
    private Stage stage;
    
    public void connectToggleFired(final ActionEvent event) {
        Platform.runLater((Runnable)new Runnable() {
            @Override
            public void run() {
                if (DAQController.this.portChoice.getSelectionModel().isEmpty()) {
                    DAQController.this.setStatus("No Port Selected.");
                    DAQController.this.setConnStatus(0);
                }
                else if (DAQController.this.connectToggle.isSelected()) {
                    DAQController.this.setConnStatus(DAQController.this.model.openConn((String)DAQController.this.portChoice.getSelectionModel().getSelectedItem(), DAQController.this.portChoice.getSelectionModel().getSelectedIndex()));
                }
                else {
                    DAQController.this.setConnStatus(DAQController.this.model.closeConn());
                }
            }
        });
        event.consume();
    }
    
    public void connectTogglePressed(final MouseEvent event) {
        this.setConnStatus(1);
        event.consume();
    }
    
    public void pollButtonFired(final ActionEvent event) {
        this.model.Poll();
        event.consume();
    }
    
    public void pollToggleFired(final ActionEvent event) {
        this.model.pollToggle(this.pollToggle.isSelected());
        this.pollButton.setDisable(this.pollToggle.isSelected());
        event.consume();
    }
    
    public void pollingRateDown(final ActionEvent event) {
        this.pollField.setText(Integer.parseInt(this.pollField.getText()) - 125 + "");
        event.consume();
    }
    
    public void pollingRateUp(final ActionEvent event) {
        this.pollField.setText(Integer.parseInt(this.pollField.getText()) + 125 + "");
        event.consume();
    }
    
    public void addButtonFired(final ActionEvent event) {
        this.model.addCurrent();
        event.consume();
    }
    
    public void addToggleFired(final ActionEvent event) {
        this.graphAdd.setDisable(this.graphToggle.isSelected());
        this.model.setGraphing(this.graphToggle.isSelected());
        event.consume();
    }
    
    public void portChoiceFired(final MouseEvent event) {
        this.model.closeConn();
        System.out.println("Retreiving Ports");
        this.portChoice.setItems((ObservableList)this.model.getPorts());
        event.consume();
    }
    
    public void aboutMenuFired(final ActionEvent event) {
        this.model.showAbout();
        event.consume();
    }
    
    public void clearGraphMenuFired(final ActionEvent event) {
        this.model.clearGraph();
        event.consume();
    }
    
    public void clearTableMenuFired(final ActionEvent event) {
        this.model.clearTable();
        event.consume();
    }
    
    public void exportGraphMenuFired(final ActionEvent event) {
        this.model.exportGraph();
        event.consume();
    }
    
    public void exportTableMenuFired(final ActionEvent event) {
        this.model.exportTable();
        event.consume();
    }
    
    public void setStatus(final String s) {
        System.out.println(s);
        this.status.setText(s);
    }
    
    public void setMode(final String s) {
        this.tempLabel.setVisible(false);
        this.acLabel.setVisible(false);
        this.capLabel.setVisible(false);
        this.celsiusLabel.setVisible(false);
        this.faradLabel.setVisible(false);
        this.ohmLabel.setVisible(false);
        this.voltLabel.setVisible(false);
        this.diodeImage.setVisible(false);
        switch (s) {
            case "DC": {
                this.acLabel.setText("DC");
                this.acLabel.setVisible(true);
                break;
            }
            case "AC": {
                this.acLabel.setText("AC");
                this.acLabel.setVisible(true);
                break;
            }
            case "DI": {
                this.diodeImage.setVisible(true);
                break;
            }
            case "TE": {
                this.tempLabel.setVisible(true);
                break;
            }
            case "CA": {
                this.capLabel.setVisible(true);
                break;
            }
        }
    }
    
    public void setUnits(final String s) {
        switch (s) {
            case "MOhm": {
                this.ohmLabel.setText("M\u03a9");
                this.ohmLabel.setVisible(true);
                break;
            }
            case "kOhm": {
                this.ohmLabel.setText("k\u03a9");
                this.ohmLabel.setVisible(true);
                break;
            }
            case " Ohm": {
                this.ohmLabel.setText("\u03a9");
                this.ohmLabel.setVisible(true);
                break;
            }
            case "  mV": {
                this.voltLabel.setText("mV");
                this.voltLabel.setVisible(true);
                break;
            }
            case "   V": {
                this.voltLabel.setText("V");
                this.voltLabel.setVisible(true);
                break;
            }
            case "  mA": {
                this.voltLabel.setText("mA");
                this.voltLabel.setVisible(true);
                break;
            }
            case "   A": {
                this.voltLabel.setText("A");
                this.voltLabel.setVisible(true);
                break;
            }
            case "  nF": {
                this.faradLabel.setText("nF");
                this.faradLabel.setVisible(true);
                break;
            }
            case "   C": {
                this.celsiusLabel.setText("°C");
                this.celsiusLabel.setVisible(true);
                break;
            }
        }
    }
    
    public void setConnStatus(final int i) {
        switch (i) {
            case 0: {
                this.connectToggle.setSelected(false);
                this.connStatus.setImage(new Image("/skin/disconnected.png"));
                break;
            }
            case 1: {
                this.connStatus.setImage(new Image("/skin/connecting.png"));
                break;
            }
            case 2: {
                this.connStatus.setImage(new Image("/skin/connected.png"));
                break;
            }
            default: {
                System.out.println("Invalid connStatus.");
                break;
            }
        }
    }
    
    public void setValue(final String s) {
        this.lcdLabel.setText(s);
    }
    
    public void close() {
        this.setStatus("Closing...");
        this.model.close();
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException ex) {}
        System.exit(0);
    }
    
    public void setAxisLabel(final String axis) {
        this.yAxis.setLabel(axis);
    }
    
    public void initialize(final URL fxmlFileLocation, final ResourceBundle resources) {
        assert this.aboutMenu != null : "fx:id=\"aboutMenu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.acLabel != null : "fx:id=\"acLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.capLabel != null : "fx:id=\"capLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.celsiusLabel != null : "fx:id=\"celsiusLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.clearGraphMenu != null : "fx:id=\"clearGraphMenu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.clearTableMenu != null : "fx:id=\"clearTableMenu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.connStatus != null : "fx:id=\"connStatus\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.connectToggle != null : "fx:id=\"connectToggle\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.dateCol != null : "fx:id=\"dateCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.diodeImage != null : "fx:id=\"diodeImage\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.exportGraphMenu != null : "fx:id=\"exportGraphMenu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.exportTableMenu != null : "fx:id=\"exportTableMenu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.faradLabel != null : "fx:id=\"faradLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.graph != null : "fx:id=\"graph\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.graphAdd != null : "fx:id=\"graphAdd\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.graphToggle != null : "fx:id=\"graphToggle\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.lcdLabel != null : "fx:id=\"lcdLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.menu != null : "fx:id=\"menu\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.modeCol != null : "fx:id=\"modeCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.normValueCol != null : "fx:id=\"normValueCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.ohmLabel != null : "fx:id=\"ohmLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.pollButton != null : "fx:id=\"pollButton\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.pollDown != null : "fx:id=\"pollDown\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.pollField != null : "fx:id=\"pollField\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.pollToggle != null : "fx:id=\"pollToggle\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.pollUp != null : "fx:id=\"pollUp\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.portChoice != null : "fx:id=\"portChoice\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.status != null : "fx:id=\"status\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.table != null : "fx:id=\"table\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.tempLabel != null : "fx:id=\"tempLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.timeCol != null : "fx:id=\"timeCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.unitsCol != null : "fx:id=\"unitsCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.valueCol != null : "fx:id=\"valueCol\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.voltLabel != null : "fx:id=\"voltLabel\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.xAxis != null : "fx:id=\"xAxis\" was not injected: check your FXML file 'DAQ.fxml'.";
        assert this.yAxis != null : "fx:id=\"yAxis\" was not injected: check your FXML file 'DAQ.fxml'.";
        this.setStatus("Status: MAS-345 DAQ - Select Port.");
        this.model = new DAQModel(this, this.graph, this.table);
        final Font digital = Font.loadFont(this.getClass().getResource("/skin/digital-7 (mono italic).ttf").toExternalForm(), 110.0);
        this.lcdLabel.setFont(digital);
        this.setConnStatus(0);
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Press to automatically add new \ndata points to the graph  \n");
        this.graphToggle.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Polling Period (ms) \nShould be >= 1000   \n");
        this.pollField.setTooltip(tooltip);
        final InnerShadow inner = new InnerShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 12.0, 0.25, 0.0, 0.0);
        inner.setHeight(45.0);
        inner.setWidth(18.0);
        final DropShadow drop = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK, 9.5, 0.135, 0.0, 0.0);
        drop.setInput((Effect)inner);
        this.connectToggle.setEffect((Effect)drop);
        this.portChoice.setEffect((Effect)drop);
        this.graphAdd.setEffect((Effect)drop);
        this.graphToggle.setEffect((Effect)drop);
        this.pollButton.setEffect((Effect)drop);
        this.pollDown.setEffect((Effect)drop);
        this.pollUp.setEffect((Effect)drop);
        this.pollToggle.setEffect((Effect)drop);
        this.menu.setEffect((Effect)drop);
        this.pollField.textProperty().addListener((ChangeListener)new ChangeListener<String>() {
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    try {
                        if (newValue.endsWith("f") || newValue.endsWith("d")) {
                            newValue = newValue.substring(0, newValue.length() - 1);
                            DAQController.this.pollField.setText(newValue);
                        }
                        final int i = Integer.parseInt(newValue);
                        if (i > 250) {
                            DAQController.this.model.setDelay(i);
                        }
                        if (i < 0) {
                            DAQController.this.pollField.setText(oldValue);
                        }
                    }
                    catch (Exception e) {
                        DAQController.this.pollField.setText(oldValue);
                    }
                }
            }
        });
        this.modeCol.setCellValueFactory((Callback)new PropertyValueFactory("mode"));
        this.dateCol.setCellValueFactory((Callback)new PropertyValueFactory("date"));
        this.timeCol.setCellValueFactory((Callback)new PropertyValueFactory("time"));
        this.valueCol.setCellValueFactory((Callback)new PropertyValueFactory("value"));
        this.normValueCol.setCellValueFactory((Callback)new PropertyValueFactory("normValue"));
        this.unitsCol.setCellValueFactory((Callback)new PropertyValueFactory("units"));
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.yAxis.setTickLabelFormatter((StringConverter)new NumberAxis.DefaultFormatter(this.yAxis) {
            private DecimalFormat df = new DecimalFormat("0.0###");
            
            public String toString(final Number object) {
                return this.df.format(object);
            }
        });
        this.populatePortList();
        System.out.println("Finished GUI Initialization.");
    }
    
    public void populatePortList() {
        this.portChoice.setItems((ObservableList)this.model.getPorts());
        this.setStatus("Status: MAS-345 DAQ - Select Port.");
        this.portChoice.getSelectionModel().select(this.model.getDefaultPort());
    }
    
    public void setSelectedPort(final int index) {
        this.portChoice.getSelectionModel().select(index);
    }
    
    public String getAxisLabel() {
        return this.yAxis.getLabel();
    }
    
    public void setStage(final Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return this.stage;
    }
}
