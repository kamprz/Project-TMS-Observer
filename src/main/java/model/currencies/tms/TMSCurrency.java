package model.currencies.tms;

import model.currencies.CurrencyValue;

import java.util.ArrayList;

public class TMSCurrency
{
    private String symbol;
    private CurrencyValue bid;
    private CurrencyValue ask;
    private String time;
    private CurrencyValue low;
    private CurrencyValue high;

    public TMSCurrency(String symbol, Double bid, Double ask, String time, Double low, Double high)
    {
        this.symbol = symbol;
        this.bid = new CurrencyValue(bid,symbol,"bid");
        this.ask = new CurrencyValue(ask,symbol,"ask");
        this.time = time;
        this.low = new CurrencyValue(low,symbol,"low");
        this.high = new CurrencyValue(high,symbol,"high");
    }

    public CurrencyValue getTMSValue(String str)
    {
        if(str.equals("bid")) return bid;
        else if(str.equals("ask")) return ask;
        else if(str.equals("low")) return low;
        else return high;
    }

    public String toString() {
        return ""+symbol+" "+bid+" "+ask+" "+time+" "+low+" "+high;
    }
    public ArrayList<Object> toObjectList()
    {
        ArrayList<Object> result = new ArrayList<>();
        result.add(symbol);
        result.add(bid.toString());
        result.add(ask.toString());
        result.add(time);
        try{result.add(low); } catch(NullPointerException e) { result.add(" "); }
        try{result.add(high);} catch(NullPointerException e) { result.add(" "); } //else result.add(high.toString());
        return result;
    }

    //GETTERS:

    public String getSymbol() { return symbol; }

    public CurrencyValue getBid() { return bid; }

    public CurrencyValue getAsk() { return ask; }

    public String getTime() { return time; }

    public CurrencyValue getLow() { return low; }

    public CurrencyValue getHigh() { return high; }
}