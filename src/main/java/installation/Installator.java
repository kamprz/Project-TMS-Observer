package installation;

import config.Dictionary;
import config.PropertiesLoader;
import controller.DataBaseController;
import controller.TMSScrapManager;
import controller.CurrencyGuardiansController;
import events.ConnectedToTMSEvent;
import events.listeners.IConnectedToTMSListener;
import view.dictionary.JLabelDictionary;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;
import java.awt.*;

public class Installator implements IConnectedToTMSListener
{
    JFrame loading;
    public Installator(){}

    public void install()
    {
        if(PythonChecker.isPythonInstalled())
        {
            setLoading();
            setLoggers();
            DataBaseController.getInstance();
            loading.dispose();
            createGUI();
            startConnectionInitializer(this); //when gets Event from connectionInitializer -> install scrapper
            CurrencyGuardiansController.install();
        }
        else
        {
            closeSystem();
        }
    }

    private void closeSystem()
    {
        javax.swing.SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JOptionPane.showMessageDialog(frame,Dictionary.load("noPythonError"),"ERROR",JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        });
    }
    private void setLoading()
    {
        javax.swing.SwingUtilities.invokeLater(() ->
        {
            loading = new JFrame();
            loading.setLayout(new FlowLayout());
            loading.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            loading.add(new JLabel(new ImageIcon(PropertiesLoader.load("loading.jpg"))));
            loading.pack();
            loading.setVisible(true);
        });
    }

    private void installTMSScrapManager()
    {
        Thread t = new Thread(TMSScrapManager::getInstance);
        t.start();
    }
    private static void createGUI()
    {
        javax.swing.SwingUtilities.invokeLater(ApplicationMainFrame::getInstance);
    }

    private void setLoggers()
    {
        Loggers.getInstance();
    }

    //inicjalizuje połączenie bazy danych i wczytuje jej zawartość do wymagających tego klas
    private void connectToDataBase()
    {
        Thread t = new Thread(CurrencyGuardiansController::install);
        t.start();
    }

    //sprawdza czy da się nawiązać połączenie z serwerem TMS
    private void startConnectionInitializer(IConnectedToTMSListener listener)
    {
        ConnectionInitializer connectionInitializer = new ConnectionInitializer(listener);
        Thread thread = new Thread(connectionInitializer);
        thread.start();
    }

    //reakcja na wynik inicjalizatora połączenia
    @Override
    public void connected(ConnectedToTMSEvent event)
    {
        if(event.getIsConnected()) // jeżeli uda się nawiązać połączenie to uruchamia pobieranie danych
        {
            installTMSScrapManager();
            JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("connected"));
        }
        else
        {
            ApplicationMainFrame.getInstance().setState("stateLabel.notConnected");
            JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("noConnection"));
        }
    }
}
