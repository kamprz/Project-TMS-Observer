package model.database;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="currencyguardian")
public class CurrencyGuardian extends DataBaseObject
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="idCG")
    private int id;

    @Column(name="symbolCG")
    private String symbol;

    @Column(name="valueCG")
    private double value;

    @Column(name="setting_dateCG")
    private String settingDate;

    @Column(name="refers_to_bid")
    private Boolean refersToBid;

    @Column(name="was_value_greater_than_scrapped")
    private Boolean wasValueGreaterThanScrapped;

    public CurrencyGuardian(String symbol, double value, Boolean refersToBid, Boolean wasValueGreaterThanScrapped)
    {
        this.symbol = symbol;
        this.value = value;
        SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.S");
        this.settingDate =s.format(new Date());
        this.refersToBid = refersToBid;
        this.wasValueGreaterThanScrapped = wasValueGreaterThanScrapped;
    }

    public CurrencyGuardian(){}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(symbol+"    ");
        if(refersToBid) str.append("Bid"+"    ");
        else str.append("Ask"+"    ");
        str.append(value+"    ");
        str.append(settingDate);
        return str.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public String getSettingDate() {
        return settingDate;
    }

    public void setSettingDate(String settingDate) {
        this.settingDate = settingDate;
    }
    public boolean getRefersToBid() {
        return refersToBid;
    }

    public void setRefersToBid(boolean refersToBid) {
        this.refersToBid = refersToBid;
    }

    public boolean getWasValueGreaterThanScrapped() {
        return wasValueGreaterThanScrapped;
    }

    public void setWasValueGreaterThanScrapped(boolean wasValueGreaterThanScrapped) {
        this.wasValueGreaterThanScrapped = wasValueGreaterThanScrapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyGuardian that = (CurrencyGuardian) o;

        if (id != that.id) return false;
        if (Double.compare(that.value, value) != 0) return false;
        if (!symbol.equals(that.symbol)) return false;
        return refersToBid.equals(that.refersToBid);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + symbol.hashCode();
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + refersToBid.hashCode();
        return result;
    }
}
