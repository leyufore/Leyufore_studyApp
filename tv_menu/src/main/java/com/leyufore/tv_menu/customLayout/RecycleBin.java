package com.leyufore.tv_menu.customLayout;

import android.view.View;
import java.util.Deque;
import java.util.LinkedList;
import com.leyufore.tv_menu.util.LogU;

public class RecycleBin
{
  private Deque<View> deque = new LinkedList();

  public View pop()
  {
    if (this.deque.size() == 0)
    {
      LogU.logE("RecycleBin deque size = 0");
      return null;
    }
    return this.deque.removeFirst();
  }

  public void push(View view)
  {
    this.deque.add(view);
  }

  public int size()
  {
    return this.deque.size();
  }
}