package events.listeners;

import events.CurrencyValCrossedGuardianValEvent;

public interface ICurrencyValCrossedGuardianValListener
{
    public void guardianValueCrossed(CurrencyValCrossedGuardianValEvent event);
}
