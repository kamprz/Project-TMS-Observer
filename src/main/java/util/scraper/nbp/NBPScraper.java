package util.scraper.nbp;

import config.Dictionary;
import exceptions.NoInternetConnectionException;
import installation.Loggers;
import events.NewNBPDataEvent;
import events.listeners.INewNBPDataListener;
import model.currencies.nbp.NBPCurrencyFrame;
import oracle.jrockit.jfr.JFR;
import util.parsers.XMLParser;
import view.mainFrame.ApplicationMainFrame;

import javax.swing.*;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NBPScraper implements Runnable
{
    private INewNBPDataListener listening;
    private final Logger logger = Loggers.htmlScraperLogger;
    public NBPScraper(INewNBPDataListener listening)
    {
        this.listening=listening;
    }

    //pobiera i parsuje dane pobrane z NBP, po czym przekazuje je nasłuchującemu obiektowi - NBPFrame
    public void run()
    {
        try
        {
            NBPCurrencyFrame dataFrame = XMLParser.parse(HtmlScraper.getHtml());
            transferData(new NewNBPDataEvent(this,dataFrame));
        }
        catch(UnknownHostException e)
        {
            String message = Dictionary.load("noConnection") + "\n"+e.toString();
            logger.log(Level.WARNING,message);
            setWarningMessage(new NoInternetConnectionException(message));
        }
        catch (Exception e)  //was ScrapingFailureException
        {
            logger.log(Level.WARNING,e.toString());
            setWarningMessage(e);
        }
        //catch (NBPServerFailureException e) { System.out.println(e.toString()); }
        //catch (IOException e) { e.printStackTrace();}
        //catch (ConcurrentModificationException e) {/*Może się zdarzyć przy błyskawicznym wielokrotnym wywołaniu*/}
    }

    private void setWarningMessage(Exception e)
    {
        String message = Dictionary.load("NBPScraper.warnMessage") + e.getMessage();
        JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(), message);
        JFrame frame = (JFrame)listening;
        frame.dispose();
    }

    private void transferData(NewNBPDataEvent event)
    {
        listening.dataReceived(event);
    }
}
