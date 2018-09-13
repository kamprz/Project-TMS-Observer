package view.mainFrame;

import config.Dictionary;
import config.PropertiesLoader;
import controller.MainFrameController;
import controller.TMSScrapManager;
import installation.ApplicationEngine;
import view.ActionGetNBP;
import view.dictionary.JButtonDictionary;
import view.dictionary.JLabelDictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ApplicationMainFrame extends JFrame implements WindowListener
{
    private GuardiansPanel panelLeft;
    private JPanel panelRight;
    private JTable tmsFrame;
    private ActionGetNBP action;
    private JLabelDictionary state;
    private final Object stateLock = new Object();
    private static ApplicationMainFrame singleton;

    public static synchronized ApplicationMainFrame getInstance() {
        if(singleton==null) singleton = new ApplicationMainFrame();
        return singleton;
    }

    private ApplicationMainFrame()
    {
        init();
    }
    private void init()
    {
        setFrame();
        setAction();
        setMenu();
        setPanels();
        setStateLabel();
        setVisible(true);
        int width = Integer.parseInt(PropertiesLoader.load("AppMainFrame.width"));
        int height = Integer.parseInt(PropertiesLoader.load("AppMainFrame.height"));
        setBounds(0,0,width,height);
    }

    private void setFrame()
    {
        setTitle(PropertiesLoader.load("ApplicationName"));
        setLayout(new GridLayout(Integer.parseInt(PropertiesLoader.load("AppMainFrame.grid.rows")),
                                 Integer.parseInt(PropertiesLoader.load("AppMainFrame.grid.cols")),
                                 Integer.parseInt(PropertiesLoader.load("AppMainFrame.grid.hgap")),
                                 Integer.parseInt(PropertiesLoader.load("AppMainFrame.grid.vgap"))));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }


    private void setAction()
    {
        action = new ActionGetNBP("getNBP");
    }
    private void setMenu()
    {
        AppMenu menu = new AppMenu(this,action);
        setJMenuBar(menu);
    }
    private void setPanels()
    {
        setPanelLeft();
        setPanelRight();
        setBackgroundColor();
    }
    private void setPanelLeft()
    {
        panelLeft = GuardiansPanel.getInstance();
        panelLeft.setParentFrame(singleton);
        panelLeft.setVisible(true);
        getContentPane().add(panelLeft);
    }
    private void setPanelRight() {
        panelRight = new JPanel();
        setTable();
        setButtons();
        panelRight.setVisible(true);
        getContentPane().add(panelRight);
    }
    private void setBackgroundColor()
    {
        int r = Integer.parseInt(PropertiesLoader.load("BackgroundColorR"));
        int g = Integer.parseInt(PropertiesLoader.load("BackgroundColorG"));
        int b = Integer.parseInt(PropertiesLoader.load("BackgroundColorB"));
        panelLeft.setBackground(new Color(r,g,b));
        panelRight.setBackground(new Color(r,g,b));
    }

    //inicjalizacja Tabeli do wyświetlania wyników pobierania danych
    private void setTable()
    {
        tmsFrame = new JTable(19, 6);
        panelRight.add(tmsFrame);
        tmsFrame.setValueAt("Symbol",0,0);
        tmsFrame.setValueAt("Bid",0,1);
        tmsFrame.setValueAt("Ask",0,2);
        tmsFrame.setValueAt("Time",0,3);
        tmsFrame.setValueAt("Low",0,4);
        tmsFrame.setValueAt("High",0,5);
        //próba napisania renderera do czytelniejszego wyświetlenia tabeli
        /*DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,
                                                           boolean isSelected,boolean hasFocus,int row,int column)
            {
                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                CurrencyValue v = (CurrencyValue) value;
                if(v.getColor()== ValuesDisplayColor.ZIELONY) c.setForeground(Color.GREEN);
                else if(v.getColor()== ValuesDisplayColor.CZERWONY) c.setForeground(Color.RED);
                else c.setForeground(Color.BLACK);
                return c;
            }
        };
        tableRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<6;i++) tmsFrame.getColumnModel().getColumn(i).setCellRenderer(tableRenderer);
        tmsFrame.getColumnModel().getColumn(1).setCellRenderer(tableRenderer);
        */
        tmsFrame.setEnabled(false);
    }

    private void setButtons()
    {
        JButton button2 = new JButton(action);
        panelRight.add(button2);
        JButtonDictionary resetScrapers = new JButtonDictionary("resetScrapers");
        resetScrapers.addActionListener(e -> MainFrameController.resetScrapers());
        panelRight.add(resetScrapers);
        JButtonDictionary stop = new JButtonDictionary("stopScraping");
        stop.addActionListener(e -> MainFrameController.stopScraping());
        panelRight.add(stop);
    }
    private void setStateLabel()
    {
        state = new JLabelDictionary("stateLabel.connecting");
        panelRight.add(state);
    }

    //ustawienie JLabela, który przedstawia obecny stan TMSScrapManagera
    public void setState(String dictionaryKey)
    {
        synchronized (stateLock)
        {
            state.setText(Dictionary.load(dictionaryKey));
            state.setDictionary(dictionaryKey);
        }
    }

    //metoda używana przez TMSScrapManagera do wyświetlania pobranych danych w tabeli
    public void setTMSValue(Object value, int row, int col)
    {
        tmsFrame.setValueAt(value,row,col);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    //ustawienie reakcji na próbę zamknięcia okna
    @Override
    public void windowClosing(WindowEvent e)
    {
        if (JOptionPane.showConfirmDialog(singleton, Dictionary.load("exitPaneMessage")
                , Dictionary.load("exitPaneTitle"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
        {
            singleton.setVisible(false);
            ApplicationEngine.getInstance().turnSystemOff();
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) { }
}
