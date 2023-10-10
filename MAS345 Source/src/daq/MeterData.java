// 
// Decompiled by Procyon v0.5.36
// 

package daq;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MeterData
{
    private StringProperty mode;
    private StringProperty date;
    private StringProperty time;
    private StringProperty value;
    private StringProperty normValue;
    private StringProperty units;
    
    public MeterData(final String mode, final String date, final String time, final String value, final String normValue, final String units) {
        this.mode = (StringProperty)new SimpleStringProperty((mode == null) ? "null" : mode);
        this.date = (StringProperty)new SimpleStringProperty((date == null) ? "null" : date);
        this.time = (StringProperty)new SimpleStringProperty((time == null) ? "null" : time);
        this.value = (StringProperty)new SimpleStringProperty((value == null) ? "null" : value);
        this.normValue = (StringProperty)new SimpleStringProperty((normValue == null) ? "null" : normValue);
        this.units = (StringProperty)new SimpleStringProperty((units == null) ? "null" : units);
    }
    
    public String getTime() {
        return (String)this.time.get();
    }
    
    public String getDate() {
        return (String)this.date.get();
    }
    
    public String getValue() {
        return (String)this.value.get();
    }
    
    public String getNormValue() {
        return (String)this.normValue.get();
    }
    
    public String getUnits() {
        return (String)this.units.get();
    }
    
    public String getMode() {
        return (String)this.mode.get();
    }
}
