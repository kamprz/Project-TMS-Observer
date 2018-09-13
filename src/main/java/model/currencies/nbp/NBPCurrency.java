package model.currencies.nbp;

public class NBPCurrency
{
    private String currency;
    private String code;
    private String mid;

    public NBPCurrency(String currency, String code, String mid) {
        this.currency = currency;
        this.code = code;
        this.mid = mid;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public String getMid() {
        return mid;
    }

    @Override
    public String toString() {
        return "NBPCurrency{" +
                "currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", mid='" + mid + '\'' +
                '}';
    }
}
