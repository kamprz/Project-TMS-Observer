package util.scraper.tms.scrapers;

import config.PropertiesLoader;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TimerTMS implements Callable
{
    private Future<?> pln;
    private Future<?> foreign;
    private Future<?> walutomat;
    private ScraperTMS scraperPLN;
    private ScraperTMS scraperForeign;
    //private ScraperWalutomat scraperWalutomat;

    private int time;
    //obiekty tej klasy są powoływane przez ScrapInspectora w celu kontrolowania czasu pobierania danych przez Scrapery
    //W przypadku gdy trwa to zbyt długo wątki pobierające dane zostają zatrzymane.
    public TimerTMS(ScraperTMS scraperPLN, ScraperTMS scraperForeign , Future<?> pln, Future<?> foreign)
    {
        this.scraperForeign=scraperForeign;
        this.scraperPLN=scraperPLN;
        this.pln = pln;
        this.foreign = foreign;
        loadProperties();
    }
    /*public TimerTMS(ScraperWalutomat scraper, Future<?> result)
    {
        scraperWalutomat = scraper;
        walutomat = result;
    }*/

    public void setTime(int time) { this.time = time; }
    public Object call()
    {
        try {  Thread.sleep(time); }
        catch(InterruptedException e)
        {
            //dane pobrane, timer wyłączony
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        stopScrapping();
        return null;
    }
    private void stopScrapping()
    {
        if(scraperPLN!=null)
        {
            scraperPLN.destroyProcess();
            pln.cancel(true);
        }
        if(scraperForeign!=null) {
            scraperForeign.destroyProcess();
            foreign.cancel(true);
        }
//        if(scraperWalutomat!=null)
//        {
//            scraperWalutomat.destroyProcess();
//            walutomat.cancel(true);
//        }
    }

    private void loadProperties()
    {
        time = Integer.parseInt(PropertiesLoader.load("Timer.time"));
    }
}
