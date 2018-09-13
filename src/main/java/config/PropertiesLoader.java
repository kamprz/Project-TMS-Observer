package config;

import events.TurnSystemOffEvent;
import exceptions.SystemFailureException;
import installation.Loggers;
import installation.ApplicationEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PropertiesLoader
{
    //metoda wczytuje listę propertiesów w odpowiedzi na listę zawartą w parametrze wejściowym
    public static ArrayList<String> load(ArrayList<String> input) {
        ArrayList<String> result = new ArrayList<>();
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("./config/config.properties");
            prop.load(inputStream);
            for (String s : input) {
                result.add(prop.getProperty(s));
            }
        } catch (IOException e) {
            systemFailure(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Loggers.propertiesLoaderLogger.info(Dictionary.load("propertiesInfo"));
                }
            }
        }
        return result;
    }

    //metoda wczytuje dany properties
    public static String load(String input) {
        String result = "";
        Properties prop = new Properties();
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream("./config/config.properties");
            prop.load(inputStream);
            result = (prop.getProperty(input));
        }
        catch (IOException e) { systemFailure(e); }
        finally
        {
            if (inputStream != null)
            {
                try { inputStream.close(); }
                catch (IOException e) { Loggers.propertiesLoaderLogger.info(Dictionary.load("propertiesInfo")); }
            }
        }
        return result;
    }

    //metoda wyłączająca system w razie problemu z odczytaniem pliku properties
    private static void systemFailure(IOException e)
    {
        String error = Dictionary.load("propertiesError");
        Loggers.propertiesLoaderLogger.log(Level.SEVERE, error + "\n" + e.getMessage());
        SystemFailureException exception = new SystemFailureException(error);
        TurnSystemOffEvent event = new TurnSystemOffEvent( exception);
        ApplicationEngine.getInstance().turnSystemOff(event);
    }
}
