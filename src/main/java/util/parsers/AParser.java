package util.parsers;

public abstract class AParser
{
    String input;
    int index;
    //od danego miejsca Stringa wskazanego przez index wyciąga najbliższą wartość typu double
    double getDouble()
    {
        while(index < input.length() && !isDigit(input.charAt(index))) index++;
        int flag=index;
        while(index < input.length() && isDouble(input.charAt(index))) index++;
        return Double.parseDouble(input.substring(flag,index));
    }

    //od danego miejsca Stringa wskazanego przez index wyciąga najbliższą wartość reprezentującą datę w formacie xx:xx:xx
    String getTime()
    {
        while(!isDigit(input.charAt(index))) index++;
        int flag=index;
        while(isDate(input.charAt(index))) index++;
        return input.substring(flag,index);
    }

    //sprawdza czy char przekazany jako parametr jest cyfrą
    boolean isDigit(char c)
    {
        boolean result = false;
        if(c>='0' && c<='9') result = true;
        return result;
    }

    //sprawdza czy char podany jako parametr jest częścią double'a
    boolean isDouble(char c)
    {
        boolean result = false;
        if((c>='0' && c<='9') || c=='.' || c==',') result = true;
        return result;
    }
    //sprawdza czy char podany jako parametr jest częścią Daty w formacie xx:xx:xx, gdzie x=cyfra
    boolean isDate(char c)
    {
        boolean result = false;
        if((c>='0'&&c<='9') || c==':') result = true;
        return result;
    }
}
