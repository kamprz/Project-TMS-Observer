package view;

import config.Dictionary;
import events.listeners.ILanguageChangedListener;
import util.scraper.nbp.NBPScrapManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

//Obiekt udostpęnia możliwość
public class ActionGetNBP extends AbstractAction implements ILanguageChangedListener
{
    private String dictionary;
    public ActionGetNBP(String str)
    {
        super(str);
        dictionary = str;
        putValue(Action.NAME,Dictionary.load(dictionary));
        Dictionary.addLanguageChangeListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        javax.swing.SwingUtilities.invokeLater(() ->
        {
            NBPFrame frame = new NBPFrame();
            NBPScrapManager.getNBPData(frame);
        });
    }
    @Override
    public void changeLanguage() {
        putValue(Action.NAME, Dictionary.load(dictionary));
    }
}
