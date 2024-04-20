package ru.stepup.task5.model.enums;

public enum StateType {
    CLOSED("Закрыт"),
    OPEN("Открыт"),
    RESERVED("Зарезервирован"),
    DELETED("Удален");
    private  String name;

    StateType(String name) {
        this.name = name;
    }
}
