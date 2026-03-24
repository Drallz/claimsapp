package com.example.myapplication;

import android.view.View;

public class DashboardItem {
    private String title;
    private int iconResId;
    private View.OnClickListener clickListener;

    public DashboardItem(String title, int iconResId, View.OnClickListener clickListener) {
        this.title = title;
        this.iconResId = iconResId;
        this.clickListener = clickListener;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }
}