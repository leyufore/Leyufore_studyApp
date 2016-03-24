package com.leyufore.tv_menu.customLayout;


import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.leyufore.tv_menu.util.LogU;

/**
 * 此处由于数量比较少,此处直接不考虑回收,把移动过程中回收代码收掉
 * bug : 由于在MultiColumnLayoutTemplate初始化时,加载VIew的数量并不一定就是全部的子View,
 * 因此可能导致添加数量过多时,程序做动画在null对象上.
 * 此处避免麻烦,不再做修改.由于其实右侧菜单应该直接做的,没有继承MultiColumnLayoutTemplate的必要,
 * 继承了反而多了很多不必要的麻烦
 * Created by wenrule on 16/3/22.
 */
public class RightMenu extends MultiColumnLayoutTemplate {

    private int contentScrollDeltaYPosition = 0;
    private int contentInitialDeltaY = -400;

    public RightMenu(Context context) {
        super(context);
    }

    public RightMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RightMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void moveFocusImage(int focusRow, int focusColumn) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.scrollTo(0,-400);
    }

    public void moveContent(int direction) {
        final int startY = getScrollY();

        switch (direction){
            case DOWN:
                this.contentScrollDeltaYPosition++;
                break;
            case UP:
                this.contentScrollDeltaYPosition--;
                break;
            default:
                return;
        }
        final int endY = -400 + this.contentScrollDeltaYPosition * mItemHeight;;
        final int deltaY = endY - startY;
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1).setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                RightMenu.this.scrollTo(0, startY + (int) (fraction * deltaY));
            }
        });
        valueAnimator.start();
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
                    moveContent(UP);
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
                    moveContent(DOWN);
                    observerFocusChange();
                }
                return true;
            default:
                return false;
        }
    }

}
