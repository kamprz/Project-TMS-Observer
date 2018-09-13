package events;

import model.currencies.tms.TMSCurrencyFrame;
import java.util.EventObject;

public class NewTMSDataEvent extends EventObject
{
    private TMSCurrencyFrame data;
    public NewTMSDataEvent(Object source, TMSCurrencyFrame data) {
        super(source);
        this.data = data;
    }

    public TMSCurrencyFrame getData() {
        return data;
    }
}
