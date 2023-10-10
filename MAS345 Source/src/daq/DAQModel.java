// 
// Decompiled by Procyon v0.5.36
// 

package daq;

import javafx.scene.image.Image;
import window.CustomStageBuilder;
import java.util.Iterator;
import java.io.IOException;
import java.awt.Desktop;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javafx.stage.Window;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Date;
import javafx.scene.control.TableView;
import javafx.scene.chart.ScatterChart;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javafx.scene.chart.XYChart;
import serialComm.PollingTask;
import serialComm.SerialComm;
import java.util.prefs.Preferences;

public class DAQModel
{
    private DAQController control;
    private static Preferences prefs;
    private SerialComm serial;
    private PollingTask task;
    private XYChart.Series<Double, Double> series;
    private SimpleDateFormat sdf;
    private DecimalFormat df;
    private ScatterChart<Double, Double> graph;
    private TableView<MeterData> table;
    private boolean graphing;
    private String mode;
    private Date zeroTime;
    HashMap<String, String> graphModes;
    HashMap<String, String> graphUnits;
    HashMap<String, String> tableUnits;
    private static Stage about;
    
    public DAQModel(final DAQController control, final ScatterChart<Double, Double> graph, final TableView<MeterData> table) {
        this.sdf = new SimpleDateFormat("MM/dd  HH:mm:ss.SSSS");
        this.df = new DecimalFormat("0.0####");
        this.mode = "Null";
        this.graphModes = new HashMap<String, String>() {
            {
                this.put("TE", "Temperature");
                this.put("DC", "DC");
                this.put("AC", "AC");
                this.put("DI", "Diode");
                this.put("OH", "Resistance");
                this.put("CA", "Capacitance");
                this.put("  ", "Transistor");
            }
        };
        this.graphUnits = new HashMap<String, String>() {
            {
                this.put("MOhm", "(k\u03a9)");
                this.put("kOhm", "(k\u03a9)");
                this.put(" Ohm", "(k\u03a9)");
                this.put("  nF", "(nF)");
                this.put("  mA", "Current (A)");
                this.put("   A", "Current (A)");
                this.put("  mV", "Voltage (V)");
                this.put("   V", "Voltage (V)");
                this.put("   C", "(°C)");
                this.put("    ", "hFE");
            }
        };
        this.tableUnits = new HashMap<String, String>() {
            {
                this.put("MOhm", "k\u03a9");
                this.put("kOhm", "k\u03a9");
                this.put(" Ohm", "k\u03a9");
                this.put("  nF", "nF");
                this.put("  mA", "A");
                this.put("   A", "A");
                this.put("  mV", "V");
                this.put("   V", "V");
                this.put("   C", "°C");
                this.put("    ", "hFE");
            }
        };
        this.graph = graph;
        this.table = table;
        DAQModel.prefs = Preferences.userNodeForPackage(this.getClass());
        this.control = control;
        this.series = (XYChart.Series<Double, Double>)new XYChart.Series();
        this.series.getData().add(new XYChart.Data(1.0, 1.0));
        graph.getData().add(this.series);
        this.series.getData().clear();
        this.serial = new SerialComm(this);
    }
    
    public void sendStatus(final String message) {
        this.control.setStatus(message);
    }
    
    public void alertConnStatus(final int i) {
        this.control.setConnStatus(i);
    }
    
    public int openConn(final String portName, final int index) {
        DAQModel.prefs.putInt("port", index);
        return this.serial.connect(portName);
    }
    
    public int closeConn() {
        return this.serial.close();
    }
    
    public void cancelPolling() {
        if (this.task != null) {
            this.task.cancel();
        }
    }
    
    public int getDefaultPort() {
        int i = 0;
        try {
            i = DAQModel.prefs.getInt("port", i);
        }
        catch (Exception ex) {
            DAQModel.prefs.putInt("port", 0);
            i = 0;
        }
        return i;
    }
    
    public void Poll() {
        if (this.serial.isConnected()) {
            this.sendStatus("Polled Meter.");
            this.serial.Poll();
        }
    }
    
    public void pollToggle(final boolean selected) {
        if (selected) {
            this.task = new PollingTask(this.serial);
            final Thread th = new Thread((Runnable)this.task);
            th.setDaemon(true);
            th.start();
            this.sendStatus("Toggled Meter Polling.");
        }
        else {
            this.task.cancel();
            this.sendStatus("Cancelled Meter Polling.");
        }
    }
    
    public void addCurrent() {
        final Double x = Double.parseDouble(((MeterData)this.table.getItems().get(this.table.getItems().size() - 1)).getTime());
        final Double y = Double.parseDouble(((MeterData)this.table.getItems().get(this.table.getItems().size() - 1)).getNormValue());
        this.addPoint(x, y);
    }
    
    public void setGraphing(final boolean selected) {
        this.graphing = selected;
        this.serial.setGraphing(selected);
    }
    
    public void sendPortSelection(final int index) {
        this.control.setSelectedPort(index);
    }
    
    public ObservableList<String> getPorts() {
        return this.serial.getPorts();
    }
    
    public void setDelay(final int i) {
        this.serial.setDelay(i);
    }
    
    public void createGraph(final String axis, final Double number, final Boolean graphing) {
        this.series.getData().clear();
        this.control.setAxisLabel(axis);
        if (number != null && graphing) {
            this.series.getData().add(new XYChart.Data(0.0, number));
        }
        if (this.graph.getData().isEmpty()) {
            this.graph.getData().add(this.series);
        }
    }
    
    public void addPoint(final Double time, final Double number) {
        if (number != null) {
            this.series.getData().add(new XYChart.Data(time, number));
        }
    }
    
    public void incomingData(final String current, final String value, final String units, final Double number, final Date date, final Double diff) {
        this.control.setStatus("Received: " + current);
        this.control.setMode(this.mode);
        this.control.setUnits(units);
        this.control.setValue(value);
        String tableMode = this.graphModes.get(this.mode);
        if (this.mode.equals("AC") || this.mode.equals("DC")) {
            tableMode = tableMode + " " + this.graphUnits.get(units).substring(0, 7);
        }
        if (number == null) {
            this.table.getItems().add(new MeterData(tableMode, this.sdf.format(date), diff.toString(), value + units, "O.L", this.tableUnits.get(units)));
        }
        else {
            this.table.getItems().add(new MeterData(tableMode, this.sdf.format(date), diff.toString(), value + units, this.df.format(number), this.tableUnits.get(units)));
        }
        this.table.scrollTo(this.table.getItems().size());
    }
    
    void clearGraph() {
        this.zeroTime = null;
        this.series.getData().clear();
        if (this.graph.getData().isEmpty()) {
            this.graph.getData().add(this.series);
        }
    }
    
    void clearTable() {
        this.table.getItems().clear();
    }
    
    public void relay(final String current, final Date time, final boolean graph) {
        Platform.runLater((Runnable)new Runnable() {
            @Override
            public void run() {
                if (!current.isEmpty()) {
                    final boolean unitChange = !DAQModel.this.mode.equals(current.substring(0, 2));
                    DAQModel.this.mode = current.substring(0, 2);
                    final String units = current.substring(9, 13);
                    String value = current.substring(3, 9);
                    if (DAQModel.this.mode.equals("TE")) {
                        value = value.trim().replaceFirst("^0+(?!$)", "");
                    }
                    Double number = DAQModel.this.getDouble(value);
                    if (number != null) {
                        final String s = units;
                        switch (s) {
                            case "MOhm": {
                                number = Double.valueOf(Math.round(number * 1000.0));
                                break;
                            }
                            case " Ohm": {
                                number = Math.round(number * 1000.0) / 1000000.0;
                                break;
                            }
                            case "  mV": {
                                number = Math.round(number * 1000.0) / 1000000.0;
                                break;
                            }
                            case "  mA": {
                                number = Math.round(number * 1000.0) / 1000000.0;
                                break;
                            }
                        }
                    }
                    if (unitChange || DAQModel.this.zeroTime == null) {
                        DAQModel.this.zeroTime = time;
                        DAQModel.this.createGraph(DAQModel.this.graphModes.get(DAQModel.this.mode) + " " + DAQModel.this.graphUnits.get(units), number, graph);
                    }
                    final double difference = (time.getTime() - DAQModel.this.zeroTime.getTime()) / 1000.0;
                    if (graph) {
                        DAQModel.this.addPoint(difference, number);
                    }
                    DAQModel.this.incomingData(current, value, units, number, time, difference);
                }
            }
        });
    }
    
    private Double getDouble(final String s) {
        Double n;
        try {
            n = Double.parseDouble(s.trim());
        }
        catch (Exception ex) {
            n = null;
        }
        return n;
    }
    
    void exportGraph() {
        Platform.runLater((Runnable)new Runnable() {
            @Override
            public void run() {
                String filePref = DAQModel.prefs.get("graphFile", "");
                if (filePref.isEmpty()) {
                    filePref = System.getProperty("user.home");
                }
                File file = new File(filePref);
                final FileChooser fileChooser = new FileChooser();
                if (file.canRead()) {
                    fileChooser.setInitialDirectory(file);
                }
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[] { new FileChooser.ExtensionFilter("HTML file (*.html)", new String[] { "*.html" }) });
                if (file.canRead()) {
                    fileChooser.setInitialDirectory(file);
                }
                fileChooser.setTitle("Table Export File Chooser");
                file = fileChooser.showSaveDialog((Window)DAQModel.this.control.getStage());
                if (file != null) {
                    if (!file.getName().endsWith(".html")) {
                        file = new File(file.getAbsolutePath() + ".html");
                    }
                    DAQModel.prefs.put("graphFile", file.getParent());
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    }
                    catch (Exception ex) {
                        DAQModel.this.sendStatus("Could not access/create selected file.");
                        return;
                    }
                    try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write("<html>\n    <head>\n        <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\n        <script type=\"text/javascript\">\n            google.load(\"visualization\", \"1\", {packages: [\"corechart\"]});\n            google.setOnLoadCallback(drawChart);\n            function drawChart() {\n                var data = google.visualization.arrayToDataTable([\n");
                        writer.write("['Time (s)', '" + DAQModel.this.control.getAxisLabel().replace("\u03a9", "Ohms").replace("°", "") + "'],\n");
                        int i = DAQModel.this.series.getData().size();
                        for (final XYChart.Data point : DAQModel.this.series.getData()) {
                            if (--i > 0) {
                                writer.write("                     [" + point.getXValue() + ", " + point.getYValue() + "],\n");
                            }
                            else {
                                writer.write("                     [" + point.getXValue() + ", " + point.getYValue() + "]]);\n");
                            }
                        }
                        writer.write("                var options = {\n                    chartArea: {\n                        width: \"80%\",\n                        height: \"80%\"},\n                     pointSize: 4,\n                    hAxis: {title: 'Time (s)', minValue: 0},\n                    vAxis: {title: '" + DAQModel.this.control.getAxisLabel().replace("\u03a9", "Ohms").replace("°", "") + "', minValue: 0},\n" + "                    legend: 'none'\n" + "                };\n" + "                var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));\n" + "                google.visualization.events.addListener(chart, 'ready', getChartSVG); // ADD LISTENER\n" + "                chart.draw(data, options);\n" + "            }\n" + "\n" + "            function getChartSVG() {\n" + "                var e = document.getElementById('chart_div');\n" + "                // svg elements don't have inner/outerHTML properties, so use the parents\n" + "                document.getElementById(\"svg\").innerHTML = e.getElementsByTagName('svg')[0].parentNode.innerHTML;\n" + "            }\n" + "\n" + "            function selectElementContents(el) {\n" + "                el.select()\n" + "                document.getElementById(\"instructions\").innerHTML = \"SVG code selected.\";\n" + "                setTimeout(function() {\n" + "                    document.getElementById(\"instructions\").innerHTML = \"SVG used to create chart shown below.\";\n" + "                }, 3000);\n" + "            }\n" + "        </script>\n" + "    </head>\n" + "    <body>\n" + "\t\t<form style=\"float: left;\" action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_top\">\n" + "\t\t\t<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">\n" + "\t\t\t<input type=\"hidden\" name=\"encrypted\" value=\"-----BEGIN PKCS7-----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-----END PKCS7-----\n" + "\t\t\t\">\n" + "\t\t\t<input type=\"image\" src=\"https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">\n" + "\t\t\t<img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">\n" + "\t\t</form>\n" + "\t\t<p style=\"font-size: 20px;\"> Enjoying this software?  Please consider supporting the developer.</p>\n" + "        <div id=\"chart_div\" style=\"width: 600px; height: 350px;\"></div>\n" + "        <input type=\"button\" name=\"selectButton\" value=\"Select All\" onclick=\"selectElementContents(document.getElementById('svg'));\">\n" + "        <p id=\"instructions\">Below is the SVG code used to create the chart.</p>\n" + "        <textarea id =\"svg\" style=\"width: 600px; height: 700px;\">\n" + "        </textarea>\n" + "    </body>\n" + "</html>");
                        writer.flush();
                        writer.close();
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file);
                        }
                    }
                    catch (IOException ex2) {
                        DAQModel.this.sendStatus("ERROR: Could not create graph file.");
                    }
                }
            }
        });
    }
    
    void exportTable() {
        Platform.runLater((Runnable)new Runnable() {
            @Override
            public void run() {
                String filePref = DAQModel.prefs.get("tableFile", "");
                if (filePref.isEmpty()) {
                    filePref = System.getProperty("user.home");
                }
                File file = new File(filePref);
                final FileChooser fileChooser = new FileChooser();
                if (file.canRead()) {
                    fileChooser.setInitialDirectory(file);
                }
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[] { new FileChooser.ExtensionFilter("HTML file (*.html)", new String[] { "*.html" }) });
                fileChooser.setInitialDirectory(file);
                fileChooser.setTitle("Table Export File Chooser");
                file = fileChooser.showSaveDialog((Window)DAQModel.this.control.getStage());
                if (file != null) {
                    if (!file.getName().endsWith(".html")) {
                        file = new File(file.getAbsolutePath() + ".html");
                    }
                    DAQModel.prefs.put("tableFile", file.getParent());
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    }
                    catch (Exception ex) {
                        DAQModel.this.sendStatus("Could not access/create selected file.");
                        return;
                    }
                    try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write("<html>\n\t<head>\n\t\t<script type=\"text/javascript\">\n\t\t\tfunction selectElementContents(el) {\n\t\t\t\tvar body = document.body, range, sel;\n\t\t\t\tif (document.createRange && window.getSelection) {\n\t\t\t\t\trange = document.createRange();\n\t\t\t\t\tsel = window.getSelection();\n\t\t\t\t\tsel.removeAllRanges();\n\t\t\t\t\ttry {\n\t\t\t\t\t\trange.selectNodeContents(el);\n\t\t\t\t\t\tsel.addRange(range);\n\t\t\t\t\t\t} catch (e) {\n\t\t\t\t\t\trange.selectNode(el);\n\t\t\t\t\t\tsel.addRange(range);\n\t\t\t\t\t}\n\t\t\t\t\t} else if (body.createTextRange) {\n\t\t\t\t\trange = body.createTextRange();\n\t\t\t\t\trange.moveToElementText(el);\n\t\t\t\t\trange.select();\n\t\t\t\t}\n\t\t\t\tdocument.getElementById(\"instructions\").innerHTML=\"Table Selected, now Copy it into desired Spreadsheet Software\";\n\t\t\t\tsetTimeout(function(){\n\t\t\t\t\tdocument.getElementById(\"instructions\").innerHTML=\"Select Table and Copy into desired Spreadsheet Software\";\n\t\t\t\t},3000);\n\t\t\t}\n\t\t</script>\n\t</head>\n\t<body>\n\t\t<form style=\"float: left;\" action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_top\">\n\t\t\t<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">\n\t\t\t<input type=\"hidden\" name=\"encrypted\" value=\"-----BEGIN PKCS7-----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-----END PKCS7-----\n\t\t\t\">\n\t\t\t<input type=\"image\" src=\"https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">\n\t\t\t<img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">\n\t\t</form>\n\t\t<p style=\"font-size: 20px;\"> Enjoying this software?  Please consider supporting the developer.</p>\n\t\t<p id=\"instructions\">Select and Copy table into desired Spreadsheet Software</p>\n\t\t<input type=\"button\" name=\"selectButton\" value=\"Select Table\" onclick=\"selectElementContents( document.getElementById('table'));\">");
                        writer.write("\n\t\t<table id=\"table\" border=\"1\">\n\t\t\t<thead>\n\t\t\t\t<tr><th>Mode</th><th>Date</th><th>&Delta;Time (s)</th><th>Value</th><th>Normalized Value</th><th>Units</th></tr>\n\t\t\t</thead>\n\t\t\t<tbody>");
                        for (final MeterData meter : DAQModel.this.table.getItems()) {
                            writer.write("\n\t\t\t\t<tr><td>" + meter.getMode() + "</td><td>" + meter.getDate() + "</td><td>" + meter.getTime() + "</td><td>" + meter.getValue() + "</td><td>" + meter.getNormValue() + "</td><td>" + meter.getUnits().replace("\u03a9", "&Omega;").replace("°", "&deg;") + "</td></tr>");
                        }
                        writer.write("\n\t\t\t</tbody>\n\t\t</table>\n\t</body>\n</html>");
                        writer.flush();
                        writer.close();
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file);
                        }
                    }
                    catch (IOException ex2) {
                        DAQModel.this.sendStatus("ERROR: Could not create table file.");
                    }
                }
            }
        });
    }
    
    void showAbout() {
        if (DAQModel.about == null) {
            try {
                (DAQModel.about = new Stage()).setResizable(false);
                final CustomStageBuilder aboutBuilder = new CustomStageBuilder(this.getClass().getResource("/about/About.fxml"), DAQModel.about);
                aboutBuilder.setStageStyleSheet(this.getClass().getResource("/skin/skin.css"));
                aboutBuilder.getWindowController().setTitle("About");
                DAQModel.about.setTitle("About");
                final Image img = new Image("/skin/MAS-345_StageIcon.png");
                aboutBuilder.getWindowController().setIcon(img);
                DAQModel.about.getIcons().removeAll(new Image[0]);
                DAQModel.about.getIcons().add(img);
                aboutBuilder.getWindowController().setResizable(Boolean.valueOf(false));
                aboutBuilder.showStage();
                DAQModel.about.setX(this.control.getStage().getX() + this.control.getStage().getWidth() / 2.0 - DAQModel.about.getWidth() / 2.0);
                DAQModel.about.setY(this.control.getStage().getY() + this.control.getStage().getHeight() / 2.0 - DAQModel.about.getHeight() / 2.0);
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
        }
        else {
            DAQModel.about.close();
            DAQModel.about = null;
            this.showAbout();
        }
    }
    
    void close() {
        this.cancelPolling();
        this.closeConn();
        if (DAQModel.about != null) {
            DAQModel.about.close();
        }
    }
}
