package view.dictionary;

import config.Dictionary;
import events.listeners.ILanguageChangedListener;

import javax.swing.*;

//klasa przycisku z możliwością zmiany jezyka wyświetlanego napisu
public class JButtonDictionary extends JButton implements ILanguageChangedListener
{
    private String dictionary;
    public JButtonDictionary(String dictionaryKey)
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
