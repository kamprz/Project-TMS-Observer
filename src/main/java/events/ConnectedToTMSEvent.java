package events;

import java.util.EventObject;

public class ConnectedToTMSEvent extends EventObject 
{
    private Boolean isConnected;
    public ConnectedToTMSEvent(Object source, Boolean isConnected) {
        super(source);
        this.isConnected = isConnected;
    }
    public boolean getIsConnected()
    {
        return isConnected;
    }
}
