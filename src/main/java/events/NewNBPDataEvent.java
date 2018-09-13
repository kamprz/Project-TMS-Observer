package events;

import model.currencies.nbp.NBPCurrencyFrame;

import java.util.EventObject;

public class NewNBPDataEvent extends EventObject
{
    public NBPCurrencyFrame data;
    public NewNBPDataEvent(Object source, NBPCurrencyFrame data)
    {
        super(source);
        this.data = data;
    }
}
