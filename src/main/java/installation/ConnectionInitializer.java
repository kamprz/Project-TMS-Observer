package installation;

import config.Dictionary;
import config.PropertiesLoader;
import events.ConnectedToTMSEvent;
import events.listeners.IConnectedToTMSListener;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//klasa służąca do sprawdzenia połączenia z serwerem TMS
public class ConnectionInitializer implements Runnable
{
    private String program;
    private String returned;
    private IConnectedToTMSListener listener;
    private final Logger logger = Loggers.connectionInitializerLogger;

    private static boolean isConnecting=false;
    private final static Object isConnectingLock = new Object();
    public ConnectionInitializer(IConnectedToTMSListener listener)
    {
        this.listener=listener;
        loadProperties();
    }

    private void fireEvent(ConnectedToTMSEvent event)
    {
        listener.connected(event);
    }

    private void loadProperties()
    {
       program = PropertiesLoader.load("python.initializer");
       returned = PropertiesLoader.load("initializer.returned");
    }


    @Override
    public void run()
    {
        setIsConnecting(true);
        logger.info( Dictionary.load("ConnectionInitializer.start"));
        Boolean result = false;
        try
        {
            String s;
            Process p = Runtime.getRuntime().exec(program);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = stdInput.readLine()) != null && !result)
            {   //skrypt Python zwraca dwie wartości zależnie od tego czy uda mu się połączyć z serwerem
                if (s.equals(returned)) result = true;
            }
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,Dictionary.load("ConnectionInitializer.warn"));
            setWarningMessage();
        }
        if(result) logger.info(Dictionary.load("ConnectionInitializer.connected"));
        else logger.info(Dictionary.load("ConnectionInitializer.notConnected"));
        fireEvent(new ConnectedToTMSEvent(this,result));
        setIsConnecting(false);
    }
    private void setWarningMessage()
    {
        String message = Dictionary.load("ConnectionInitializer.warningMessage");
        JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(),message);
    }
    public static boolean getIsConnecting() { synchronized (isConnectingLock) { return isConnecting; } }

    public static void setIsConnecting(boolean b) { synchronized (isConnectingLock) { isConnecting = b; } }
}
