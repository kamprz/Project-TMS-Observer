package view.guardianFrames;

import config.Dictionary;
import config.PropertiesLoader;
import controller.CurrencyGuardiansController;
import events.listeners.ILanguageChangedListener;
import events.listeners.ISelectionChangedListener;
import model.currencies.CurrenciesSequence;
import view.dictionary.JButtonDictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Okno umożlwiające dobór wyświetlanych Guardianów w JList w GuardiansPanelu  poprzez kryteria przynależności
//do danego symbolu określającego rodzaj pary walutowej
public class GuardiansSelectFrame extends JFrame implements ILanguageChangedListener,ItemListener
{
    private String dictionary;
    private JPanel panel;
    private JCheckBox[] checkboxes;
    private JButton buttonOK;
    private JButtonDictionary buttonCancel;
    private JButtonDictionary buttonSelectAll;
    private JButtonDictionary buttonDisselectAll;


    public GuardiansSelectFrame()
    {
        javax.swing.SwingUtilities.invokeLater(() -> {
            dictionary = "GuardiansSelectFrame.title";
            init();
        });

    }

    private void init()
    {
        setFrame();
        setPanel();
        setCheckboxes();
        setButtons();
        addContent();
        pack();
    }

    private void setFrame()
    {
        Dictionary.addLanguageChangeListener(this);
        setTitle(Dictionary.load(dictionary));
        setResizable(true);
        setLayout(new FlowLayout());
        //ustawienie w oknie reakcji na próbę jego zamknięcia
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {   @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent)
        {
            CurrencyGuardiansController.getInstance().clearChanged();
            dispose();
        }
        });
        setVisible(true);
    }
    private void setPanel()
    {
        panel = new JPanel();
        int rows = Integer.parseInt(PropertiesLoader.load("GuardiansFrame.Radio.GridRows"));
        int cols = Integer.parseInt(PropertiesLoader.load("GuardiansFrame.Radio.GridCols"));
        panel.setLayout(new GridLayout(rows,cols));
        panel.setVisible(true);
    }
    private void setCheckboxes()
    {
        checkboxes = new JCheckBox[CurrenciesSequence.getCurrenciesNumber()];
        for(int i=0;i< CurrenciesSequence.getCurrenciesNumber();i++)
        {
            String symbol = CurrenciesSequence.getCurrencySymbol(i);
            checkboxes[i]= new JCheckBox(symbol);
            checkboxes[i].setSelected(CurrencyGuardiansController.getInstance().isSelected(symbol));
            panel.add(checkboxes[i]);
            checkboxes[i].addItemListener(this);
        }
    }

    //ustawienie przycisków i reakcji na nie (które są realizowane przez warstwę kontrolera w CurrencyGuardiansController
    private void setButtons()
    {
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(e -> {
            CurrencyGuardiansController.getInstance().okClicked();
            dispose();
        });
        buttonCancel = new JButtonDictionary("cancel");
        buttonCancel.addActionListener(e -> {
            CurrencyGuardiansController.getInstance().cancelClicked();
            dispose();
        });
        buttonSelectAll = new JButtonDictionary("selectAll");
        buttonSelectAll.addActionListener(e -> {
            for(int i=0;i< CurrenciesSequence.getCurrenciesNumber();i++) checkboxes[i].setSelected(true);
        });
        buttonDisselectAll = new JButtonDictionary("disselectAll");
        buttonDisselectAll.addActionListener(e -> {
            for(int i=0;i< CurrenciesSequence.getCurrenciesNumber();i++) checkboxes[i].setSelected(false);
        });
    }
    private void addContent()
    {
        getContentPane().add(panel);
        getContentPane().add(buttonOK);
        getContentPane().add(buttonCancel);
        getContentPane().add(buttonSelectAll);
        getContentPane().add(buttonDisselectAll);
    }

    @Override
    public void changeLanguage() {
        setTitle(Dictionary.load(dictionary));
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CurrencyGuardiansController.getInstance().selectionChanged(e);
    }
}