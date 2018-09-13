package controller;

import model.currencies.CurrenciesSequence;
import model.database.ChangeableProperties;
import model.database.CurrencyGuardian;
import model.database.DataBaseObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DataBaseController
{
    private SessionFactory factory;
    private static DataBaseController singleton;
    private static boolean isConnected = false;
    private static final Object isConnectedLock = new Object();

    public static synchronized DataBaseController getInstance()
    {
        if(singleton==null)
        {
            singleton = new DataBaseController();
        }
        return singleton;
    }
    private DataBaseController()
    {
        try{
            factory = new Configuration()
                    .configure()
                    .addAnnotatedClass(CurrencyGuardian.class)
                    .buildSessionFactory();
            setIsConnected(true);
            if(selectChangeableProperty("language")==null)
                insertDataBaseObject(new ChangeableProperties("language","eng"));
        }
        catch(Exception e)
        {
            setIsConnected(false);
        }
    }

    public void insertDataBaseObject(DataBaseObject cg)
    {
        Session session = getSession();
        session.beginTransaction();
        session.save(cg);
        session.getTransaction().commit();
        session.close();
    }

    public List<CurrencyGuardian> selectCurrencyGuardians()
    {
        Session session = getSession();
        session.beginTransaction();
        String hql = "FROM CurrencyGuardian";
        Query query = session.createQuery(hql);
        List<CurrencyGuardian> list = query.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    public List<CurrencyGuardian> selectCurrencyGuardians(String selectedSymbol)
    {
        Session session = getSession();
        session.beginTransaction();
        String hql = "FROM CurrencyGuardian WHERE symbol = :selectedSymbol";
        Query query = session.createQuery(hql).setParameter("selectedSymbol", selectedSymbol);
        List<CurrencyGuardian> list = query.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    /*
        pobiera dane z bazy danych zgodnie z klauzulą WHERE, gdzie jej kolejnymi argumentami oddzielonymi alternatywą są
        kolejne elementy (symbole) zawarte w liście, będącej parametrem wejściowym
     */
    public List<CurrencyGuardian> selectCurrencyGuardians(ArrayList<String> selectedSymbols)
    {
        if(selectedSymbols==null || selectedSymbols.size()==0)
        {   //co by się program nie wywalił przy uruchamianiu i zapisanym stanie pustej listy
            selectedSymbols=new ArrayList<>();
            selectedSymbols.add(CurrenciesSequence.getCurrencySymbol(0));
        }
        Session session = getSession();
        StringBuilder hql = new StringBuilder("FROM CurrencyGuardian WHERE symbol = :selectedSymbol0");
        int i=0;
        if(selectedSymbols.size()>1)
            for(String str : selectedSymbols)
            {
                if(i>0) hql.append(" OR symbol = :selectedSymbol").append(i);
                i++;
            }
        session.beginTransaction();
        Query query = session.createQuery(hql.toString());
        for(i=0;i<selectedSymbols.size();i++)
        {
            query.setParameter("selectedSymbol"+i, selectedSymbols.get(i));
        }
        List<CurrencyGuardian> list = query.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    public void updateCurrencyGuardian(CurrencyGuardian oldCg, CurrencyGuardian newCg)
    {
        Session session = getSession();
        session.beginTransaction();
        CurrencyGuardian updated = session.get(CurrencyGuardian.class,oldCg.getId());
        updated.setRefersToBid(newCg.getRefersToBid());
        updated.setSymbol(newCg.getSymbol());
        updated.setValue(newCg.getValue());
        session.getTransaction().commit();
        session.close();
    }

    public void deleteCurrencyGuardian(CurrencyGuardian cg)
    {
        try
        {
            Session session = getSession();
            session.beginTransaction();
            session.delete(cg);
            session.getTransaction().commit();
            session.close();
        }
        catch (OptimisticLockException e) { /* Nie było czego usuwać */}
    }

    public ChangeableProperties selectChangeableProperty(String key)
    {
        Session session = getSession();
        session.beginTransaction();
        ChangeableProperties property = session.get(ChangeableProperties.class, key);
        session.getTransaction().commit();
        session.close();
        return property;
    }

    public void updateChangeableProperty(String key, String value)
    {
        Session session = getSession();
        session.beginTransaction();
        ChangeableProperties property = session.get(ChangeableProperties.class, key);
        property.setValue(value);
        session.getTransaction().commit();
        session.close();
    }

    private Session getSession()
    {
        return factory.getCurrentSession();
    }
    public void closeConnection()
    {
        if(factory!=null) factory.close();
    }

    public static boolean isConnected()
    {
        synchronized (isConnectedLock)
        {
            return isConnected;
        }
    }
    private void setIsConnected(boolean b)
    {
        synchronized (isConnectedLock)
        {
            isConnected = b;
        }
    }
}
