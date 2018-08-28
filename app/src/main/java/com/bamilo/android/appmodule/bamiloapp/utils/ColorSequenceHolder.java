package com.bamilo.android.appmodule.bamiloapp.utils;

import java.util.List;

public class ColorSequenceHolder {
    private List<Integer> colors;
    private int currentColorIndex;

    public ColorSequenceHolder(List<Integer> colors) {
        this.colors = colors;
        currentColorIndex = 0;
    }

    public int getNextColor() {
        int color = colors.get(currentColorIndex % colors.size());
        currentColorIndex++;
        return color;
    }
}
