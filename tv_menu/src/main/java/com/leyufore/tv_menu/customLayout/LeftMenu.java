package com.leyufore.tv_menu.customLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.leyufore.tv_menu.util.LogU;

/**
 * 左侧菜单栏
 * Created by wenrule on 16/3/22.
 */
public class LeftMenu extends MultiColumnLayoutTemplate {

    private ImageView mFocusImage;
    /**
     * 添加一个监听器,是为了留一个接口给外部实现功能需求
     * 针对左侧菜单在移动动画结束时候给外部重新加载数据的需求
     */
    private NotifyLoadDataListener mNotifyLoadDataListener;

    private AnimatorSet animatorSet_focusImage;

    public LeftMenu(Context context) {
        super(context);
    }

    public LeftMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setNotifyLoadDataListener(NotifyLoadDataListener notifyLoadDataListener){
        this.mNotifyLoadDataListener = notifyLoadDataListener;
    }

    public void setFocusImage(ImageView focusImage,int visible){
        this.mFocusImage = focusImage;
        this.mFocusImage.setVisibility(visible);
    }

    public void changeFocusState(){
        if (this.mFocusImage.getVisibility() == GONE)
            this.mFocusImage.setVisibility(VISIBLE);
        else if (this.mFocusImage.getVisibility() == VISIBLE) {
            this.mFocusImage.setVisibility(GONE);
        } else {

        }
    }

    public void moveFocusImage(int focusRow, int focusColumn) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this.mFocusImage, "translationY",
                this.mFocusImage.getTranslationY(), focusRow * this.mItemHeight);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this.mFocusImage, "translationX",
                this.mFocusImage.getTranslationX(), focusColumn * this.mItemWidth);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(2000);
        set.playTogether(animator1, animator2);
        set.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mNotifyLoadDataListener != null){
                    mNotifyLoadDataListener.loadData(LeftMenu.this.mSelectedRow);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                LogU.logE("animator cancel");
            }
        });
        set.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.mAdapter == null) {
            return false;
        }
        if (keyEvent.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:  //按上键
                if (this.mSelectedRow > 0) {    //未到达顶部
                    //选择框出现移动时,更新最后一次选择的位置
                    this.mLastSelectedRow = this.mSelectedRow;
                    this.mLastSelectedColumn = this.mSelectedColumn;
                    //当前选择行数--
                    this.mSelectedRow--;
                    //行数浮标--
                    this.mFocusCursor--;
                    if (this.mFocusCursor < 0) {
                        this.mFocusCursor = 0;
                        //内容块移动时,需要进行View回收利用
                        recoveryAndLoad(this.mSelectedRow, this.mSelectedColumn, UP);
                        moveContent(this.mSelectedRow, this.mRow, UP);
                    }else {
                        //聚焦框移动
                        if (this.mFocusImage != null) {
                            moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
                        }
                    }
                    //提供接口给外面,当选择Item改变时,外部可进行功能需求实现.便于解耦
                    observerFocusChange();
                }
                //到达顶部不进行操作
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (this.mSelectedRow < getMaxRow() - 1) {
                    this.mLastSelectedRow = this.mSelectedRow;
                    this.mLastSelectedColumn = this.mSelectedColumn;
                    this.mSelectedRow++;
                    this.mFocusCursor++;
                    if (this.mFocusCursor > this.mRow - 1) {
                        this.mFocusCursor = this.mRow - 1;
                        recoveryAndLoad(this.mSelectedRow, this.mSelectedColumn, DOWN);
                        moveContent(this.mSelectedRow, this.mRow, DOWN);
                    }else {
                        if (this.mFocusImage != null) {
                            moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
                        }
                    }
                    observerFocusChange();
                }
                return true;
            default:
                return false;
        }
    }
}

