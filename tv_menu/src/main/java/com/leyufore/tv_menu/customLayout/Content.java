package com.leyufore.tv_menu.customLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.leyufore.tv_menu.adapter.ImageTextAdapter;
import com.leyufore.tv_menu.model.ImageText;
import com.leyufore.tv_menu.model.PositionTag;
import com.leyufore.tv_menu.observer.DataObserver;
import com.leyufore.tv_menu.util.LogU;

/**
 * Created by wenrule on 16/3/22.
 */
public class Content extends MultiColumnLayoutTemplate {

    private ImageView mFocusImage;
    private int state = ImageTextAdapter.STATE_NORMAL;

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
                    /**
                     * 特殊情况 :
                     * 例子 : 按下时,选择位置移动到最后一行,而此时选择的列号为2.(列号标记为0,1,2....),而最后一行只有1个View.
                     * 此时需要将当前所选择的列号赋值为最后一行的最后一个View的列号
                     */

                    if (this.mSelectedRow == getMaxRow() - 1 && this.mSelectedColumn >= loadViewCount(getMaxRow() - 1)) {
                        this.mSelectedColumn = loadViewCount(getMaxRow() - 1) - 1;
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
                if (this.mSelectedRow == getMaxRow() - 1 && this.mSelectedColumn == loadViewCount(getMaxRow()-1) - 1) {
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

    /**
     *  当使用notifyDataSetChange时,聚焦框应该也把位置重置为0,0处
     */
    public void resetFocusImage(){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this.mFocusImage, "translationY",
                this.mFocusImage.getTranslationY(),0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this.mFocusImage, "translationX",
                this.mFocusImage.getTranslationX(), 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2);
        animatorSet.start();
    }

    /**
     * @return  当前内容块状态
     */
    public int getState(){
        return state;
    }

    /**
     * 改变内容块的状态
     */
    public void setState(int state){
        this.state = state;
        if(getChildCount() == 0) {
            return;
        }
        if(state == ImageTextAdapter.STATE_EDIT) {      //编辑状态
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setAlpha(0.5f);
            }
            if(mObserverListener != null){
                mObserverListener.itemSelected(getSelectedItem());
            }
        }else{      //正常显示状态
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setAlpha(1f);
            }
            if(mObserverListener != null){
                mObserverListener.itemSelected(getSelectedItem());
            }
        }

    }
    /**
     * 该方法是为了实现外部需求而提供的,返回当前选择项的数据的位置
     */
    public int getSelectedPosition(){
        return findPostitionInAdapterByRowAndColumn(this.mSelectedRow,this.mSelectedColumn);
    }

    /**
     * 编辑模式下删除某个项
     */
    public void removeViewInEditState(){
       /* * 1.改变数据
                * 2.刷新视图  - notifyDataSetChange()
                * 3.重定位到相应位置
                * 4.聚焦框移动到相应位置
                * */

        boolean lastOne = false;
        //删除的是最后一个时特殊处理
        if(this.mSelectedRow == getMaxRow() - 1 && this.mSelectedColumn == loadViewCount(this.mSelectedRow)-1){
            lastOne = true;
        }

        ImageTextAdapter imageTextAdapter = (ImageTextAdapter)this.mAdapter;
        int deletePosition = findPostitionInAdapterByRowAndColumn(this.mSelectedRow,this.mSelectedColumn);
        imageTextAdapter.deleteData(deletePosition);

        for(int i = getChildCount() - 1; i >=0; i--){
            View child = getChildAt(i);
            if (child.getAnimation() != null)
                child.getAnimation().cancel();
            child.clearAnimation();
            child.setTag(new PositionTag(-1,-1));
            this.mRecyle.push(child);
            removeViewAt(i);
        }

        int loadRow = this.mSelectedRow - (this.mFocusCursor + 1);
        LogU.logE("selectedROw : " + this.mSelectedRow + " mFocusCUrsor : " + this.mFocusCursor);
        for(int i = 0 ; i < this.mRow + 2; i++){
            LogU.logE("loadAndPopViews mROw :" + this.mRow + " loadROw : "+ loadRow);
            loadAndPopViews(loadRow,this.mSelectedColumn);
            loadRow++;
        }
        for(int i = 0;i < getChildCount();i++){
            getChildAt(i).setAlpha(0.5f);
        }
        if(lastOne){
            if(this.mSelectedColumn != 0){  //当前选择不是第一列
                this.mSelectedColumn--;
            }else{  //当前选择是第一列时
                if(this.mSelectedRow  == 0) {   //当前选择是第一行第一个,此时什么都不做,隐藏聚焦框
                    this.mFocusImage.setVisibility(GONE);
                    return;
                }   //当前选择不是第一行,但是第一列,此时行数-1,列号变为最后一个
                this.mSelectedRow--;
                this.mSelectedColumn = this.mColumn -1;
                if(this.mFocusCursor == 0){ //当前选择的是第一列同时也是显示第一行
                    loadAndPopViews(this.mSelectedRow - 1,this.mSelectedColumn);
                    moveContent(this.mSelectedRow - (this.mRow - 1),this.mRow,UP);
                    this.mFocusCursor = this.mRow - 1;
                }else{
                    this.mFocusCursor--;
                }
            }
        }
        moveFocusImage(this.mFocusCursor, this.mSelectedColumn);

        if(mObserverListener != null){
            mObserverListener.itemSelected(getSelectedItem());
        }
    }

}
