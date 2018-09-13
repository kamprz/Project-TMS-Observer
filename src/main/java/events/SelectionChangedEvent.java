package events;

import java.util.EventObject;

public class SelectionChangedEvent extends EventObject
{
    public SelectionChangedEvent(Object source) {
        super(source);
    }
}
