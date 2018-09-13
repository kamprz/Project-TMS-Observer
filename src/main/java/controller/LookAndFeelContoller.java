package controller;

import config.Dictionary;
import view.mainFrame.ApplicationMainFrame;
import view.mainFrame.GuardiansPanel;

import javax.swing.*;

public class LookAndFeelContoller
{
    private static LookAndFeelContoller singleton;
    private UIManager.LookAndFeelInfo[] availableLookAndFeels;
    public static final String[] lookAndFeelNames = {"Metal","Nimbus","Motif","Windows","WindowsClassic"};

    public static LookAndFeelContoller getInstance() {
        if(singleton==null) singleton = new LookAndFeelContoller();
        return singleton;
    }
    private LookAndFeelContoller()
    {
        availableLookAndFeels = UIManager.getInstalledLookAndFeels();
    }

    //powołuje wątek zmieniający Look and Feel aplikacji
    public void changeLaF(JFrame frame, int i)
    {
        Thread t = new Thread(() ->
        {
            try
            {
                //JList jest wrażliwy na zmianę PLAF, wyrzuca czasami wyjątek z niezrozumiałego powodu.
                if(DataBaseController.isConnected()) GuardiansPanel.getInstance().deleteList();
                UIManager.setLookAndFeel(availableLookAndFeels[i].getClassName());
                SwingUtilities.updateComponentTreeUI(frame);
                if(DataBaseController.isConnected()) GuardiansPanel.getInstance().resetList();
                Dictionary.changeLanguage();Dictionary.changeLanguage();
                //z jakiegoś powodu bez wykonania jakiejś operacji np takiej j.w. wspomniana JList się nie wyświetla,
                // zauważyłem, że po wywołaniu zmiany języka się pojawią, stad powyższa linia.
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(ApplicationMainFrame.getInstance(),
                        Dictionary.load("PLAFChangeFailure"));
            }
        });
        t.start();
    }
}
