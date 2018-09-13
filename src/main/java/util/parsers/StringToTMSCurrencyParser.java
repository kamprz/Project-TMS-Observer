package util.parsers;

import model.currencies.tms.TMSCurrency;

//parsuje danego na wejściu Stringa (pobranego przez Pythona z serwera TMS, wyciągając z niego obiekt TMSCurrency

public class StringToTMSCurrencyParser extends AParser
{
    public StringToTMSCurrencyParser() { }
    public TMSCurrency parse(String input)
    {
        String symbol = input.substring(0, 6);
        this.input = input;
        this.index = 7;
        Double bid = getDouble();
        Double ask = getDouble();
        String time = null;
        Double low = null;
        Double high = null;
        try {   //gdy serwer TMS jest wyłączony, to poniższe wartości nie są na nim umieszczone
            time = getTime();
            low = getDouble();
            high = getDouble();
        }
        catch(NumberFormatException e)
        {
            return new TMSCurrency(symbol,bid,ask,time,low,high);
        }
        return new TMSCurrency(symbol,bid,ask,time,low,high);
    }
}