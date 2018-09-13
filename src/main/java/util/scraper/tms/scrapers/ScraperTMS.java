package util.scraper.tms.scrapers;

import model.currencies.tms.TMSCurrency;
import util.scraper.IScraper;
import util.parsers.StringToTMSCurrencyParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.Callable;

public abstract class ScraperTMS implements Callable,IScraper
{
    protected String program;
    protected HashMap<String,TMSCurrency> result = new HashMap<>();
    protected Process process;

    //pobiera i parsuje dane z serwera TMS, umieszczając je w mapie <symbol,obiekt pary walutowej>
    public void scrap() throws IOException
    {
        String s;
        process = Runtime.getRuntime().exec(program);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringToTMSCurrencyParser parser = new StringToTMSCurrencyParser();
        while ((s = stdInput.readLine()) != null)
        {
            if(s.length()>10)
            {
                TMSCurrency c = parser.parse(s);
                result.put(c.getSymbol(),c);
            }
        }
        process.destroy();
    }
    public void destroyProcess()
    {
        process.destroy();
    }

    @Override
    public Object call() throws Exception {
        scrap();
        return result;
    }
}