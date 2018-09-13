package installation;

import controller.DataBaseController;
import controller.TMSScrapManager;
import events.*;
import events.listeners.*;
import util.scraper.tms.ProcessCleaner;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;

public class ApplicationEngine implements ITurnSystemOffListener
{
    private static ApplicationEngine singleton;
    private ApplicationEngine()
    {
        Installator installator = new Installator();
        installator.install();
    }
    public static ApplicationEngine getInstance()
    {
        if(singleton==null) singleton = new ApplicationEngine();
        return singleton;
    }

    private void cleanProcesses()
    {
        ProcessCleaner cleaner = new ProcessCleaner(true);
        cleaner.run();
    }

    @Override
    public void turnSystemOff(TurnSystemOffEvent e)
    {
        TMSScrapManager.getInstance().interruptInspectors();
        cleanProcesses();
        if(DataBaseController.isConnected()) DataBaseController.getInstance().closeConnection();
        JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), e.getException().getMessage());
    }
    public void turnSystemOff()
    {
        TMSScrapManager.getInstance().interruptInspectors();
        cleanProcesses();
        if(DataBaseController.isConnected()) DataBaseController.getInstance().closeConnection();
    }
}
