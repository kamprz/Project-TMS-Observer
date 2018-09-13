package model.currencies;

public class CurrenciesSequence
{
    public static final String[] currencySymbols = {
            "EURPLN",
            "USDPLN",
            "CHFPLN",
            "GBPPLN",
            "EURUSD",
            "EURCHF",
            "GBPUSD",
            "USDCHF",
            "AUDUSD",
            "USDCAD",
            "USDJPY",
            "GBPJPY",
            "EURJPY",
            "EURGBP",
            "EURSEK",
            "EURNOK",
            "USDNOK",
            "USDSEK"
    };
    public static String getCurrencySymbol(int i)
    {
        return currencySymbols[i];
    }
    public static int getCurrenciesNumber()
    {
        return currencySymbols.length;
    }
}
