package util.scraper.tms.scrapers;

import config.PropertiesLoader;
public class ScraperPLN extends ScraperTMS
{
    public ScraperPLN()
    {
        loadProperties();
    }
    private void loadProperties()
    {
        program=PropertiesLoader.load("python.PLN");
    }
}
