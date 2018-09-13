package view.dictionary;

import config.Dictionary;
import events.listeners.ILanguageChangedListener;

import javax.swing.*;

//klasa napisu z możliwością zmiany języka
public class JLabelDictionary extends JLabel implements ILanguageChangedListener
{
    private String dictionary;
    public JLabelDictionary(String dictionaryKey)
    {
        dictionary=dictionaryKey;
        setText(Dictionary.load(dictionary));
        Dictionary.addLanguageChangeListener(this);
    }
    public void setDictionary(String str)
    {
        dictionary = str;
    }
    @Override
    public void changeLanguage() {
        setText(Dictionary.load(dictionary));
    }

}
