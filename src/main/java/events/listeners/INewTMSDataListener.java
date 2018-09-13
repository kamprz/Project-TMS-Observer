package events.listeners;

import events.NewTMSDataEvent;

public interface INewTMSDataListener {
    public long dataReceived(NewTMSDataEvent newData); //zwraca czas jaki minął od przyjścia poprzednich danych
}
