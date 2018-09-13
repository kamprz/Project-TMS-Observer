package model.currencies.tms;

import java.time.LocalTime;
import java.util.HashMap;

public class TMSCurrencyFrame
{
    private HashMap<String,TMSCurrency> data;
    private static TMSCurrencyFrame currentTMSCurrencyFrame;
    private static final Object currentFrameLock = new Object();
    private LocalTime timeArrived;

    public TMSCurrencyFrame(HashMap<String,TMSCurrency> pln, HashMap<String,TMSCurrency> foreign)
    {
        this.data = foreign;
        timeArrived=LocalTime.now();
        for(String symbol : pln.keySet()) data.put(symbol,pln.get(symbol));
        synchronized(currentFrameLock)
        {
            currentTMSCurrencyFrame = this;
        }
    }
    public HashMap<String,TMSCurrency> getData()
    {
        return data;
    }

    public LocalTime getTimeArrived() {  return timeArrived; }

    public static TMSCurrencyFrame getCurrentFrame() {
        synchronized(currentFrameLock)
        {
            return currentTMSCurrencyFrame;
        }
    }
    public String toString()
    {
        StringBuilder str= new StringBuilder();
        for(String s : data.keySet())
        {
            str.append(data.get(s)).append("\n");
        }
        return str.toString();
    }
    public static boolean isValueGreaterThenScrapped(Double value,String symbol, boolean refersToBid)
    {
//        if(currentTMSCurrencyFrame==null) System.out.println("no current frame");
//        System.out.println("Symbol = "+symbol);
//        System.out.println("Data of " + symbol +"\n"+currentTMSCurrencyFrame.data.get(symbol));
//        if(value==null) System.out.println("value is null");
//        else System.out.println("vaule is not null");

        if(refersToBid)
        {
            Double d = currentTMSCurrencyFrame.data.get(symbol).getBid().getValue();
            return value > d;
        }
        else
        {
            Double d = currentTMSCurrencyFrame.data.get(symbol).getAsk().getValue();
            return value > d;
        }
    }
}
