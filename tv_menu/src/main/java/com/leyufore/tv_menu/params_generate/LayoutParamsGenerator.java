package com.leyufore.tv_menu.params_generate;

import android.widget.AbsoluteLayout;

/**
 * setAdapter时,进行初始化第一次View布局时,自动生成LayoutParams.
 * 规则:一行一行排列
 */
public class LayoutParamsGenerator
{
  private int column;
  private int currentColumn;
  private int currentRow;
  private boolean flag;
  private int height;
  private int width;

  public LayoutParamsGenerator(int column, int height, int width)
  {
    this.column = column;
    this.height = height;
    this.width = width;
    this.currentColumn = -1;
    this.currentRow = -1;
  }

  private int getNextColumn()
  {
    this.currentColumn = (this.currentColumn + 1) % this.column;
    if(this.currentColumn == 0){
      this.flag = true;
    }else{
      this.flag = false;
    }
    return this.currentColumn;


  }

  private int getNextRow()
  {
    if (this.flag == true)
      this.currentRow = (1 + this.currentRow);
    return this.currentRow;
  }

  public AbsoluteLayout.LayoutParams getParams()
  {
    int column = getNextColumn();
    int row = getNextRow();
    return new AbsoluteLayout.LayoutParams(this.width, this.height, column * this.width, row * this.height);
  }

  public void reset(){
    this.currentColumn = -1;
    this.currentRow = -1;
  }

}
