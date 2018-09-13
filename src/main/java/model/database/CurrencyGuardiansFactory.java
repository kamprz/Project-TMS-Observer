package model.database;

import model.currencies.tms.TMSCurrency;
import model.currencies.tms.TMSCurrencyFrame;

public abstract class CurrencyGuardiansFactory
{
    //Klasa służąca do tworzenia obiektów typu CurrencyGuardian

    //Parametry wejściowe określają dla nowego strażnika symbol pary walutowej,
    //wartość strażnika i czy strażnik odnosi się do wartości bid czy ask
    public static CurrencyGuardian createCurrencyGuardian(String currencySymbol, double value, boolean refersToBid)
    {
        TMSCurrencyFrame currentFrame = TMSCurrencyFrame.getCurrentFrame();
        TMSCurrency currency = currentFrame.getData().get(currencySymbol);
        double currVal = getCurrencyValue(currency,refersToBid);
        boolean isValueGreaterThanScrapped = getIsThisValueGreaterThanScrapped(currVal,value);
        CurrencyGuardian cg = new CurrencyGuardian(currencySymbol,value,refersToBid,isValueGreaterThanScrapped);
        return cg;
    }
    private static double getCurrencyValue(TMSCurrency currency, boolean refersToBid)
    {
        double currVal;
        if(refersToBid) currVal = currency.getBid().getValue();
        else currVal = currency.getAsk().getValue();
        return currVal;
    }
    private static boolean getIsThisValueGreaterThanScrapped(double scrappedValue, double guardianValue)
    {
        return guardianValue>scrappedValue;
    }
}
