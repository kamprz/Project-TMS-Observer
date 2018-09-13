package util.parsers;

import config.Dictionary;
import installation.Loggers;
import model.currencies.nbp.NBPCurrency;
import exceptions.NBPServerFailureException;
import exceptions.ScrapingFailureException;
import model.currencies.nbp.NBPCurrencyFrame;

import java.util.ArrayList;
import java.util.logging.Logger;

public class XMLParser
{
    private static ArrayList<NBPCurrency> currencies = new ArrayList<>();
    private static ArrayList<String> rates = new ArrayList<>();
    private static String date;
    private static final Logger logger = Loggers.htmlScraperLogger;

    //parsuje pobrany plik xml (dany w parametrze wejściowym) do ramki danych NBPCurrencyFrame
    public synchronized static NBPCurrencyFrame parse(String xml)
            throws ScrapingFailureException, NBPServerFailureException
    {
        currencies.clear();
        rates.clear();
        if (xml == null || xml.length() < 2)
            throw new ScrapingFailureException(Dictionary.load("ScrapingFailureExceptionMessage"));
        ArrayList<String> result = new ArrayList<>();
        try
        {
            date = getSelector(xml, "EffectiveDate");
            getRatesList(xml, "Rate");
            for (String rate : rates)
            {
                String curr = getSelector(rate, "Currency");
                String code = getSelector(rate, "Code");
                String mid = getSelector(rate, "Mid");
                NBPCurrency currency = new NBPCurrency(curr, code, mid);
                currencies.add(0, currency);
            }
        }
        catch(StringIndexOutOfBoundsException e)
        {
            throw new NBPServerFailureException(Dictionary.load("NBPServerFailureExceptionMessage"));
        }
        if (currencies.size() < 5)
            throw new NBPServerFailureException(Dictionary.load("NBPServerFailureExceptionMessage"));
        return new NBPCurrencyFrame(currencies,date);
    }

    //wyciąga z xml'a listę substringów znajdujących się między znacznikami podanymi w drugim parametrze
    private static void getRatesList(String xml, String selector)
    {
        int last;
        int first;
        while(xml.contains("</"+selector+">"))
        {
            last = xml.lastIndexOf("</"+selector+">");
            first = xml.lastIndexOf("<"+selector+">") + selector.length()+2;
            String substring = xml.substring(first,last);
            xml = xml.substring(0,first-selector.length()+3);
            rates.add(substring);
        }
    }

    //wyciąga ze stringa ukrytego w znacznikach zawartość
    public static String getSelector(String str, String selector)
    {
        int last = str.lastIndexOf("</"+selector+">");
        int first = str.lastIndexOf("<"+selector+">") + selector.length()+2;
        return str.substring(first,last);
    }
}
