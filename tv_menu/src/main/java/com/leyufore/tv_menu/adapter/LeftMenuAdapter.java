package com.leyufore.tv_menu.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.util.List;

public class LeftMenuAdapter extends AbsAdapterTemplate{
    private Context context;
    private List<String> list;

    public LeftMenuAdapter(Context context, List<String> list) {
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
        TextView tv;
        if (convertView != null) {
            tv = (TextView) convertView;
        } else {
            tv = new TextView(this.context);
        }
        tv.setText(this.list.get(position));
        tv.setGravity(Gravity.CENTER);
        if (position == 0) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 1.5F, 1.0F, 1.5F, 1, 0.5F, 1, 0.5F);
            scaleAnimation.setFillAfter(true);
            tv.startAnimation(scaleAnimation);
        }
        return tv;
    }

    public void setList(List<String> list) {
        if (list != null)
            this.list = list;
    }
}