package installation;

import config.PropertiesLoader;

import java.io.IOException;
//sprawdza czy użytkownik ma zainstalowanego Pythona
public class PythonChecker
{
    public static boolean isPythonInstalled()
    {
        boolean result;
        try
        {   //jeżeli się uruchomi, to znaczy, że użytkownik ma zainstalowanego Pythona
            Runtime.getRuntime().exec(PropertiesLoader.load("python.checker"));
            result = true;
        }
        catch(IOException e)
        {
            result = false;
        }
        return result;
    }
}
