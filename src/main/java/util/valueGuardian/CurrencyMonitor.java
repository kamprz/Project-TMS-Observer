package util.valueGuardian;

import controller.DataBaseController;
import events.CurrencyValCrossedGuardianValEvent;
import events.listeners.ICurrencyValCrossedGuardianValListener;
import model.currencies.tms.TMSCurrency;
import model.currencies.tms.TMSCurrencyFrame;
import model.database.CurrencyGuardian;

//Klasa sprawdzająca, czy któraś z par walutowych pobranej właśnie ramki przekroczyła wartość graniczną strażnika
public class CurrencyMonitor implements Runnable
{
    private TMSCurrencyFrame dataFrame;
    private ICurrencyValCrossedGuardianValListener listener;


    public CurrencyMonitor(TMSCurrencyFrame dataFrame, ICurrencyValCrossedGuardianValListener listener)
    {
        this.dataFrame=dataFrame;
        this.listener=listener;
    }
    @Override
    public void run()
    {
        for(CurrencyGuardian cg : DataBaseController.getInstance().selectCurrencyGuardians())
        {
            TMSCurrency curr = dataFrame.getData().get(cg.getSymbol());
            if(hasScrappedValReachedGuardianVal(cg,curr))  fireEvent(cg);
        }
    }

    private boolean hasScrappedValReachedGuardianVal(CurrencyGuardian cg, TMSCurrency curr)
    {
        boolean result = false;
        double currVal;
        if(cg.getRefersToBid()) currVal = curr.getBid().getValue();
        else currVal = curr.getAsk().getValue();

        if( ( cg.getWasValueGreaterThanScrapped() && currVal>=cg.getValue() )
                || ( (!cg.getWasValueGreaterThanScrapped()) && currVal<=cg.getValue() )
                ) result = true;
        return result;
    }
    private void fireEvent(CurrencyGuardian cg)
    {
        CurrencyValCrossedGuardianValEvent event = new CurrencyValCrossedGuardianValEvent(this,cg);
        listener.guardianValueCrossed(event);
    }
}
