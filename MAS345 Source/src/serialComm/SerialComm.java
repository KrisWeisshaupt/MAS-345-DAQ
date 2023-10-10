// 
// Decompiled by Procyon v0.5.36
// 

package serialComm;

import gnu.io.SerialPortEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.io.IOException;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.PortInUseException;
import gnu.io.CommPortIdentifier;
import java.io.File;
import java.util.Calendar;
import daq.DAQModel;
import java.io.OutputStream;
import java.io.InputStream;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;

public class SerialComm implements SerialPortEventListener
{
    private SerialPort serialPort;
    private StringBuilder builder;
    private InputStream input;
    private OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 600;
    private DAQModel model;
    private int delay;
    private String portName;
    private boolean graphing;
    private boolean connected;
    private String current;
    private Calendar cal;
    private boolean haveRXTX;
    private String library;
    
    public SerialComm(final DAQModel model) {
        this.builder = new StringBuilder(14);
        this.delay = 1000;
        this.connected = false;
        this.haveRXTX = true;
        this.library = "UnknownOS.Library";
        this.model = model;
        final String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            this.library = "rxtxSerial.dll";
            this.haveRXTX = new File(this.library).exists();
        }
        else if (OS.contains("mac")) {
            this.library = "librxtxSerial.jnilib";
        }
        else if (OS.contains("nux")) {
            this.library = "librxtxSerial.so";
        }
        if (!this.haveRXTX) {
            model.sendStatus("ERROR: Could not locate appropriate rxtxSerial library (See README.txt for help)");
            model.sendPortSelection(0);
        }
    }
    
    public int connect(final String port) {
        if (!this.haveRXTX) {
            this.model.sendStatus("ERROR: Could not locate appropriate rxtxSerial library (See README.txt for help)");
            return 0;
        }
        this.portName = port;
        CommPortIdentifier portId = null;
        final Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(this.portName)) {
                portId = currPortId;
                break;
            }
        }
        if (portId == null) {
            this.model.sendStatus("Could not find COM port.");
            System.out.println("Could not find COM port.");
            this.model.alertConnStatus(0);
            this.connected = false;
            return 0;
        }
        try {
            (this.serialPort = (SerialPort)portId.open(this.getClass().getName(), 2000)).setSerialPortParams(600, 7, 2, 0);
            this.input = this.serialPort.getInputStream();
            this.output = this.serialPort.getOutputStream();
            this.serialPort.addEventListener((SerialPortEventListener)this);
            this.serialPort.notifyOnDataAvailable(true);
            this.model.sendStatus("Connected to: " + this.portName);
            this.connected = true;
            return 2;
        }
        catch (PortInUseException | UnsupportedCommOperationException | IOException | TooManyListenersException ex2) {

            final Exception e = ex2;
            this.close();
            this.model.alertConnStatus(0);
            this.model.sendStatus("Error during connection.");
            System.out.println(e.toString());
            this.connected = false;
            return 0;
        }
    }
    
    public ObservableList<String> getPorts() {
        final ObservableList<String> ports = FXCollections.observableArrayList();
        if (!this.haveRXTX) {
            this.model.sendStatus("ERROR: Could not locate appropriate rxtxSerial library (See README.txt for help)");
            ports.add("ERROR");
            this.model.sendPortSelection(0);
            return ports;
        }
        final Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getPortType() == 1) {
                ports.add(currPortId.getName());
            }
        }
        if (ports.isEmpty()) {
            this.model.sendStatus("No serial ports found.");
            ports.add("Ports");
            this.model.sendPortSelection(0);
        }
        return ports;
    }
    
    public void setDelay(final int delay) {
        this.delay = delay;
    }
    
    public int getDelay() {
        return this.delay;
    }
    
    public void Poll() {
        if (this.connected) {
            try {
                System.out.println("Poll");
                this.output.write(0);
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    
    public synchronized int close() {
        if (this.serialPort != null) {
            try {
                this.serialPort.removeEventListener();
                this.serialPort.close();
                this.output.close();
                this.input.close();
                System.out.println("Closed Connection.");
                this.model.alertConnStatus(0);
                this.connected = false;
                this.model.sendStatus("Closed connection to: " + this.portName);
                return 0;
            }
            catch (Exception ex) {
                this.model.sendStatus("Error closing connection to: " + this.portName);
                System.out.println(ex);
            }
        }
        return 1;
    }
    
    public synchronized void serialEvent(final SerialPortEvent oEvent) {
        try {
            final int c = this.input.read();
            if (c == 13) {
                this.current = this.builder.toString();
                this.builder = new StringBuilder(13);
                this.cal = Calendar.getInstance();
                this.model.relay(this.current, this.cal.getTime(), this.graphing);
            }
            else {
                this.builder.append((char)c);
            }
        }
        catch (Exception ex) {}
    }
    
    public void setGraphing(final boolean graph) {
        this.graphing = graph;
    }
    
    public boolean isConnected() {
        return this.connected;
    }
}
