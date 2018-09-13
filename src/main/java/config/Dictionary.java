package config;


import controller.DataBaseController;
import installation.ApplicationEngine;
import events.TurnSystemOffEvent;
import events.listeners.ILanguageChangedListener;
import exceptions.SystemFailureException;
import installation.Loggers;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

public abstract class Dictionary
{
    //aktualnie wybrany język
    private static String language = "eng";
    //lista słuchaczy na zdarzenie zmiany języka
    private static ArrayList<ILanguageChangedListener> languageChangeListeners=new ArrayList<>();
    //metoda wczytująca odpowiedni pole z danego słownika
    public static String load (String input)
    {
        String result="";
        try
        {
            Properties prop = new Properties();
            InputStream inputStream = null;
            inputStream = new FileInputStream("./config/" + language + ".lang");
            prop.load(inputStream);
            result = prop.getProperty(input);
            inputStream.close();
        }
        catch(IOException e) {
            systemFailure(e);
        }
        return result;
    }
    //metoda wyłączająca aplikację w razie błędu wczytywania danych
    private static void systemFailure(IOException e)
    {
        String error = "Błąd ładowania słownika.\nDictionary loading error.";
        Loggers.propertiesLoaderLogger.log(Level.SEVERE,error +"\n"+ e.getMessage());
        SystemFailureException exception = new SystemFailureException(error);
        TurnSystemOffEvent event = new TurnSystemOffEvent(exception);
        ApplicationEngine.getInstance().turnSystemOff(event);
    }
    //zapisanie się na zdarzenie zmiany języka
    public static void addLanguageChangeListener(ILanguageChangedListener listener)
    {
        languageChangeListeners.add(listener);
    }

    /*
     Metoda zmienia język wszystkim zapisanym słuchaczom
     */
    public synchronized static void changeLanguage()
    {
        Thread t = new Thread(() ->
        {
            try
            {
                if(language.equals("pl")) language="eng";
                else language="pl";
                for(ILanguageChangedListener listener : languageChangeListeners)
                {
                    if(listener!=null) listener.changeLanguage();
                }
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), load("LanguageChangeFailure"));
            }
        });
        t.start();
    }
}
