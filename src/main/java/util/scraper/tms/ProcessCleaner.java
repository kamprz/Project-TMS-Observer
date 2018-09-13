package util.scraper.tms;

import config.Dictionary;
import controller.TMSScrapManager;
import installation.Loggers;
import config.PropertiesLoader;
import util.parsers.ProcessParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

// Klasa sprzątająca w systemie po module pobierającym dane - pozostawia on procesy zombie
// (Pythona i PyQt5), które sa usuwane przez obiekty tej klasy
public class ProcessCleaner implements Runnable, Callable
{
    private String tasklist;
    private String killProcess;
    private String pyQtProcessName;
    private String pythonProcessName;
    private int processNumberLimit;
    private ArrayList<String> pyQtProcessPIDList;
    private ArrayList<String> pythonProcessPIDList;
    private HashSet<Process> killers;
    private boolean isTurningSystemOff;

    private final Logger logger = Loggers.processCleanerLogger;
    public ProcessCleaner()
    {
        init();
    }
    public ProcessCleaner(boolean isTurningSystemOff)
    {
        init();
        this.isTurningSystemOff=isTurningSystemOff;
    }
    private void init()
    {
        pyQtProcessPIDList = new ArrayList<>();
        pythonProcessPIDList = new ArrayList<>();
        killers = new HashSet<>();
        loadProperties();
    }

    // metoda parsuje wynik komendy tasklist do list PIDów procesów PyQt i Pythona
    private Process parseTaskList() throws IOException
    {
        String s;
        Process process = Runtime.getRuntime().exec(tasklist);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        ProcessParser pyQtParser = new ProcessParser(pyQtProcessName);
        ProcessParser pythonParser = new ProcessParser(pythonProcessName);
        int iteration=0;
        while ((s = stdInput.readLine()) != null)
        {
            if(iteration++>3)
            {
                String parsedPyQt = pyQtParser.parse(s);
                if(!parsedPyQt.equals("")) pyQtProcessPIDList.add(parsedPyQt);
                String parsedPython = pythonParser.parse(s);
                if(!parsedPython.equals("")) pythonProcessPIDList.add(parsedPython);
            }
        }
        return process;
    }

    //metoda usuwa procesy, jezeli ich (pythona lub PyQt) jest większa od ustawionego limitu
    private void clean() throws IOException
    {
        if(isTurningSystemOff) processNumberLimit=1;
        Process process=parseTaskList();
        if(pyQtProcessPIDList.size()>=processNumberLimit || pythonProcessPIDList.size()>=processNumberLimit)
        {
            logger.info(Dictionary.load("ProcessCleaner.log.startCleaning1"));
            TMSScrapManager.getInstance().interruptInspectors();
            killProcesses();
            pyQtProcessPIDList.clear();
            killers.clear();
        }
        else
        {
            StringBuilder str = new StringBuilder(Dictionary.load("ProcessCleaner.log.notCleaning1"));
            str.append(pyQtProcessPIDList.size())
                    .append(Dictionary.load("ProcessCleaner.log.python"))
                    .append(pythonProcessPIDList.size())
                    .append(Dictionary.load("ProcessCleaner.log.notCleaning2"));
            logger.info(str.toString());
        }

        process.destroy();
    }

    //metoda niszcząca procesy
    private void killProcesses() throws IOException//, InterruptedException
    {
        int nrPyQt = pyQtProcessPIDList.size();
        int nrPython = pythonProcessPIDList.size();
        for(String s : pyQtProcessPIDList)
        {
            int pid = Integer.parseInt(s);
            Process process = Runtime.getRuntime().exec(killProcess+pid);
            killers.add(process);
        }
        for(String s : pythonProcessPIDList)
        {
            int pid = Integer.parseInt(s);
            Process process = Runtime.getRuntime().exec(killProcess+pid);
            killers.add(process);
        }
        logger.info(Dictionary.load("ProcessCleaner.log.deleted1")
                +nrPyQt+Dictionary.load("ProcessCleaner.log.deleted2")
                +nrPython+Dictionary.load("ProcessCleaner.log.deleted3"));
        if(!isTurningSystemOff)
        {
            logger.info(Dictionary.load("ProcessCleaner.log.restore"));
            TMSScrapManager.getInstance().restartScrappers();
        }
        else try { Thread.sleep(1000); }
        catch (InterruptedException e) { logger.info(Dictionary.load("ProcessCleaner.log.interrupted")); }
        for(Process process : killers) process.destroy();
    }

    private void loadProperties()
    {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("cleaner.tasklist");
        keys.add("cleaner.killProcess");
        keys.add("cleaner.pyQtProcessName");
        keys.add("cleaner.pythonProcessName");
        keys.add("cleaner.processNumberLimit");
        ArrayList<String> properties = PropertiesLoader.load(keys);
        tasklist = properties.get(0);
        killProcess = properties.get(1);
        pyQtProcessName = properties.get(2);
        pythonProcessName = properties.get(3);
        processNumberLimit = Integer.parseInt(properties.get(4));
    }

    @Override
    public void run()
    {
        try { clean(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public Object call() throws Exception {
        clean();
        Boolean result = true;
        return true;
    }
}
