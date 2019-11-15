package com.example.aniscoreandroid;

import android.text.Editable;
import android.text.TextWatcher;

public class TextChangeListener implements TextWatcher {
    private String query;

    public TextChangeListener() {
        this.query = "";
    }

    public String getQuery() {
        return this.query;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        query = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}