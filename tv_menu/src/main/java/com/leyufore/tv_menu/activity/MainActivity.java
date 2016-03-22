package com.leyufore.tv_menu.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.adapter.ImageTextAdapter;
import com.leyufore.tv_menu.adapter.LeftMenuAdapter;
import com.leyufore.tv_menu.adapter.RightMenuAdapter;
import com.leyufore.tv_menu.customLayout.Content;
import com.leyufore.tv_menu.customLayout.LeftMenu;
import com.leyufore.tv_menu.customLayout.NotifyLoadDataListener;
import com.leyufore.tv_menu.customLayout.ObserverListener;
import com.leyufore.tv_menu.customLayout.RightMenu;
import com.leyufore.tv_menu.model.ImageText;
import com.leyufore.tv_menu.util.LogU;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final int LEFT_MENU = 1;
    public static final int CONTENT = 2;
    public static final int RIGHT_MENU = 3;

    public static final int MENU_STATE_IN = 1;
    public static final int MENU_STATE_OUT = 2;
    //在菜单进入显示前,哪个模块控制.
    private int lastController;

    private LeftMenuAdapter leftMenuAdapter;
    private LeftMenu left_menu;
    private ImageTextAdapter imageTextAdapter;
    private Content content;
    private RightMenuAdapter rightMenuAdapter;
    private RightMenu rightMenu;

    private int controller = LEFT_MENU;
    private int menu_state = MENU_STATE_OUT;


    private AbsoluteLayout right_wrapper;

    private static List<String> left_menu_list = new ArrayList<>();
    private static List<String> rigth_menu_list = new ArrayList<>();
    private static List<ImageText> content_list1 = new ArrayList<>();
    private static List<ImageText> content_list2 = new ArrayList<>();


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);

        right_wrapper = (AbsoluteLayout) findViewById(R.id.right_wrapper);

        initData();

        initLeftMenu();
        initContent();
        initRightMenu();
    }

    @TargetApi(21)
    public void initData(){
        //初始化Content数据结合
        Drawable dog = getResources().getDrawable(R.drawable.dog, null);
        for (int i = 0; i < 28; i++)
            this.content_list1.add(new ImageText(dog, "leyufore" + i));
        Drawable pad = getResources().getDrawable(R.drawable.pad, null);
        for (int i = 0; i < 13; i++)
            this.content_list2.add(new ImageText(pad, "leyufore" + i));

        //初始化left_menu数据集合
        for (int i = 0; i < 14; i++)
            left_menu_list.add("leyufore" + i);
        //初始化right_menu数据集合
        this.rigth_menu_list.add("删除内容");
        this.rigth_menu_list.add("删除全部内容");
    }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() != KeyEvent.ACTION_UP)
            return false;
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                switch (controller) {
                    case LEFT_MENU:
                        return this.left_menu.dispatchKeyEvent(keyEvent);
                    case CONTENT:
                        return this.content.dispatchKeyEvent(keyEvent);
                    case RIGHT_MENU:
                        return this.rightMenu.dispatchKeyEvent(keyEvent);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                switch (controller) {
                    case LEFT_MENU:
                        return this.left_menu.dispatchKeyEvent(keyEvent);
                    case CONTENT:
                        return this.content.dispatchKeyEvent(keyEvent);
                    case RIGHT_MENU:
                        return this.rightMenu.dispatchKeyEvent(keyEvent);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                switch (controller) {
                    case LEFT_MENU:
                        return true;
                    case CONTENT:
                        //左侧菜单获得焦点,内容块失去焦点
                        LogU.logE("main activity content selectedColumn :" + content.getCurrentSelectedColumn());
                        if(content.getCurrentSelectedColumn() == 0){
                            content.changeFocusState();
                            content.lostFocus();
                            left_menu.changeFocusState();
                            //改变当前页面的控制权.
                            changeMainController(LEFT_MENU);
                            return true;
                        }
                        return content.dispatchKeyEvent(keyEvent);
                    case RIGHT_MENU:
                        return true;
                    default:
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                switch (controller) {
                    case LEFT_MENU:
                        left_menu.changeFocusState();
                        content.changeFocusState();
                        content.getFocus();
                        changeMainController(CONTENT);
                        break;
                    case CONTENT:
                        return this.content.dispatchKeyEvent(keyEvent);
                    case RIGHT_MENU:
                        return true;
                    default:
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            case KeyEvent.KEYCODE_MENU:
                LogU.logE("key menu occure");
                if(menu_state == MENU_STATE_OUT){
                    this.lastController = this.controller;
                    this.controller = RIGHT_MENU;
                    menu_state = MENU_STATE_IN;
                    slideMenu(MENU_STATE_IN);
                }else{  //menu_state == MENU_STATE_IN
                    this.controller = this.lastController;
                    menu_state = MENU_STATE_OUT;
                    slideMenu(MENU_STATE_OUT);
                }
            default:
                return false;
        }
        return false;
    }

    /**
     * 控制右侧菜单显示隐藏状态
     * @param menu_state    想要的状态
     */
    public void slideMenu(int menu_state){
        if(menu_state == MENU_STATE_IN){
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.right_wrapper, "translationX",
                    this.right_wrapper.getTranslationX(), -300);
            animator.setDuration(200);
            animator.start();
        }else{  ////menu_state == MENU_STATE_OUT
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.right_wrapper, "translationX",
                    this.right_wrapper.getTranslationX(), 0);
            animator.setDuration(200);
            animator.start();
        }
    }

    @TargetApi(21)
    public void initContent() {
        ImageView focusImage = (ImageView) findViewById(R.id.content_focus_image);
        this.content = (Content) findViewById(R.id.content);

        ArrayList<ImageText> list = new ArrayList<>(content_list1);
        this.imageTextAdapter = new ImageTextAdapter(this.content.getContext(), list);
        this.content.setAdapter(this.imageTextAdapter);
        this.content.setFocusImage(focusImage,View.GONE);
        this.content.setOnObserverListener(new ObserverListener() {

            public void itemCancelSelected(View lastSelectedView) {
                if (lastSelectedView.getAnimation() != null)
                    lastSelectedView.getAnimation().cancel();
                lastSelectedView.clearAnimation();
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
        this.left_menu = ((LeftMenu) findViewById(R.id.left_menu));
        ImageView focusImage = (ImageView) findViewById(R.id.left_menu_focus_image);

        ArrayList list = new ArrayList(left_menu_list);
        this.leftMenuAdapter = new LeftMenuAdapter(this.left_menu.getContext(), list);
        this.left_menu.setAdapter(this.leftMenuAdapter);
        this.left_menu.setFocusImage(focusImage, View.VISIBLE);
        this.left_menu.setOnObserverListener(new ObserverListener() {

            public void itemCancelSelected(View lastSelectedView) {
                lastSelectedView.clearAnimation();
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
        this.left_menu.setNotifyLoadDataListener(new NotifyLoadDataListener() {
            @Override
            public void loadData(int position) {
                LogU.logE("position");
            }
        });
    }

    public void initRightMenu(){
        this.rightMenu = (RightMenu) findViewById(R.id.right_menu);

        ArrayList list = new ArrayList(rigth_menu_list);
        this.rightMenuAdapter = new RightMenuAdapter(this.rightMenu.getContext(), list);
        this.rightMenu.setAdapter(this.rightMenuAdapter);
        this.rightMenu.setOnObserverListener(new ObserverListener() {

            public void itemCancelSelected(View lastSelectedView) {
                lastSelectedView.clearAnimation();
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

    public void changeMainController(int controller){
        this.controller = controller;
    }

}