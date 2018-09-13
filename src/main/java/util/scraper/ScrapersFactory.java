package util.scraper;

import util.scraper.tms.scrapers.ScraperForeign;
import util.scraper.tms.scrapers.ScraperPLN;

public abstract class ScrapersFactory
{
    public static IScraper createScraper(String scraperClassName)
    {
        IScraper scraper=null;
        if(scraperClassName.equals("ScraperPLN")) scraper = new ScraperPLN();
        else if(scraperClassName.equals("ScraperForeign")) scraper = new ScraperForeign();
        return scraper;
    }
}
