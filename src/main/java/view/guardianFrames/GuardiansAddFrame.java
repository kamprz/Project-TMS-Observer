package view.guardianFrames;

import config.Dictionary;
import config.PropertiesLoader;
import controller.CurrencyGuardiansController;
import events.listeners.ILanguageChangedListener;
import model.currencies.CurrenciesSequence;
import model.database.CurrencyGuardian;
import model.database.CurrencyGuardiansFactory;
import model.guardiansFramesSetting.GuardianAddFrameSettings;
import view.dictionary.JButtonDictionary;
import view.dictionary.JLabelDictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

//okno umożliwiające użytkownikowi dodanie nowego CurrencyGuardiana
public class GuardiansAddFrame extends JFrame implements ILanguageChangedListener, ActionListener
{
    protected String dictionary;
    String selectedSymbol;
    JTextField textField;
    protected Double value;
    JLabelDictionary wrongFormatLabel;
    boolean selectedRefersToBid=true;
    JRadioButton bid;
    JRadioButton ask;
    HashMap<String,JRadioButton> radioButtons;
    JButton buttonOK;
    JButtonDictionary buttonCancel;

    public GuardiansAddFrame()
    {
        dictionary="GuardiansAddFrame.title";
        Dictionary.addLanguageChangeListener(this);
        init();
    }

    void init()
    {
        setRadioButtons();
        setValuePanel();
        setBidAskPanel();
        setButtons();
        readSettings();
        setFrame();
    }

    void setFrame()
    {
        setLayout(new FlowLayout());
        int width = Integer.parseInt(PropertiesLoader.load("GuardiansAddFrame.width"));
        int height = Integer.parseInt(PropertiesLoader.load("GuardiansAddFrame.height"));
        setSize(width,height);
        setResizable(false);
        //umożliwienie przechwycenia operacji zamknięcia okna
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {   @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) { dispose(); }
        });
        setVisible(true);
    }

    private void setRadioButtons()
    {
        int rows = Integer.parseInt(PropertiesLoader.load("GuardiansFrame.Radio.GridRows"));
        int cols = Integer.parseInt(PropertiesLoader.load("GuardiansFrame.Radio.GridCols"));
        JPanel radioPanel = new JPanel(new GridLayout(rows,cols));
        ButtonGroup group = new ButtonGroup();
        radioButtons = new HashMap<>();
        //ustawienie mapy radioButtonów<String: symbol,RadioButton>
        for(int i = 0; i< CurrenciesSequence.getCurrenciesNumber(); i++)
        {
            String symbol = CurrenciesSequence.getCurrencySymbol(i);
            JRadioButton button = new JRadioButton(symbol);
            button.setActionCommand(symbol);
            group.add(button);
            button.addActionListener(e -> selectedSymbol = e.getActionCommand());
            radioPanel.add(button);
            radioButtons.put(symbol,button);
        }
        selectedSymbol=CurrenciesSequence.getCurrencySymbol(0);
        getContentPane().add(radioPanel);
    }

    //panel do wstawiania wartości Guardiana
    void setValuePanel()
    {
        JPanel valuePanel = new JPanel();
        JLabel label = new JLabelDictionary("giveValue");
        wrongFormatLabel = new JLabelDictionary(" ");
        valuePanel.add(label);
        textField = new JTextField();
        int width = Integer.parseInt(PropertiesLoader.load("GuardiansAddFrame.TextFieldWidth"));
        int height = Integer.parseInt(PropertiesLoader.load("GuardiansAddFrame.TextFieldHeight"));
        textField.setPreferredSize(new Dimension(width,height));
        valuePanel.add(textField);
        getContentPane().add(valuePanel);
    }

    //RadioButton do określenia czy Guardian dotyczy wartości bid czy ask
    protected void setBidAskPanel()
    {
        JPanel panel = new JPanel();
        ButtonGroup group = new ButtonGroup();
        {
            bid = new JRadioButton("Bid");
            bid.setActionCommand("Bid");
            group.add(bid);
            bid.addActionListener(e -> selectedRefersToBid=true);
            bid.setSelected(true);
            panel.add(bid);
        }
        {
            ask = new JRadioButton("Ask");
            ask.setActionCommand("Ask");
            group.add(ask);
            ask.addActionListener(e -> selectedRefersToBid=false);
            panel.add(ask);
        }
        getContentPane().add(panel);
    }
    //ustawienie przycisków: OK i Cancel
    protected void setButtons()
    {
        JPanel panel = new JPanel(new FlowLayout());
        {
            buttonOK = new JButton("OK");
            buttonOK.addActionListener(this);
            panel.add(buttonOK);
        }
        {
            buttonCancel = new JButtonDictionary("cancel");
            Dictionary.addLanguageChangeListener(buttonCancel);
            buttonCancel.addActionListener(this);
            panel.add(buttonCancel);
        }
        panel.add(wrongFormatLabel);
        getContentPane().add(panel);
    }

    @Override
    public void changeLanguage()
    {
        setTitle(Dictionary.load("GuardiansAddFrame.title"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(buttonCancel))
        {
            setSettings();
            dispose();
        }
        else if(e.getSource().equals(buttonOK))
        {
            try
            {
                CurrencyGuardian cg = getCurrencyGuardian();
                wrongFormatLabel.setText(Dictionary.load(" "));
                CurrencyGuardiansController.add(cg);
                setSettings();
                dispose();
            }
            catch (NumberFormatException e1) { wrongFormatLabel.setText(Dictionary.load("wrongFormat")); }
        }
    }
    protected CurrencyGuardian getCurrencyGuardian() throws NumberFormatException
    {
        try{ value = Double.parseDouble(textField.getText()); }
        catch(NumberFormatException e1) { value = Double.parseDouble(textField.getText().replace(',','.'));}
        return CurrencyGuardiansFactory.createCurrencyGuardian(selectedSymbol, value, selectedRefersToBid);
    }

    void displaySelectedValues()
    {
        radioButtons.get(selectedSymbol).setSelected(true);
        if(selectedRefersToBid)     { ask.setSelected(false); bid.setSelected(true); }
        else                        { ask.setSelected(true); bid.setSelected(false); }
        textField.setText(value.toString());
    }

    private void setSettings()
    {
        GuardianAddFrameSettings.getInstance().setAddSettings(selectedSymbol,value,selectedRefersToBid);
    }
    private void readSettings()
    {
        selectedSymbol = GuardianAddFrameSettings.getInstance().getSymbol();
        value = GuardianAddFrameSettings.getInstance().getValue();
        selectedRefersToBid = GuardianAddFrameSettings.getInstance().isRefersToBid();
        radioButtons.get(selectedSymbol).setSelected(true);
        displaySelectedValues();
    }
}
