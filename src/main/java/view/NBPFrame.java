package view;

import config.Dictionary;
import config.PropertiesLoader;
import events.NewNBPDataEvent;
import events.listeners.ILanguageChangedListener;
import events.listeners.INewNBPDataListener;
import model.currencies.nbp.NBPCurrency;

import javax.swing.*;
import java.awt.*;

public class NBPFrame extends JFrame implements INewNBPDataListener, ILanguageChangedListener
{
    private JTable table;
    private JLabel state;
    private JList list;
    private String[] titles = {"","",""};
    public NBPFrame()
    {
        init();
    }
    private void init()
    {
        setFrame();
        state = new JLabel(Dictionary.load("stateLabel.connecting"));
        getContentPane().add(state);
    }
    private void setFrame()
    {
        int width = Integer.parseInt(PropertiesLoader.load("NBPFrame.width"));
        int height = Integer.parseInt(PropertiesLoader.load("NBPFrame.height"));
        setSize(width,height);
        setLayout(new FlowLayout());
        setVisible(true);
    }
    private void setTitles()
    {
        getContentPane().remove(state);
        titles[0] = Dictionary.load("nbpTableCurrencies");
        titles[1] = Dictionary.load("nbpTableCode");
        titles[2] = Dictionary.load("nbpTableMid");
        for(int i=0;i<3;i++) table.setValueAt(titles[i],0,i);
    }
    private void setTable(NewNBPDataEvent newData)
    {
        int rows = newData.data.getData().size()+1;
        int cols = 3;
        table = new JTable(rows,cols);
        setTitles();
        int row=1;
        for(NBPCurrency currency : newData.data.getData())
        {
            table.setValueAt(currency.getCurrency(),row,0);
            table.setValueAt(currency.getCode(),row,1);
            table.setValueAt(currency.getMid(),row,2);
            row++;
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(157);
        table.getColumnModel().getColumn(0).setResizable(true);
        table.setEnabled(false);
        getContentPane().add(table);
        pack();
    }

    @Override
    public void dataReceived(NewNBPDataEvent newData) {
        setTable(newData);
        pack();
    }

    @Override
    public void changeLanguage() {
        setTitles();
    }
}
