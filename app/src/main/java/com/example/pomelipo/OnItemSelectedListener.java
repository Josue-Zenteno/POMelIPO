package com.example.pomelipo;

import android.view.MenuItem;
import android.widget.ImageButton;

public interface OnItemSelectedListener {
    void onWorkSessionContextualMenu(int position, MenuItem menu);
    void onStartPressed(int position, ImageButton btnStart, ImageButton btnStop);
    void onStopPressed(ImageButton btnStart, ImageButton btnStop);
}
