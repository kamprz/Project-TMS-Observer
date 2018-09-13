package model.currencies;

import model.currencies.tms.TMSCurrencyFrame;

public class CurrencyValue
{
    private Double value;
    private ValuesDisplayColor color;

    public CurrencyValue(Double value, String symbol, String whichTMSVal) {
        this.value = value;
        setColor(symbol, whichTMSVal);
    }
    public enum ValuesDisplayColor
    {
        CZERWONY,
        ZIELONY,
        CZARNY;
    }

    private void setColor(String symbol,String whichTMSVal)
    {
        if(TMSCurrencyFrame.getCurrentFrame()==null) color = ValuesDisplayColor.CZARNY;
        else
        {
            Double previewValue = TMSCurrencyFrame
                    .getCurrentFrame()
                    .getData()
                    .get(symbol)
                    .getTMSValue(whichTMSVal)
                    .getValue();
            if(value > previewValue) color = ValuesDisplayColor.ZIELONY;
            else if(value < previewValue) color = ValuesDisplayColor.CZERWONY;
            else color = ValuesDisplayColor.CZARNY;
        }
    }

    @Override
    public String toString(){return (value==null)? "":value.toString();}
//    public String toString() {
//        String strColor;
//        if(color==ValuesDisplayColor.CZERWONY) strColor = "R";
//        else if(color==ValuesDisplayColor.CZARNY) strColor = "B";
//        else strColor="G";
//        return value+strColor;
//    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public ValuesDisplayColor getColor() {
        return color;
    }

}
