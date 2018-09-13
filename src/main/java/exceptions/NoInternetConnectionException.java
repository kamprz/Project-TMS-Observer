package exceptions;

public class NoInternetConnectionException extends Exception
{
    public NoInternetConnectionException()
    {
        super();
    }
    public NoInternetConnectionException(String s)
    {
        super(s);
    }
}
