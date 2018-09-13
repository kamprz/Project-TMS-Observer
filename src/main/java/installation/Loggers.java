package installation;

import config.Dictionary;
import config.PropertiesLoader;
import controller.TMSScrapManager;
import events.TurnSystemOffEvent;
import exceptions.SystemFailureException;
import util.scraper.nbp.HtmlScraper;
import util.scraper.tms.ProcessCleaner;
import util.scraper.tms.ScrapTMSInspector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Loggers
{
    //klasa zapewniająća dostęp do wszystkich loggerów i inicjalizująca je

    public static final Logger propertiesLoaderLogger = Logger.getLogger(PropertiesLoader.class.getName());
    public static final  Logger scrapInspectorLogger = Logger.getLogger(ScrapTMSInspector.class.getName());
    public static final Logger processCleanerLogger = Logger.getLogger(ProcessCleaner.class.getName());
    public static final Logger htmlScraperLogger = Logger.getLogger(HtmlScraper.class.getName());
    public static final Logger TMSScrapManagerLogger = Logger.getLogger(TMSScrapManager.class.getName());
    public static final Logger connectionInitializerLogger = Logger.getLogger(ConnectionInitializer.class.getName());
    private static String connectionInitializerLoggerPath;
    private static String propertiesLoaderLoggerPath;
    private static String scrapInspectorLoggerPath;
    private static String processCleanerLoggerPath;
    private static String htmlScraperLoggerPath;
    private static String TMSScrapManagerLoggerPath;
    private static Loggers singleton;
    private static int limit;
    private static int count;
    public static Loggers getInstance()
    {
        if(singleton==null) singleton = new Loggers();
        return singleton;
    }
    private Loggers()
    {
        loadProperties();
        setLogger(propertiesLoaderLogger,propertiesLoaderLoggerPath);
        setLogger(scrapInspectorLogger,scrapInspectorLoggerPath);
        setLogger(processCleanerLogger,processCleanerLoggerPath);
        setLogger(htmlScraperLogger,htmlScraperLoggerPath);
        setLogger(TMSScrapManagerLogger,TMSScrapManagerLoggerPath);
        setLogger(connectionInitializerLogger,connectionInitializerLoggerPath);
    }

    private void setLogger(Logger logger, String file)
    {
        try
        {
            FileHandler fh = new FileHandler(file,limit,count);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }
        catch (IOException e)
        {
            String error = Dictionary.load("Logger.error");
            SystemFailureException exception = new SystemFailureException(error);
            turnSystemOff(exception);
        }
    }
    private void turnSystemOff(SystemFailureException e)
    {
        TurnSystemOffEvent event = new TurnSystemOffEvent(this,e);
        ApplicationEngine.getInstance().turnSystemOff(event);
    }

    private static void loadProperties()
    {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("Logger.PropertiesLoader");
        keys.add("Logger.ProcessCleaner");
        keys.add("Logger.ScrapInspector");
        keys.add("Logger.HtmlScraper");
        keys.add("Logger.TMSScrapManager");
        keys.add("Logger.ConnectionInitializer");
        keys.add("Logger.limit");
        keys.add("Logger.count");
        int i=0;
        ArrayList<String> properties = PropertiesLoader.load(keys);
        propertiesLoaderLoggerPath = properties.get(i++);
        processCleanerLoggerPath = properties.get(i++);
        scrapInspectorLoggerPath = properties.get(i++);
        htmlScraperLoggerPath = properties.get(i++);
        TMSScrapManagerLoggerPath = properties.get(i++);
        connectionInitializerLoggerPath = properties.get(i++);
        limit = Integer.parseInt(properties.get(i++));
        count = Integer.parseInt(properties.get(i));
    }
}
