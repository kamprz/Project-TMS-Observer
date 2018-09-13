package events;

import java.util.EventObject;

public class InternetConnectionInterruptedEvent extends EventObject
{
    public InternetConnectionInterruptedEvent(Object source)
    {
        super(source);
    }
}
