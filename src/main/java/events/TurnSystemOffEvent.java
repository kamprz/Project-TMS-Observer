package events;

import java.util.EventObject;

public class TurnSystemOffEvent extends EventObject
{
    private Exception exception;
    public TurnSystemOffEvent(Object source) {
        super(source);
    }
    public TurnSystemOffEvent(Object source, Exception e) {
        super(source);
    }
    public TurnSystemOffEvent(Exception e)
    {//used in Dictionary
        super(e);
        exception=e;
    }
    public Exception getException() {
        return exception;
    }
}
