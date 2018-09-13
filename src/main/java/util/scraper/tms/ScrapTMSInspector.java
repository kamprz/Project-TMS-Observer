package util.scraper.tms;

import config.Dictionary;
import controller.TMSScrapManager;
import events.ServerWentOffEvent;
import events.listeners.IInternetConnectionInterruptedListener;
import events.listeners.IServerWentOfflineListener;
import installation.Loggers;
import model.currencies.CurrenciesSequence;
import model.currencies.tms.TMSCurrency;
import model.currencies.tms.TMSCurrencyFrame;
import events.InternetConnectionInterruptedEvent;
import events.NewTMSDataEvent;
import exceptions.NoInternetConnectionException;
import exceptions.ServerIsOfflineException;
import util.scraper.tms.scrapers.ScraperTMS;
import util.scraper.ScrapersFactory;
import util.scraper.tms.scrapers.TimerTMS;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScrapTMSInspector implements Runnable
{
    private HashMap<String,TMSCurrency> pln;
    private HashMap<String,TMSCurrency> foreign;
    private Future<HashMap<String,TMSCurrency>> resultPLN;
    private Future<HashMap<String,TMSCurrency>> resultForeign;
    private Future<HashMap<String,TMSCurrency>> resultTimer;
    private Logger logger;
    private int estimatedPackageArrivalPeriod = 4;
    private static int staticId = 1;
    private int id;
    private boolean isWorking = true;
    private ExecutorService threads;
    private IInternetConnectionInterruptedListener internetConnectionInterruptedListener;
    private IServerWentOfflineListener serverWentOfflineListener;

    public ScrapTMSInspector()
    {
        this.id=staticId++;
        logger = Loggers.scrapInspectorLogger;
        threads = Executors.newFixedThreadPool(3);
    }

    //inicjalizuje wątki do wykonania poszczególnych ról:
    //pobrania danych ze strony pln, ze strony foreign oraz ustawia timer
    private void threadsSetup()
    {
        ScraperTMS scraperPLN = (ScraperTMS)ScrapersFactory.createScraper("ScraperPLN");
        resultPLN = threads.submit(scraperPLN);

        ScraperTMS scraperForeign = (ScraperTMS)ScrapersFactory.createScraper("ScraperForeign");
        resultForeign = threads.submit(scraperForeign);

        TimerTMS timer = new TimerTMS(scraperPLN, scraperForeign, resultPLN, resultForeign);
        resultTimer = threads.submit(timer);
    }

    public void run()
    {
        try {
            while(isWorking)
            {
                threadsSetup();
                try
                {
                    //czeka na pobranie danych od obu wątków
                    pln = resultPLN.get();
                    foreign = resultForeign.get();
                    //jeżeli otrzymał dane od obu - usuwa timer
                    resultTimer.cancel(true);
                    //sprawdza czy pobrano dane
                    checkIfDataIsValid();
                    //przekazuje dane do TMSScrapManagera
                    TMSCurrencyFrame dataReceived = new TMSCurrencyFrame(pln, foreign);
                    long timeFromLatestData = transferData(dataReceived);
                    //sprawdza czy serwer jest czynny
                    if(dataReceived.getData().get(CurrenciesSequence.currencySymbols[0]).getHigh()==null)
                    {
                        throw new ServerIsOfflineException();
                    }
                    //jeżeli pakiet przyszedł tuż po poprzednim to usypia na chwilę wątek, aby dane przychodziły co jakiś czas
                    //a nie na raz od wszystkich wątków
                    if(timeFromLatestData < estimatedPackageArrivalPeriod) goToSleep(timeFromLatestData);
                }
                catch (ExecutionException e)
                {   //when parser throws StringIndexOutOfBoundsException / ScraperTMS -> IOException
                    throw new ServerIsOfflineException();
                }
                catch (CancellationException e)
                {   //minął czas i Timer go ubił
                    logger.info(Dictionary.load("ScrapTMSInspector.log.CancellationExceptionMessage") +
                            TMSScrapManager.getInstance().getConnectionsInterruptedInRow() + "). Inspector nr " + id + " reset.");
                    TMSScrapManager.getInstance().incrementConnectionsInterrupted();
                }
            }
        }
        catch (InterruptedException e)
        {
            logger.info(Dictionary.load("ScrapTMSInspector.log.InterruptedExceptionMessage") +id+")");
        }
        catch(ServerIsOfflineException e)
        {
            logger.log(Level.WARNING,Dictionary.load("ScrapTMSInspector.log.ServerIsOfflineExceptionMessage"));
            fireServerWentOffEvent();
        }
        catch (NoInternetConnectionException e)
        {
            logger.log(Level.WARNING,Dictionary.load("ScrapTMSInspector.log.NoInternetConnectionExceptionMessage")
                    + TMSScrapManager.getInstance().getConnectionsInterruptedInRow() + ").");
            TMSScrapManager.getInstance().incrementConnectionsInterrupted();
            fireInternetConnectionInterruptedEvent();
        }
    }
    private void checkIfDataIsValid() throws NoInternetConnectionException
    {
        if(pln.size()==0 || foreign.size()==0) throw new NoInternetConnectionException();
    }

    private long transferData(TMSCurrencyFrame data)
    {
        NewTMSDataEvent event= new NewTMSDataEvent(this,data);
        return TMSScrapManager.getInstance().dataReceived(event);
    }

    private void fireInternetConnectionInterruptedEvent()
    {
        InternetConnectionInterruptedEvent event1 = new InternetConnectionInterruptedEvent(this);
        internetConnectionInterruptedListener.connectionInterrupted(event1);
    }
    private void fireServerWentOffEvent()
    {
        ServerWentOffEvent event2 = new ServerWentOffEvent(this);
        serverWentOfflineListener.serverOff(event2);
    }
    public void addInternetConnInterruptedListenersList(IInternetConnectionInterruptedListener listener) {
        internetConnectionInterruptedListener = listener;
    }
    public void addServerWentOffListeners(IServerWentOfflineListener listener) {
        serverWentOfflineListener = listener;
    }
    private void goToSleep(long time) throws InterruptedException
    {
        logger.info("Inspector "+id+" idzie spac na " + (estimatedPackageArrivalPeriod -time) +" sekund.");
        Thread.sleep(1000*(estimatedPackageArrivalPeriod -time));
    }
}
