package model.database;

import javax.persistence.*;

@Entity
@Table(name="changeable_properties")
public class ChangeableProperties extends DataBaseObject
{
    @Id
    @Column(name="key_prop")
    private String key;
    @Column(name="value_prop")
    private String value;

    public ChangeableProperties(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ChangeableProperties() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ChangeableProperties{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
