package com.leyufore.tv_menu.adapter;

import android.view.View;

public abstract interface AdapterTemplate
{
  public abstract int getCount();

  public abstract View getView(int position, View convertView);
}