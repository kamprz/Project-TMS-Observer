package view.dictionary;

import config.Dictionary;
import events.listeners.ILanguageChangedListener;

import javax.swing.*;

//klasa elementu menu z możliwością zmiany jezyka wyświetlanego napisu
public class JMenuDictionary extends JMenu implements ILanguageChangedListener
{
    private String dictionary;
    public JMenuDictionary(String dictionaryKey)
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
