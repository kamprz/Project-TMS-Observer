package events;

import java.util.EventObject;

public class ServerWentOffEvent extends EventObject
{

    public ServerWentOffEvent(Object source) {
        super(source);
    }
}
