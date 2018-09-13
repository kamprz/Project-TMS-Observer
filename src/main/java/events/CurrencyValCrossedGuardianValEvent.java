package events;

import model.database.CurrencyGuardian;

import java.util.EventObject;

public class CurrencyValCrossedGuardianValEvent extends EventObject
{
    CurrencyGuardian currencyGuardian;
    public CurrencyValCrossedGuardianValEvent(Object source, CurrencyGuardian cg) {
        super(source);
        this.currencyGuardian = cg;
    }
    public CurrencyGuardian getCurrencyGuardian()
    {
        return currencyGuardian;
    }
}
