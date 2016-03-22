package com.leyufore.tv_menu.customLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.leyufore.tv_menu.util.LogU;

/**
 * Created by wenrule on 16/3/22.
 */
public class Content extends MultiColumnLayoutTemplate {

    private ImageView mFocusImage;

    public Content(Context context) {
        super(context);
    }

    public Content(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Content(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Content(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
                this.mFocusImage.getTranslationY(),focusRow * this.mItemHeight);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this.mFocusImage, "translationX",
                this.mFocusImage.getTranslationX(), focusColumn * this.mItemWidth);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300L);
        animatorSet.playTogether(animator1,animator2);
        animatorSet.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        LogU.logE("content dispathce Key Event");
        //没数据则不处理
        if (this.mAdapter == null) {
            return false;
        }
        //避免按一次按键,触发两次事件
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
                    }
                    //聚焦框移动
                    moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
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
                    }
                    moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
                    observerFocusChange();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (this.mSelectedRow == 0 && this.mSelectedColumn == 0) {
                    return true;
                }
                this.mLastSelectedRow = this.mSelectedRow;
                this.mLastSelectedColumn = this.mSelectedColumn;

                if (this.mSelectedColumn == 0) {
                    this.mSelectedColumn = this.mColumn - 1;
                    this.mSelectedRow--;
                    this.mFocusCursor--;
                    if (this.mFocusCursor < 0) {
                        this.mFocusCursor = 0;
                        recoveryAndLoad(this.mSelectedRow, this.mSelectedColumn, UP);
                        moveContent(this.mSelectedRow, this.mRow, UP);
                    }
                } else {
                    this.mSelectedColumn--;
                }
                moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
                observerFocusChange();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (this.mSelectedRow == getMaxRow() - 1 && this.mSelectedColumn == this.mColumn - 1) {
                    return true;
                }
                this.mLastSelectedRow = this.mSelectedRow;
                this.mLastSelectedColumn = this.mSelectedColumn;

                if (this.mSelectedColumn == this.mColumn - 1) {
                    this.mSelectedColumn = 0;
                    this.mSelectedRow++;
                    this.mFocusCursor++;
                    if (this.mFocusCursor > this.mRow - 1) {
                        this.mFocusCursor = this.mRow - 1;
                        recoveryAndLoad(this.mSelectedRow, this.mSelectedColumn, DOWN);
                        moveContent(this.mSelectedRow, this.mRow, DOWN);
                    }
                } else {
                    this.mSelectedColumn++;
                }
                moveFocusImage(this.mFocusCursor, this.mSelectedColumn);
                observerFocusChange();
                return true;
            case KeyEvent.KEYCODE_BACK:
            default:
                return false;
        }
    }

    public int getCurrentSelectedColumn(){
        return this.mSelectedColumn;
    }

    public void lostFocus(){
        if(this.mObserverListener != null){
            this.mObserverListener.itemCancelSelected(getSelectedItem());
        }
    }

    public void getFocus(){
        if(this.mObserverListener != null){
            this.mObserverListener.itemSelected(getSelectedItem());
        }
    }

}
