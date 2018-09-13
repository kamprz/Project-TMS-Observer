package events.listeners;

import events.NewNBPDataEvent;

public interface INewNBPDataListener {
    public void dataReceived(NewNBPDataEvent newData);
}
