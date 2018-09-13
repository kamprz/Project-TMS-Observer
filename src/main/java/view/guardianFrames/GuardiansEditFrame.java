package view.guardianFrames;

import config.Dictionary;
import controller.CurrencyGuardiansController;
import model.database.CurrencyGuardian;
import view.mainFrame.GuardiansPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
//Okno umożlwiające użytkownikowi zmianę wybranego (zaznaczonego w JList w GuardiansPanelu) Guardiana
//różni się wywyłwaną operacją na bazie danych oraz automatycznym ładowaniem danych, które są zczytywane z wybranego Guardiana
public class GuardiansEditFrame extends GuardiansAddFrame
{
    public GuardiansEditFrame()
    {
        super();
        setValues();
    }

    //odczytanie wartości z zaznaczonego w JList w GuardiansPanelu Guardiana
    private void setValues()
    {
        CurrencyGuardian cg = GuardiansPanel.getInstance().getSelectedElement();
        selectedSymbol=cg.getSymbol();
        selectedRefersToBid=cg.getRefersToBid();
        value=cg.getValue();
        displaySelectedValues();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(buttonCancel))
        {
            dispose();
        }
        else if(e.getSource().equals(buttonOK))
        {
            try
            {
                CurrencyGuardian newCg = getCurrencyGuardian();
                wrongFormatLabel.setText(Dictionary.load(" "));
                CurrencyGuardiansController.edit(newCg);
                dispose();
            }
            catch (NumberFormatException e1) { wrongFormatLabel.setText(Dictionary.load("wrongFormat")); }
        }
    }
}