package controller;

import config.Dictionary;
import events.*;
import events.listeners.IInternetConnectionInterruptedListener;
import events.listeners.IServerWentOfflineListener;
import exceptions.SystemFailureException;
import installation.Loggers;
import config.PropertiesLoader;
import events.listeners.ICurrencyValCrossedGuardianValListener;
import model.currencies.CurrenciesSequence;
import model.currencies.tms.TMSCurrencyFrame;
import events.listeners.INewTMSDataListener;
import installation.ApplicationEngine;
import util.TimePassed;
import util.scraper.tms.ProcessCleaner;
import util.scraper.tms.ScrapTMSInspector;
import util.valueGuardian.CurrencyMonitorFactory;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TMSScrapManager implements INewTMSDataListener,ICurrencyValCrossedGuardianValListener,
        IServerWentOfflineListener, IInternetConnectionInterruptedListener
{
    private ApplicationMainFrame view;
    private static TMSScrapManager singleton;
    private ScrapTMSInspector[] scrapInspectors;
    private Thread[] scrapThreads;
    private final Logger logger = Loggers.TMSScrapManagerLogger;
    private int scrappingThreadsNumber;
    private int connectionsInterruptedInRow;
    private int uncheckedConnectionsInterrupted;
    private int uncheckedConnectionsInterruptedLimit;
    private boolean scrappedSthAlready = false;
    private boolean isServerOnline = true;
    private boolean isWorking=false;
    private boolean beingModified = false;
    private final Object modificationLock = new Object();
    private final Object connectionsLock = new Object();
    private final Object newDataLock = new Object();
    private final Object isWorkingLock = new Object();
    private LocalTime lastDataTime;

    private TMSScrapManager()
    {
        loadProperties();
        scrapInspectors = new ScrapTMSInspector[scrappingThreadsNumber];
        scrapThreads = new Thread[scrappingThreadsNumber];
        view = ApplicationMainFrame.getInstance();
        logger.info(Dictionary.load("TMSScrapManager.log.start"));
        createInspectors();
        setBeingModified(false);
    }
    public static synchronized TMSScrapManager getInstance()
    {
        if(singleton==null)
        {
            singleton = new TMSScrapManager();
        }
        return singleton;
    }

    //metoda powołująca zarządców pobierania danych
    private void createInspectors()
    {
        setWorking(true);
        setBeingModified(true);
        view.setState("stateLabel.downloading");
        for (int i = 0; isWorking() && i < scrappingThreadsNumber; i++)
        {
            logger.info(Dictionary.load("TMSScrapManager.log.inspectorCreated"));
            scrapInspectors[i] = new ScrapTMSInspector();
            scrapThreads[i] = new Thread(scrapInspectors[i]);
            scrapInspectors[i].addServerWentOffListeners(this);
            scrapInspectors[i].addInternetConnInterruptedListenersList(this);
            scrapThreads[i].start();
        }
    }

    //metoda odczytująca liczbę zaburzonych połączeń pod rząd (potrzebne do ustalenia czasu uruchomienia ProcessCleanera)
    public int getConnectionsInterruptedInRow()
    {
        synchronized (connectionsLock) {  return connectionsInterruptedInRow;  }
    }

    //metoda resetująca liczbę zaburzonych połączeń pod rząd (potrzebne do ustalenia czasu uruchomienia ProcessCleanera)
    private void resetConnectionsInterrupted()
    {
        synchronized (connectionsLock) { connectionsInterruptedInRow=0; }
    }

    //metoda inkrementująca liczbę zaburzonych połączeń pod rząd (potrzebne do ustalenia czasu uruchomienia ProcessCleanera)
    // w razie przekroczenia limitu - uruchamia ProcessCleanera
    public void incrementConnectionsInterrupted()
    {
        synchronized (connectionsLock)
        {
            connectionsInterruptedInRow++;
            uncheckedConnectionsInterrupted++;
            logger.info(Dictionary.load("TMSScrapManager.log.uncheckedConnections") + uncheckedConnectionsInterrupted);
            if(uncheckedConnectionsInterrupted==uncheckedConnectionsInterruptedLimit)
            {
                uncheckedConnectionsInterrupted=0;
                cleanProcesses();
            }
        }
    }

    //zatrzymuje pobieranie danych
    public void interruptInspectors()
    {
        setBeingModified(true);
        if(isWorking())
        {
            view.setState("stateLabel.notDownloading");
            logger.info(Dictionary.load("TMSScrapManager.log.interruptInspectors"));
            for (int i = 0; i < scrappingThreadsNumber; i++)
                if (scrapThreads[i] != null)
                    if (!scrapThreads[i].isInterrupted()) scrapThreads[i].interrupt();
            setWorking(false);
        }
        setBeingModified(false);
    }

    //uruchamia ProcessCleanera, który sprząta procesy zombie zostawione w systemie
    private void cleanProcesses()
    {
        logger.info(Dictionary.load("TMSScrapManager.log.cleaner"));
        ProcessCleaner cleaner = new ProcessCleaner();
        Thread thread = new Thread(cleaner);
        thread.start();
    }
    //resetuje pobieranie danych
    public void restartScrappers()
    {
        setBeingModified(true);
        createInspectors();
        setBeingModified(false);
    }
    /*@Override
    DEBUG
    public synchronized long dataReceived(NewTMSDataEvent newData)
    {
        resetConnectionsInterrupted();
        TMSCurrencyFrame cf = newData.getData();
        CurrencyMonitorFactory.getCurrencyMonitor(cf,this);
        ScrapTMSInspector inspector = (ScrapTMSInspector)newData.getSource();
        StringBuilder str =
                new StringBuilder("Dane od inspektora " + inspector.getId() + ", numer paczki: " + statsDataPackagesNumber +"\n");

        for(int i=0;i< CurrenciesSequence.getCurrenciesNumber();i++)
        {
            str.append(cf.getData().get(CurrenciesSequence.currencySymbols[i]))
            .append("\n");
        }
        str.append("Koniec paczki nr ")
            .append(statsDataPackagesNumber++);
        logger.info(str.toString());
        Dictionary.setLanguage("eng");
        return timePassed();
    }*/

    //wywoływane, gdy któryś z zarządców pobierania skompletuje zestaw danych
    public long dataReceived(NewTMSDataEvent newData)
    {
        synchronized(newDataLock)
        {
            resetConnectionsInterrupted();
            TMSCurrencyFrame cf = newData.getData();
            if(DataBaseController.isConnected()) CurrencyMonitorFactory.getCurrencyMonitor(cf,this);
            int y=1,x=0;
            for(int i=0;i< CurrenciesSequence.getCurrenciesNumber();i++)
            {
                ArrayList<Object> list = cf.getData().get(CurrenciesSequence.currencySymbols[i]).toObjectList();
                for(Object obj : list)
                {
                    view.setTMSValue(obj,y,x++);
                }
                y++;x=0;
            }
            scrappedSthAlready=true;
            return timePassed();
        }
    }

    //znaczenie statystyczne - do określenia co ile czasu przychodzi nowa ramka
    private long timePassed()
    {
        long between = TimePassed.compareTimeWithNow(lastDataTime);
        logger.info(Dictionary.load("TimePassed")+" " + between);
        lastDataTime=LocalTime.now();
        return between;
    }


    private void loadProperties()
    {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("ScrapManager.scrappingThreadsNumber");
        keys.add("ScrapManager.connectionsInterruptedInRow");
        keys.add("ScrapManager.uncheckedConnectionsInterrupted");
        keys.add("ScrapManager.uncheckedConnectionsInterruptedLimit");
        ArrayList<String> properties = PropertiesLoader.load(keys);
        int i=0;
        scrappingThreadsNumber=Integer.parseInt(properties.get(i++));
        connectionsInterruptedInRow=Integer.parseInt(properties.get(i++));
        uncheckedConnectionsInterrupted=Integer.parseInt(properties.get(i++));
        uncheckedConnectionsInterruptedLimit=Integer.parseInt(properties.get(i));
    }

    //reakcja na zdarzenie przekroczenia wartości strażnika przez jedną z odczytanych par walutowych
    @Override
    public void guardianValueCrossed(CurrencyValCrossedGuardianValEvent event) {
        String symbol = event.getCurrencyGuardian().getSymbol();
        CurrencyGuardiansController.delete(event.getCurrencyGuardian());
        JOptionPane.showMessageDialog(view,symbol +
                Dictionary.load("TMSScrapManager.guardianValueCrossed") + " : " + event.getCurrencyGuardian().getValue());
    }

    //reakcja na wyłączony serwer TMS
    @Override
    public synchronized void serverOff(ServerWentOffEvent event) {
        if(isServerOnline)
        {
            turnSystemDown();
            isServerOnline = false;
            logger.info(Dictionary.load("TMSScrapManager.serverOff"));
            view.setState("stateLabel.TMSOff");
            JOptionPane.showMessageDialog(view, Dictionary.load("TMSScrapManager.serverOff"));
        }
    }

    //reakcja na brak internetu
    @Override
    public synchronized void connectionInterrupted(InternetConnectionInterruptedEvent event)
    {
        logger.info(Dictionary.load("TMSScrapManager.connectionInterrupted"));
        JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("TMSScrapManager.connectionInterrupted"));
        view.setState("stateLabel.notConnected");
        turnSystemDown();
    }

    private void turnSystemDown()
    {
        if(isWorking())
        {
            interruptInspectors();
            setWorking(false);
        }
    }
    public boolean isSthScrappedAlready()
    {
        return scrappedSthAlready;
    }
    public boolean isBeingModified()
    {
        synchronized (modificationLock)
        {
            return beingModified;
        }
    }
    private void setBeingModified(boolean b)
    {
        synchronized (modificationLock)
        {
            beingModified = b;
        }
    }

    public boolean isWorking()
    {
        synchronized(isWorkingLock) { return isWorking; }
    }
    public void setWorking(boolean working)
    {
        synchronized(isWorkingLock) {  isWorking = working; }
    }
}