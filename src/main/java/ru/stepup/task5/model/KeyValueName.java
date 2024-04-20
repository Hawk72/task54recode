package ru.stepup.task5.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueName {
    private String key;
    private String value;
    private String name;

    @Override
    public String toString() {
        return "KeyValueName{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
