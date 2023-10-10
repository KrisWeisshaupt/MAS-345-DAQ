// 
// Decompiled by Procyon v0.5.36
// 

package serialComm;

import javafx.concurrent.Task;

public class PollingTask extends Task
{
    private final SerialComm serial;
    
    public PollingTask(final SerialComm serial) {
        this.serial = serial;
    }
    
    protected Object call() throws Exception {
        while (!this.isCancelled()) {
            this.serial.Poll();
            Thread.sleep(this.serial.getDelay());
        }
        return null;
    }
}
