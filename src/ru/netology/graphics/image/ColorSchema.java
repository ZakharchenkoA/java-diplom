package ru.netology.graphics.image;

public class ColorSchema implements TextColorSchema{

    public static final char[] symbols = {'#', '$', '@', '%', '*', '+', '-', '/'};

    public char convert(int color) {
        return symbols[color / 32];
    }
}
