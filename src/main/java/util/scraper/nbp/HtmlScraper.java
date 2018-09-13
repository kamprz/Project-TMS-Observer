package util.scraper.nbp;

import installation.Loggers;
import config.PropertiesLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//pobiera plik xml ze strony NBP
public class HtmlScraper
{
    public static String getHtml() throws IOException
    {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        StringBuilder result= new StringBuilder();
        url = new URL(PropertiesLoader.load("HtmlScraper.site"));
        is = url.openStream();
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) { result.append(line); }
        if (is != null) is.close();
        return result.toString();
    }
}
