package view.dictionary;

import config.Dictionary;
import events.listeners.ILanguageChangedListener;

import javax.swing.*;

//klasa liścia menu z możliwością zmiany jezyka wyświetlanego napisu
public class JMenuItemDictionary extends JMenuItem implements ILanguageChangedListener
{
    private String dictionary;
    public JMenuItemDictionary(String dictionaryKey)
    {
        dictionary=dictionaryKey;
        setText(Dictionary.load(dictionary));
        Dictionary.addLanguageChangeListener(this);
    }
    @Override
    public void changeLanguage() {
        setText(Dictionary.load(dictionary));
    }
}
