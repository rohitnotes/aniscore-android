package com.example.aniscoreandroid.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TextChangeListener implements TextWatcher {
    private String query;
    private TextView submit = null;

    public TextChangeListener() {
        this.query = "";
    }

    public TextChangeListener(TextView submitButton) {
        submit = submitButton;
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
        if (submit != null && s.length() > 0) {
            submit.setTextColor(Color.rgb(255,192,203));
        } else if (submit != null && s.length() == 0) {
            submit.setTextColor(Color.rgb(224,224,224));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void clearQuery() {
        this.query = "";
    }
}