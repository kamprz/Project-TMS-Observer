package events.listeners;

import events.ServerWentOffEvent;

public interface IServerWentOfflineListener {
    public void serverOff(ServerWentOffEvent event);
}
