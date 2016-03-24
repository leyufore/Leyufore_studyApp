package com.leyufore.tv_menu.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ListView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    public static final int LEFT_MENU = 1;
    public static final int CONTENT = 2;
    public static final int RIGHT_MENU = 3;

    public static final int MENU_STATE_IN = 1;
    public static final int MENU_STATE_OUT = 2;

    public static final int MSG_LOAD_DATA = 1;

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
    private static Map<Integer, List<ImageText>> content_map = new HashMap<>();

    private Handler handler;

    private ListView listView;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_LOAD_DATA) {
                    LogU.logE("400ms get message");
                    imageTextAdapter.setList(content_map.get(msg.obj));
                    content.setAdapter(imageTextAdapter);
                    imageTextAdapter.notifyDataSetChange();
                    //更新完数据,刷新视图后,同时也把聚焦框还原到初始地方
                    content.resetFocusImage();
                }
            }
        };

        right_wrapper = (AbsoluteLayout) findViewById(R.id.right_wrapper);

        initData();

        initLeftMenu();
        initContent();
        initRightMenu();
    }

    @TargetApi(21)
    public void initData() {
        //初始化Content数据结合
        Drawable dog = getResources().getDrawable(R.drawable.dog, null);
        List<ImageText> dogList = new ArrayList<>();
        for (int i = 0; i < 26; i++)
            dogList.add(new ImageText(dog, "leyufore" + i));
        Drawable pad = getResources().getDrawable(R.drawable.pad, null);
        List<ImageText> padList = new ArrayList<>();
        for (int i = 0; i < 13; i++)
            padList.add(new ImageText(pad, "leyufore" + i));
        Drawable flower = getResources().getDrawable(R.drawable.flower, null);
        List<ImageText> flowerList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            flowerList.add(new ImageText(flower, "leyufore" + i));
        Drawable love = getResources().getDrawable(R.drawable.love, null);
        List<ImageText> loveList = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            loveList.add(new ImageText(love, "leyufore" + i));
        Drawable sadStory = getResources().getDrawable(R.drawable.sad, null);
        List<ImageText> sadStoryList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            sadStoryList.add(new ImageText(sadStory, "leyufore" + i));

        this.content_map.put(0, dogList);
        this.content_map.put(1, padList);
        this.content_map.put(2, flowerList);
        this.content_map.put(3, loveList);
        this.content_map.put(4, sadStoryList);

        //初始化left_menu数据集合
        this.left_menu_list.add("dog");
        this.left_menu_list.add("pad");
        this.left_menu_list.add("flower");
        this.left_menu_list.add("love");
        this.left_menu_list.add("sadStory");

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
                        /**
                         * 正常状态下,左侧菜单获得焦点,内容块失去焦点
                         * 编辑状态下,把处理逻辑交给内容块自己处理
                         */

                        LogU.logE("main activity content selectedColumn :" + content.getCurrentSelectedColumn());
                        if (content.getCurrentSelectedColumn() == 0) {
                            if (content.getState() == ImageTextAdapter.STATE_NORMAL) {
                                content.changeFocusState();
                                content.lostFocus();
                                left_menu.changeFocusState();
                                //改变当前页面的控制权.
                                changeMainController(LEFT_MENU);
                                return true;
                            } else {
                                return content.dispatchKeyEvent(keyEvent);
                            }
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
                if (content.getState() == ImageTextAdapter.STATE_EDIT) {
                    content.setState(ImageTextAdapter.STATE_NORMAL);
                } else {
                    this.finish();
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                switch (controller) {
                    case CONTENT:
                        if(this.controller == CONTENT) {    //控制权在内容块时,才能操作菜单
                            if (menu_state == MENU_STATE_OUT) {
                                this.lastController = this.controller;
                                this.controller = RIGHT_MENU;
                                menu_state = MENU_STATE_IN;
                                slideMenu(MENU_STATE_IN);
                            } else {  //menu_state == MENU_STATE_IN
                                this.controller = this.lastController;
                                menu_state = MENU_STATE_OUT;
                                slideMenu(MENU_STATE_OUT);
                            }
                        }
                        return true;
                    case RIGHT_MENU:
                        if (this.controller == RIGHT_MENU) {    //控制权在内容块时,才能操作菜单
                            if (menu_state == MENU_STATE_OUT) {
                                this.lastController = this.controller;
                                this.controller = RIGHT_MENU;
                                menu_state = MENU_STATE_IN;
                                slideMenu(MENU_STATE_IN);
                            } else {  //menu_state == MENU_STATE_IN
                                this.controller = this.lastController;
                                menu_state = MENU_STATE_OUT;
                                slideMenu(MENU_STATE_OUT);
                            }
                        }
                        return true;
                    default:
                        return true;
                }
            case KeyEvent.KEYCODE_DPAD_CENTER:
                LogU.logE("key enter occur");
                switch (controller) {
                    case LEFT_MENU:
                        return true;
                    case CONTENT:
                        if (content.getState() == ImageTextAdapter.STATE_EDIT) {
                            /**
                             * 为了实现该功能,Content的ViewGroup需提供重定位机制的功能
                             * 实现删除某个某个项的思路:
                             * 1.改变数据
                             * 2.刷新视图  - notifyDataSetChange()
                             * 3.重定位到相应位置
                             * 4.聚焦框移动到相应位置
                             **/
                            if (this.imageTextAdapter.size() > 1) {
                                this.content.removeViewInEditState();
                            } else if (this.imageTextAdapter.size() == 1) {
                                this.content.removeViewInEditState();
                                this.left_menu.changeFocusState();
                                changeMainController(LEFT_MENU);
                                this.content.setState(ImageTextAdapter.STATE_NORMAL);
                            }

                        }
                        return true;
                    case RIGHT_MENU:
                        if (menu_state == MENU_STATE_IN) {
                            this.content.setState(ImageTextAdapter.STATE_EDIT);
                            menu_state = MENU_STATE_OUT;
                            slideMenu(MENU_STATE_OUT);
                            this.controller = CONTENT;
                        }
                        return true;
                }
            default:
                LogU.logE("key code : " + keyEvent.getKeyCode());
                return false;
        }
        return false;
    }

    /**
     * 控制右侧菜单显示隐藏状态
     *
     * @param menu_state 想要的状态
     */
    public void slideMenu(int menu_state) {
        if (menu_state == MENU_STATE_IN) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.right_wrapper, "translationX",
                    this.right_wrapper.getTranslationX(), -300);
            animator.setDuration(200);
            animator.start();
        } else {  ////menu_state == MENU_STATE_OUT
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

        this.imageTextAdapter = new ImageTextAdapter(this.content.getContext(), content_map.get(0));
        this.content.setAdapter(this.imageTextAdapter);
        this.content.setFocusImage(focusImage, View.GONE);
        this.content.setOnObserverListener(new ObserverListener() {

            public void itemCancelSelected(View lastSelectedView) {
                if (content.getState() == ImageTextAdapter.STATE_EDIT) {
                    ImageView image_delete = (ImageView) lastSelectedView.findViewById(R.id.image_delete);
                    image_delete.setVisibility(View.GONE);
                }
                if (lastSelectedView.getAnimation() != null)
                    lastSelectedView.getAnimation().cancel();
                lastSelectedView.clearAnimation();
            }

            public void itemSelected(View selectedView) {
                ImageView image_delete = (ImageView) selectedView.findViewById(R.id.image_delete);
                if (content.getState() == ImageTextAdapter.STATE_EDIT) {
                    image_delete.setVisibility(View.VISIBLE);
                } else {
                    image_delete.setVisibility(View.GONE);
                }
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
        this.imageTextAdapter.notifyDataSetChange();
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
            public void loadData(int selectedRow) {
                handler.removeMessages(MSG_LOAD_DATA);
                Message msg = handler.obtainMessage(MSG_LOAD_DATA);
                msg.obj = selectedRow;
                handler.sendMessageDelayed(msg, 400);
            }
        });
        this.leftMenuAdapter.notifyDataSetChange();
    }

    public void initRightMenu() {
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
        this.rightMenuAdapter.notifyDataSetChange();
    }

    public void changeMainController(int controller) {
        this.controller = controller;
    }

}