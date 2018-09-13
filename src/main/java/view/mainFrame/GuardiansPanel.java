package view.mainFrame;

import config.Dictionary;
import config.PropertiesLoader;
import controller.CurrencyGuardiansController;
import controller.DataBaseController;
import controller.TMSScrapManager;
import events.SelectionChangedEvent;
import events.listeners.ISelectionChangedListener;
import model.database.CurrencyGuardian;
import view.dictionary.JLabelDictionary;
import view.guardianFrames.GuardiansAddFrame;
import view.guardianFrames.GuardiansEditFrame;
import view.guardianFrames.GuardiansSelectFrame;
import view.dictionary.JButtonDictionary;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class GuardiansPanel extends JPanel implements ISelectionChangedListener
{
    private JFrame frame;
    private JPanel upperPanel;
    private JScrollPane lowerPanel;
    private JList<CurrencyGuardian> list;
    private DefaultListModel<CurrencyGuardian> listModel;
    private CurrencyGuardian selected;
    private static GuardiansPanel singleton;
    public static GuardiansPanel getInstance()
    {
        if(singleton==null) singleton = new GuardiansPanel();
        return singleton;
    }
    private GuardiansPanel()
    {
        setLayout(new FlowLayout());
        init();
    }
    private void init()
    {
        if(DataBaseController.isConnected())
        {
            setUpperPanel();
            setLowerPanel();
            CurrencyGuardiansController.getInstance().setListener(this);
        }
        else setPanelIfNoConnectionToDatabase();
    }
    private void setPanelIfNoConnectionToDatabase()
    {
        JLabelDictionary label = new JLabelDictionary("noDatabaseConnection");
        add(label);
    }
    private void setUpperPanel()
    {
        upperPanel = new JPanel(new FlowLayout());
        JButtonDictionary add = new JButtonDictionary("add");
        add.addActionListener(e ->
        {
            if(TMSScrapManager.getInstance().isSthScrappedAlready())
            {
                javax.swing.SwingUtilities.invokeLater(GuardiansAddFrame::new);
            }
            else JOptionPane.showMessageDialog(frame, Dictionary.load("addWarnMessage"));
        });

        JButtonDictionary edit = new JButtonDictionary("edit");
        edit.addActionListener(e ->
        {
            if(TMSScrapManager.getInstance().isSthScrappedAlready())
            {
                if(getSelectedElement()!=null) javax.swing.SwingUtilities.invokeLater(GuardiansEditFrame::new);
                else JOptionPane.showMessageDialog(frame, Dictionary.load("editSelectWarnMessage"));
            }
            else JOptionPane.showMessageDialog(frame, Dictionary.load("addWarnMessage"));
        });

        JButtonDictionary delete = new JButtonDictionary("delete");
        delete.addActionListener(e ->
        {
            if(getSelectedElement()!=null) CurrencyGuardiansController.delete(selected);
            else JOptionPane.showMessageDialog(frame, Dictionary.load("deleteWarnMessage"));
        });

        JButtonDictionary selectOption = new JButtonDictionary("selectOption");
        selectOption.addActionListener(e -> new GuardiansSelectFrame());

        upperPanel.add(add); upperPanel.add(edit); upperPanel.add(delete); upperPanel.add(selectOption);
        add(upperPanel);
    }
    private void setLowerPanel()
    {
        setList();
    }
    public synchronized void deleteList()
    {   //plaf potrzebuje
        remove(lowerPanel);
        lowerPanel=null;
    }
    public synchronized void resetList()
    {   //plaf potrzebuje
        int width = Integer.parseInt(PropertiesLoader.load("GuardiansPanel.list.width"));
        int height = Integer.parseInt(PropertiesLoader.load("GuardiansPanel.list.height"));
        lowerPanel = new JScrollPane(list);
        lowerPanel.setPreferredSize(new Dimension(width,height));
        lowerPanel.createVerticalScrollBar();
        lowerPanel.createHorizontalScrollBar();
        add(lowerPanel);
    }
    private synchronized void setList()
    {
        listModel = new DefaultListModel<>();
        ArrayList<String> selectedList = CurrencyGuardiansController.getInstance().getSelectedList();
        for(CurrencyGuardian cg : DataBaseController.getInstance().selectCurrencyGuardians(selectedList))
        {
            listModel.addElement(cg);
        }
        list = new JList<>(listModel);
        lowerPanel = new JScrollPane(list);
        int width = Integer.parseInt(PropertiesLoader.load("GuardiansPanel.list.width"));
        int height = Integer.parseInt(PropertiesLoader.load("GuardiansPanel.list.height"));
        lowerPanel.setPreferredSize(new Dimension(width,height));
        lowerPanel.createVerticalScrollBar();
        lowerPanel.createHorizontalScrollBar();
        list.addListSelectionListener(e ->
        {
            if(!e.getValueIsAdjusting()) selected=getSelectedElement();
        });
        add(lowerPanel);
    }
    public CurrencyGuardian getSelectedElement()
    {
        CurrencyGuardian selected=null;
        List<CurrencyGuardian> selectedList = list.getSelectedValuesList();
        if(selectedList.size()>0) selected = selectedList.get(0);
        return selected;
    }
    public CurrencyGuardian changeSelectedListModelElement(CurrencyGuardian cg)
    {
        CurrencyGuardian selected = list.getSelectedValue();
        int selectedIndex = list.getSelectedIndex();
        listModel.set(selectedIndex,cg);
        list.setSelectedIndex(selectedIndex);
        this.selected=cg;
        return selected;
    }

    private void updateList()   //do zoptymalizowania
    {
        DefaultListModel newModel = new DefaultListModel<>();
        ArrayList<String> selectedList = CurrencyGuardiansController.getInstance().getSelectedList();
        for(CurrencyGuardian cg : DataBaseController.getInstance().selectCurrencyGuardians(selectedList))
        {
            newModel.addElement(cg);
        }
        listModel=newModel;
        list.setModel(newModel);
    }
    public void addElement(CurrencyGuardian cg)
    {
        listModel.addElement(cg);
    }
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        updateList();
    }
    public void setParentFrame(JFrame frame)
    {
        this.frame=frame;
    }
    //public JFrame getParentFrame() { return frame;}
    public DefaultListModel<CurrencyGuardian> getGuardiansList()
    {
        return listModel;
    }
    public void repaintList()
    {
        list.repaint();
    }
}
