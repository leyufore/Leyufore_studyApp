package com.leyufore.tv_menu.model;

public class PositionTag
{
  private int column;
  private int row;

  public PositionTag(int row, int column)
  {
    this.row = row;
    this.column = column;
  }

  public int getColumn()
  {
    return this.column;
  }

  public int getRow()
  {
    return this.row;
  }

  public void setColumn(int column)
  {
    this.column = column;
  }

  public void setRow(int row)
  {
    this.row = row;
  }
}