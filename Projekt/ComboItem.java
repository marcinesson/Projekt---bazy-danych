package org.example;

// Ta klasa służy do tego, żeby w liście rozwijanej widzieć nazwę, ale pobierać ID
public class ComboItem {
    private int id;
    private String label;

    public ComboItem(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    // To metoda, którą Java wykorzystuje do wyświetlania tekstu w liście
    @Override
    public String toString() {
        return label;
    }
}