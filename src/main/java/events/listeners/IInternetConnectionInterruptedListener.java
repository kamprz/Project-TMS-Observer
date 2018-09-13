package events.listeners;

import events.InternetConnectionInterruptedEvent;

public interface IInternetConnectionInterruptedListener {
    public void connectionInterrupted(InternetConnectionInterruptedEvent event);
}
