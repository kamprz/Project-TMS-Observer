package view.mainFrame;

import config.Dictionary;
import view.ActionGetNBP;
import controller.LookAndFeelContoller;
import view.dictionary.JMenuDictionary;
import view.dictionary.JMenuItemDictionary;
import javax.swing.*;

class AppMenu extends JMenuBar
{
    private JMenuDictionary view;
    private JFrame parent;
    private ActionGetNBP action;
    AppMenu(JFrame parent, ActionGetNBP action)
    {
        this.parent=parent;
        this.action = action;
        init();
    }
    private void init()
    {
        JMenuDictionary file = new JMenuDictionary("file");
        setView();
        JMenuItem nbp = new JMenuItem(action);
        add(file); add(view); add(nbp);
    }
    private void setView()
    {
        view = new JMenuDictionary("view");
        setChangeLang();
        setChangePlaf();
    }
    private void setChangeLang()
    {
        JMenuItemDictionary changeLanguage = new JMenuItemDictionary("changeLanguage");
        changeLanguage.addActionListener(e -> Dictionary.changeLanguage());
        view.add(changeLanguage);
    }
    private void setChangePlaf()
    {
        JMenuDictionary changePlaf = new JMenuDictionary("changePlaf");
        JMenuItem[] buttons = new JMenuItem[4];
        int plaf;
        for(int i=0;i<3;i++)
        {
            plaf=i;
            if(i>0)plaf+=2;  //Wybieram 3 spośród 5 dostępnych LaF
            buttons[i] = new JMenuItem(LookAndFeelContoller.lookAndFeelNames[plaf]);
            changePlaf.add(buttons[i]);
            final int laf = plaf;
            buttons[i].addActionListener(e -> LookAndFeelContoller.getInstance().changeLaF(parent,laf));
        }
        view.add(changePlaf);
    }
}
