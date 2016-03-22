package com.leyufore.tv_menu.customLayout;

import android.view.View;

public abstract interface ObserverListener
{

  public abstract void itemCancelSelected(View paramView);


  public abstract void itemSelected(View paramView);

  public abstract void listCancelFocus();

  public abstract void listFocus();
}
