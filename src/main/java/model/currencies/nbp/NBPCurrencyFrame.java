package model.currencies.nbp;

import java.util.ArrayList;

public class NBPCurrencyFrame
{
    private ArrayList<NBPCurrency> data;
    private String date;

    public NBPCurrencyFrame(ArrayList<NBPCurrency> data, String date)
    {
        this.data = data;
        this.date=date;
    }
    public ArrayList<NBPCurrency> getData()
    {
        return data;
    }

    public String getDate() {
        return date;
    }
}
