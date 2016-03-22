package com.leyufore.tv_menu.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.adapter.ImageTextAdapter;
import com.leyufore.tv_menu.adapter.LeftMenuAdapter;
import com.leyufore.tv_menu.customLayout.MultiColumnLayout;
import com.leyufore.tv_menu.customLayout.ObserverListener;
import com.leyufore.tv_menu.model.ImageText;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ImageTextAdapter imageTextAdapter;
    private LeftMenuAdapter leftMenuAdapter;
    private MultiColumnLayout left_menu;
    private MultiColumnLayout multiColumnLayout;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        initLeftMenu();
        initContent();
    }
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() != KeyEvent.ACTION_UP)
            return false;
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return this.multiColumnLayout.dispatchKeyEvent(keyEvent);
    }

    @TargetApi(21)
    public void initContent() {
        Drawable localDrawable = getResources().getDrawable(R.drawable.dog, null);
        this.multiColumnLayout = ((MultiColumnLayout) findViewById(R.id.multiColumnLayout));
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < 28; i++)
            localArrayList.add(new ImageText(localDrawable, "leyufore" + i));
        this.imageTextAdapter = new ImageTextAdapter(this.multiColumnLayout.getContext(), localArrayList);
        this.multiColumnLayout.setAdapter(this.imageTextAdapter, 0);
        this.multiColumnLayout.setOnObserverListener(new ObserverListener() {
            public void itemCancelSelected() {
            }

            public void itemCancelSelected(View lastSelectedView) {
                if (lastSelectedView.getAnimation() != null)
                    lastSelectedView.getAnimation().cancel();
                lastSelectedView.clearAnimation();
            }

            public void itemSelected() {
            }

            public void itemSelected(View selectedView) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 1.1F, 1.0F, 1.1F, 1, 0.5F, 1, 0.5F);
                scaleAnimation.setDuration(300);
                scaleAnimation.setFillAfter(true);
                selectedView.startAnimation(scaleAnimation);
            }

            public void listCancelFocus() {
            }

            public void listFocus() {
            }
        });
    }

    public void initLeftMenu() {
        this.left_menu = ((MultiColumnLayout) findViewById(R.id.left_menu));
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < 14; i++)
            localArrayList.add("leyufore" + i);
        this.leftMenuAdapter = new LeftMenuAdapter(this.left_menu.getContext(), localArrayList);
        this.left_menu.setAdapter(this.leftMenuAdapter, 0);
        this.left_menu.setOnObserverListener(new ObserverListener() {
            public void itemCancelSelected() {
            }

            public void itemCancelSelected(View lastSelectedView) {
                lastSelectedView.clearAnimation();
            }

            public void itemSelected() {
            }

            public void itemSelected(View selectedView) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 1.5F, 1.0F, 1.5F, 1, 0.5F, 1, 0.5F);
                scaleAnimation.setDuration(300);
                scaleAnimation.setFillAfter(true);
                selectedView.startAnimation(scaleAnimation);
            }

            public void listCancelFocus() {
            }

            public void listFocus() {
            }
        });
    }


}