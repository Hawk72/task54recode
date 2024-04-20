package ru.stepup.task5.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Data {
    private List<KeyValueName> data = new ArrayList<>();

    @Override
    public String toString() {
        return "Data{" +
                "data=" + data +
                '}';
    }
}
