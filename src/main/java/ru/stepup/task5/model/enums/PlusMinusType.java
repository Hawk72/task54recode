package ru.stepup.task5.model.enums;

public enum PlusMinusType {
    Повышающий("+"),
    Понижающий("-");
    private  String name;

    PlusMinusType(String name) {
        this.name = name;
    }
}
