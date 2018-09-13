package controller;

import config.Dictionary;
import installation.ConnectionInitializer;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;

public class MainFrameController
{
    //reakcja na wciśnięcie przycisku resetującego pobieranie danych w głównym oknie aplikacji
    public static void resetScrapers()
    {
        Thread t = new Thread(() ->
        {
            if(ConnectionInitializer.getIsConnecting())
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("ConnectionInitializer.isConnecting"));
            }
            else if(TMSScrapManager.getInstance().isBeingModified())
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("TMSScrapManager.isWorkingMessage"));
            }
            else if(TMSScrapManager.getInstance().isWorking())
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("TMSScrapManager.isWorkingMessage"));
            }
            else
            {
                TMSScrapManager.getInstance().restartScrappers();
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("TMSScrapManager.restart"));
            }
        });
        t.start();
    }

    //reakcja na wciśnięcie przycisku zatrzymującego pobieranie danych w głównym oknie aplikacji
    public static void stopScraping()
    {
        Thread t = new Thread(() ->
        {
            if(TMSScrapManager.getInstance().isBeingModified())
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("scraperIsBeingModified"));
            }
            else if(!TMSScrapManager.getInstance().isWorking())
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("scraperIsNotWorking"));
            }
            else
            {
                TMSScrapManager.getInstance().interruptInspectors();
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), Dictionary.load("scrappingStopped"));
            }
        });
        t.start();
    }
}
