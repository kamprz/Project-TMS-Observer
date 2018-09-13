package model.guardiansFramesSetting;

import model.currencies.CurrenciesSequence;

public class GuardianAddFrameSettings
{
    private static GuardianAddFrameSettings addFrameSettings;
    private String symbol;
    private Double value;
    private boolean refersToBid;

    public static GuardianAddFrameSettings getInstance()
    {
        if(addFrameSettings ==null) addFrameSettings = new GuardianAddFrameSettings();
        return addFrameSettings;
    }

    private GuardianAddFrameSettings()
    {
        symbol = CurrenciesSequence.getCurrencySymbol(0);
        value=0.0;
        refersToBid=true;
    }

    public void setAddSettings(String symbol, Double value, boolean refersToBid)
    {
        this.refersToBid=refersToBid;
        this.symbol=symbol;
        this.value=value;
        System.out.println("Set: "+ this.symbol+" "+ this.value + " " + this.refersToBid);
    }

    public String getSymbol() { return symbol; }

    public Double getValue() { return value; }

    public boolean isRefersToBid() { return refersToBid; }
}
