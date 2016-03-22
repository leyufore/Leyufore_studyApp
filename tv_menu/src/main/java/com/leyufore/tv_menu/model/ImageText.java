package com.leyufore.tv_menu.model;

import android.graphics.drawable.Drawable;

public class ImageText
{
  private Drawable image;
  private String text;

  public ImageText(Drawable image, String text)
  {
    this.image = image;
    this.text = text;
  }

  public Drawable getImage()
  {
    return this.image;
  }

  public String getText()
  {
    return this.text;
  }

  public void setImage(Drawable image)
  {
    this.image = image;
  }

  public void setText(String text)
  {
    this.text = text;
  }
}