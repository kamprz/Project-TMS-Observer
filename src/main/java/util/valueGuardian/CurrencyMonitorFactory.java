package util.valueGuardian;

import events.listeners.ICurrencyValCrossedGuardianValListener;
import model.currencies.tms.TMSCurrencyFrame;

//klasa tworząca wątki CurrencyMonitor
public abstract class CurrencyMonitorFactory
{
    public static void getCurrencyMonitor(TMSCurrencyFrame dataFrame, ICurrencyValCrossedGuardianValListener listening)
    {
        CurrencyMonitor cm = new CurrencyMonitor(dataFrame, listening);
        Thread thread = new Thread(cm);
        thread.start();
    }
}
