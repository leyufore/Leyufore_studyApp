package com.leyufore.tv_menu.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.model.ImageText;

import java.util.List;

public class ImageTextAdapter extends AbsAdapterTemplate
{
  private Context context;
  private List<ImageText> list;

  public ImageTextAdapter(Context context, List<ImageText> list)
  {
    this.context = context;
    this.list = list;
  }

  public int getCount()
  {
    if (this.list == null)
      return 0;
    return this.list.size();
  }

  @TargetApi(21)
  public View getView(int position, View convertView)
  {
    LinearLayout linearLayout;
    if (convertView != null){
      linearLayout = (LinearLayout)convertView;
    }else{
      linearLayout = (LinearLayout)LayoutInflater.from(this.context).inflate(R.layout.multi_column_item, null);
    }
      ImageView localImageView = (ImageView)linearLayout.findViewById(R.id.image_up);
      TextView localTextView = (TextView)linearLayout.findViewById(R.id.text_down);
      localImageView.setBackground((this.list.get(position)).getImage());
      localTextView.setText((this.list.get(position)).getText());
      localTextView.setGravity(Gravity.CENTER);
      return linearLayout;
  }

  public void setList(List<ImageText> list)
  {
    if (list != null)
      this.list = list;
  }
}
