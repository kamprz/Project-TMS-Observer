package util.scraper.nbp;

import events.listeners.INewNBPDataListener;
import util.scraper.nbp.NBPScraper;

public abstract class NBPScrapManager
{
    public static void getNBPData(INewNBPDataListener listening)
    {
        NBPScraper scraper = new NBPScraper(listening);
        Thread thread = new Thread(scraper);
        thread.start();
    }
}
