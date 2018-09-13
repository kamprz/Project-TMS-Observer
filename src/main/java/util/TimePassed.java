package util;

import java.time.Duration;
import java.time.LocalTime;

//klasa pomocniczna do obliczenia różnicy w czasie między podanym pomiarem a chwilą obecną
public class TimePassed
{
    public static long compareTimeWithNow(LocalTime compare)
    {
        long between;
        if(compare == null)
        {
            between = 0;
        }
        else
        {
            LocalTime now = LocalTime.now();
            between = Duration.between(compare,now).getSeconds();
        }
        return between;
    }
}
