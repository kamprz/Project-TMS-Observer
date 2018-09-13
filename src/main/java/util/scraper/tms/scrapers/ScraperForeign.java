package util.scraper.tms.scrapers;

import config.PropertiesLoader;

public class ScraperForeign extends ScraperTMS
{
    public ScraperForeign()
    {
        loadProperties();
    }

    private void loadProperties()
    {
        program=PropertiesLoader.load("python.foreign");
    }
}
