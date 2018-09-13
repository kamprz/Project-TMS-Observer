package exceptions;

public class ScrapingFailureException extends Exception
{
    public ScrapingFailureException(){}
    public ScrapingFailureException(String s)
    {
        super(s);
    }
}
