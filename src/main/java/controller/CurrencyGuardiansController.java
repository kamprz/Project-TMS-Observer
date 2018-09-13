package controller;

import events.SelectionChangedEvent;
import events.listeners.ISelectionChangedListener;
import model.currencies.CurrenciesSequence;
import model.database.ChangeableProperties;
import model.database.CurrencyGuardian;
import view.mainFrame.GuardiansPanel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;

public class CurrencyGuardiansController
{
    //zbiór wszystkich zaznaczonych checkboxów w GuardiansSelectFrame
    private static HashSet<String> selected=new HashSet<>();
    //zbiór wszystkich zmienionych checkboxów od ostatniego otworzenia okna
    private static HashSet<String> changed=new HashSet<>();
    private static final Object lock = new Object();
    private ISelectionChangedListener listener;
    private static CurrencyGuardiansController singleton;
    private CurrencyGuardiansController()
    {
        for(int i=0;i<CurrenciesSequence.getCurrenciesNumber();i++)
        {
            String symbol = CurrenciesSequence.getCurrencySymbol(i);
            String checkbox = "GuardiansSelectFrame.Checkbox"+symbol;
            if(isSymbolSelected(checkbox)) selected.add(symbol);
        }
    }
    //przy uruchamianiu aplikacji odczytuje z bazy danych czy dany symbol był zaznaczony przy ostatnim użyciu
    private Boolean isSymbolSelected(String checkbox)
    {
        ChangeableProperties cp = DataBaseController.getInstance().selectChangeableProperty(checkbox);
        if(cp==null) return false;
        else return Boolean.valueOf(cp.getValue());
    }

    //inicjalizuje obiekt - ładuje dane z bazy
    public static void install()
    {
        getInstance();
    }
    public static CurrencyGuardiansController getInstance() {
        if(DataBaseController.isConnected())
        {
            if(singleton==null)  singleton = new CurrencyGuardiansController();
            return singleton;
        }
        else return null;
    }

    //metda wywoływana po zatwierdzeniu dodania nowego CurrencyGuardiana w GuardiansAddFrame
    public static void add(CurrencyGuardian cg)
    {
        Thread t = new Thread(() ->
        {
            DataBaseController.getInstance().insertDataBaseObject(cg);
            GuardiansPanel.getInstance().addElement(cg);
        });
        t.start();
    }

    //metda wywoływana po zatwierdzeniu modyfikacji CurrencyGuardiana w GuardiansEditFrame
    public static void edit(CurrencyGuardian newCg)
    {
        Thread t = new Thread(() ->
        {
            CurrencyGuardian oldCg = GuardiansPanel.getInstance().changeSelectedListModelElement(newCg);
            newCg.setId(oldCg.getId());
            DataBaseController.getInstance().updateCurrencyGuardian(oldCg,newCg);
        });
        t.start();

    }

    //metoda wywoływana podczas usuwania CurrencyGuardiana w GuardiansPanel
    public static void delete(CurrencyGuardian cg)
    {
        Thread t = new Thread(() ->
        {
            DefaultListModel<CurrencyGuardian> list = GuardiansPanel.getInstance().getGuardiansList();
            if(list.contains(cg)) list.removeElement(cg);
            GuardiansPanel.getInstance().repaintList();
            DataBaseController.getInstance().deleteCurrencyGuardian(cg);
        });
        t.start();
    }

    //reakcja na zmianę zaznaczenia checkboxa w GuardiansSelectFrame
    public void selectionChanged(ItemEvent e)
    {
        synchronized(lock)
        {
            JCheckBox source = (JCheckBox) e.getItem();
            String symbol = source.getText();
            if(changed.contains(symbol)) changed.remove(symbol);
            else changed.add(symbol);
        }
    }
    //czyści zawartość zbioru zmienionych obiektów
    public void clearChanged()
    {
        Thread t = new Thread(() -> { synchronized(lock) { changed.clear(); } });
        t.start();
    }

    //wywoływana po zatwierdzeniu zmian w GuardiansSelectFrame
    public void okClicked()
    {
        Thread t = new Thread(() ->
        {
            synchronized(lock)
            {
                for (String symbol : changed)
                {
                    if (selected.contains(symbol))
                    {
                        DataBaseController.getInstance().updateChangeableProperty("GuardiansSelectFrame.Checkbox" + symbol, "false");
                        selected.remove(symbol);
                    }
                    else
                    {
                        if (DataBaseController.getInstance().selectChangeableProperty("GuardiansSelectFrame.Checkbox" + symbol) != null)
                            DataBaseController.getInstance().updateChangeableProperty("GuardiansSelectFrame.Checkbox" + symbol, "true");
                        else DataBaseController.getInstance().insertDataBaseObject(new ChangeableProperties("GuardiansSelectFrame.Checkbox" + symbol, "true"));
                        selected.add(symbol);
                    }
                }
                if(changed.size()>0)
                {
                    listener.selectionChanged(new SelectionChangedEvent(this));
                    changed.clear();
                }
            }
        });
        t.start();
    }

    //wywoływana przy wyjściu z GuardiansSelectFrame
    public void cancelClicked()
    {
        clearChanged();
    }

    public boolean isSelected(String symbol) {
        return selected.contains(symbol);
    }

    public ArrayList<String> getSelectedList()
    {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(selected);
        return list;
    }
    public void setListener(ISelectionChangedListener listen)
    {
        listener = listen;
    }
}
