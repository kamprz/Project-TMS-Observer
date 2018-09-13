package util.parsers;

//Klasa służąca parsowaniu linii tekstu pochodzącej z wywołania komendy systemowej tasklist
//i wyciągnięciu z niej nr PID procesu, będącego przedstawicielem procesu podanego jako argument konstruktora
public class ProcessParser extends AParser
{
    private String processName;
    public ProcessParser(String s) { this.processName = s; }
    public String parse(String input)
    {
        this.input = input;
        this.index = 0;
        if (checkProcessName())
        {
            while(input.charAt(index)==' ') index++;
            return getPID();
        }
        else return "";
    }

    private String getPID()
    {
        StringBuilder s = new StringBuilder();
        while(isDigit(input.charAt(index)))
        {
            s.append(input.charAt(index++));
        }
        return s.toString();
    }
    //sprawdza czy proces dany na wejście jest szukanego typu (processName)
    private boolean checkProcessName()
    {
        boolean result = false;
        StringBuilder stringBuilder = new StringBuilder();
        while(input.charAt(index) != ' ')
        {
            stringBuilder.append(input.charAt(index++));
        }
        if(stringBuilder.toString().equals(processName)) result = true;
        return result;
    }
}
