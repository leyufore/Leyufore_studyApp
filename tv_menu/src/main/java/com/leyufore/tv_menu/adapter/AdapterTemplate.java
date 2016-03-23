package com.leyufore.tv_menu.adapter;

import android.view.View;

public interface AdapterTemplate {

    int getCount();

    View getView(int position, View convertView);

}