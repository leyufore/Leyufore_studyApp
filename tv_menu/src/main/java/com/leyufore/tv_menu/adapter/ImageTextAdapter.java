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

public class ImageTextAdapter extends AbsAdapterTemplate {
    private Context context;
    private List<ImageText> list;

    public  static final int STATE_NORMAL = 1;
    public  static final int STATE_EDIT = 2;
    //记录两种不同的内容视图状态
//    private int state = STATE_NORMAL;

    public ImageTextAdapter(Context context, List<ImageText> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        if (this.list == null)
            return 0;
        return this.list.size();
    }

    @TargetApi(21)
    public View getView(int position, View convertView) {
        LinearLayout linearLayout;
        if (convertView != null) {
            linearLayout = (LinearLayout) convertView;
        } else {
            linearLayout = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.content_item, null);
        }
        ImageView image_up = (ImageView) linearLayout.findViewById(R.id.image_up);
        TextView tv = (TextView) linearLayout.findViewById(R.id.text_down);
        ImageView image_delete = (ImageView) linearLayout.findViewById(R.id.image_delete);
        image_up.setBackground((this.list.get(position)).getImage());
        tv.setText((this.list.get(position)).getText());
        tv.setGravity(Gravity.CENTER);
        image_delete.setVisibility(View.GONE);
        return linearLayout;
    }

    public void setList(List<ImageText> list) {
        if (list != null)
            this.list = list;
    }

/*    *//**
     * 设置当前内容块子VIew显示的状态
     *//*
    public void setState(int state){
        this.state = state;
    }*/
    public void deleteData(int position) {
        if (list != null) {
            list.remove(position);
        }
    }
    public int size(){
        if(list == null)
            return 0;
        return list.size();
    }
}
