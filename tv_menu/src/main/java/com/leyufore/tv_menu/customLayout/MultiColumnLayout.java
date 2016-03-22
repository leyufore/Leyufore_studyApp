package com.leyufore.tv_menu.customLayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.adapter.AdapterTemplate;
import com.leyufore.tv_menu.model.PositionTag;
import com.leyufore.tv_menu.params_generate.LayoutParamsGenerator;
import com.leyufore.tv_menu.util.LogU;

public class MultiColumnLayout extends AbsoluteLayout {
    public static final int DOWN = 2;
    public static final int UP = 1;
    private List<View> allViews;
    private Context context;
    private AdapterTemplate mAdapter;
    private int mColumn;
    private int mFocusCursor;
    private ImageView mFocusImage;
    private int mItemHeight;
    private int mItemWidth;
    private int mLastSelectedColumn;
    private int mLastSelectedRow;
    private ObserverListener mObserverListener;
    private RecycleBin mRecyle;
    private int mRow;
    private int mSelectedColumn;
    private int mSelectedRow;
    private LayoutParamsGenerator paramsGenerator;

    public MultiColumnLayout(Context paramContext) {
        super(paramContext);
        this.context = paramContext;
        init(paramContext, null);
        LogU.logE("1");
    }

    public MultiColumnLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("2");
    }

    public MultiColumnLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("4");
    }

    @TargetApi(21)
    public MultiColumnLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("3");
    }

    private int findPostitionInAdapterByRowAndColumn(int row, int column) {
        return -1 + ((-1 + (row + 1)) * this.mColumn + (column + 1));
    }

    private int loadViewCount(int paramInt) {
        if (paramInt == -1 + getMaxRow())
            return this.mColumn - ((paramInt + 1) * this.mColumn - this.mAdapter.getCount());
        return this.mColumn;
    }

    private void observerFocusChange() {
        LogU.logE("animation start : ");
        LogU.logE("mLastSelectedRow: " + this.mLastSelectedRow);
        LogU.logE("mLastSelectedColumn : " + this.mLastSelectedColumn);
        LogU.logE("mSelectedRow : " + this.mSelectedRow);
        LogU.logE("mSelectedColumn : " + this.mSelectedColumn);
        if ((this.mObserverListener != null) && ((this.mLastSelectedRow != this.mSelectedRow) || (this.mLastSelectedColumn != this.mSelectedColumn))) {
            LogU.logE("allViews status:");
            for (int i = 0; i < this.allViews.size(); i++) {
                PositionTag localPositionTag = (PositionTag) ((View) this.allViews.get(i)).getTag();
                LogU.logE(i + " " + localPositionTag.getRow() + " " + localPositionTag.getColumn());
            }
            this.mObserverListener.itemSelected(getSelectedItem());
            this.mObserverListener.itemCancelSelected(getLastSelectedItem());
        }
    }

    private void recoveryAndLoad(int selectedRow, int selectedColumn, int direction) {
        switch (direction) {
            case DOWN:
                if (selectedRow - (1 + this.mRow) >= 0) {
                    removeAndPushViews(selectedRow - (1 + this.mRow));
                    LogU.logE("after DOWN removeAndPushViews:");
                    LogU.logE("Recycle bin size : " + this.mRecyle.size());
                }
                if (selectedRow + 1 <= getMaxRow() - 1) {
                    loadAndPopViews(selectedRow + 1, selectedColumn);
                }
                break;
            case UP:
                if (selectedRow + (1 + this.mRow) <= getMaxRow() - 1) {
                    removeAndPushViews(selectedRow + (1 + this.mRow));
                    LogU.logE("after UP removeAndPushViews:");
                    LogU.logE("Recycle bin size : " + this.mRecyle.size());
                }
                if (selectedRow - 1 >= 0) {
                    loadAndPopViews(selectedRow - 1, selectedColumn);
                }
                break;
            default:
                return;
        }
    }

    public void addView(View child) {
        AbsoluteLayout.LayoutParams lp = this.paramsGenerator.getParams();
        addView(child, getChildCount() - 1, lp);
    }

    public void addView(View child, AbsoluteLayout.LayoutParams lp) {
        addView(child, getChildCount() - 1, lp);
    }

    public void changeFocusState() {
        if (this.mFocusImage.getVisibility() == GONE)
            this.mFocusImage.setVisibility(VISIBLE);
        else if (this.mFocusImage.getVisibility() == VISIBLE) {
            this.mFocusImage.setVisibility(GONE);
        } else {

        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.mAdapter == null) {
            return false;
        }
        if (keyEvent.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (this.mSelectedRow > 0) {    //未到达顶部
                    this.mLastSelectedRow = this.mSelectedRow;
                    this.mLastSelectedColumn = this.mSelectedColumn;
                    this.mSelectedRow--;
                    this.mFocusCursor--;
                    if (this.mFocusCursor < 0) {
                        this.mFocusCursor = 0;
                        //内容块移动时,需要进行View回收利用
                        recoveryAndLoad(this.mSelectedRow, this.mSelectedColumn, UP);
                        moveContent(this.mSelectedRow, this.mRow, UP);
                    }
                    //聚焦框移动
                    moveFocusImage(this.mSelectedRow, this.mSelectedColumn);
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
                    moveFocusImage(this.mSelectedRow, this.mSelectedColumn);
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
                moveFocusImage(this.mSelectedRow, this.mSelectedColumn);
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
                moveFocusImage(this.mSelectedRow, this.mSelectedColumn);
                observerFocusChange();
                return true;
            case KeyEvent.KEYCODE_BACK:
            default:
                return false;
        }

    }

    public View getLastSelectedItem() {
        for (int i = 0; i < this.allViews.size(); i++) {
            View localView = (View) this.allViews.get(i);
            PositionTag localPositionTag = (PositionTag) localView.getTag();
            if ((localPositionTag.getRow() == this.mLastSelectedRow) && (localPositionTag.getColumn() == this.mLastSelectedColumn))
                return localView;
        }
        return null;
    }

    public int getMaxRow() {
        int i = this.mAdapter.getCount() % this.mColumn;
        int j = this.mAdapter.getCount() / this.mColumn;
        if (i == 0) ;
        for (int k = 0; ; k = 1)
            return k + j;
    }

    public View getSelectedItem() {
        for (int i = 0; i < this.allViews.size(); i++) {
            View localView = (View) this.allViews.get(i);
            PositionTag localPositionTag = (PositionTag) localView.getTag();
            if ((localPositionTag.getRow() == this.mSelectedRow) && (localPositionTag.getColumn() == this.mSelectedColumn))
                return localView;
        }
        return null;
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MultiColumnLayout);
        this.mColumn = typeArray.getInteger(R.styleable.MultiColumnLayout_multi_column, 1);
        this.mRow = typeArray.getInteger(R.styleable.MultiColumnLayout_multi_row, 2);
        this.mItemHeight = typeArray.getInteger(R.styleable.MultiColumnLayout_multi_item_height, 100);
        this.mItemWidth = typeArray.getInteger(R.styleable.MultiColumnLayout_multi_item_width, 100);
        this.paramsGenerator = new LayoutParamsGenerator(this.mColumn, this.mItemHeight, this.mItemWidth);
        this.mLastSelectedRow = -1;
        this.mLastSelectedColumn = -1;
        this.mSelectedRow = -1;
        this.mSelectedColumn = -1;
        this.mFocusCursor = -1;
        this.mRecyle = null;
        this.allViews = new ArrayList();
        this.mFocusImage = new ImageView(context);
        if (Build.VERSION.SDK_INT > 23) {
            this.mFocusImage.setBackgroundColor(getResources().getColor(R.color.orange, null));
        } else {
            this.mFocusImage.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        this.mFocusImage.setAlpha(0.5f);
        this.mFocusImage.setVisibility(GONE);
        AbsoluteLayout.LayoutParams localLayoutParams = new AbsoluteLayout.LayoutParams(this.mItemWidth, this.mItemHeight, 0, 0);
        addView(this.mFocusImage, localLayoutParams);
    }

    public void loadAndPopViews(int selectedRow, int selectedColumn) {

        for (int i = getChildCount() - 2; i >= 0; i--) {
            if (((PositionTag) getChildAt(i).getTag()).getRow() == selectedRow) {
                return;
            }
        }
        int loadViewCount = loadViewCount(selectedRow);
        for (int i = 0; i < loadViewCount; i++) {
            View view = this.mAdapter.getView(findPostitionInAdapterByRowAndColumn(selectedRow, i % this.mColumn), this.mRecyle.pop());
            view.setTag(new PositionTag(selectedRow, i % this.mColumn));
            addView(view, new AbsoluteLayout.LayoutParams(this.mItemWidth, this.mItemHeight, i * this.mItemWidth, selectedRow * this.mItemHeight));
        }
    }

    public void moveContent(int selectedRow, int showRow, int direction) {
        final int startY = getScrollY();
        final int endY;
        switch (direction){
            case DOWN:
                endY = (selectedRow - (showRow - 1)) * this.mItemHeight;
                break;
            case UP:
                endY = selectedRow * this.mItemHeight;
                break;
            default:
                return;
        }
        final int deltaY = endY - startY;
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1).setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                MultiColumnLayout.this.scrollTo(0, startY + (int) (fraction * deltaY));
            }
        });
        valueAnimator.start();
    }

    public void moveFocusImage(int selectedRow, int selectedColumn) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this.mFocusImage, "translationY",
                this.mFocusImage.getTranslationY(),selectedRow * this.mItemHeight).setDuration(300);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this.mFocusImage, "translationX",
                this.mFocusImage.getTranslationX(), selectedColumn * this.mItemWidth);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300L);
        animatorSet.playTogether(animator1,animator2);
        animatorSet.start();
    }

    public void removeAndPushViews(int selectedRow) {
        for (int i = -2 + getChildCount(); i >= 0; i--) {
            View localView = getChildAt(i);
            if (localView.getAnimation() != null)
                localView.getAnimation().cancel();
            localView.clearAnimation();
            if (((PositionTag) localView.getTag()).getRow() == selectedRow) {
                this.mRecyle.push(localView);
                removeViewAt(i);
            }
        }
    }

    public void setAdapter(AdapterTemplate adapter, int visible) {
        if ((adapter == null) || (adapter.getCount() == 0)) {
            LogU.logE("adapter is null or list in adapter is null");
            return;
        }
        this.mAdapter = adapter;
        int loadViewCount = Math.min((2 + this.mRow) * this.mColumn, adapter.getCount());
        int row = -1;
        for (int i = 0; i < loadViewCount; i++) {
            if (i % this.mColumn == 0)
                row++;
            View view = adapter.getView(i, null);
            view.setTag(new PositionTag(row, i % this.mColumn));
            addView(view);
            this.allViews.add(view);
        }
        this.mLastSelectedRow = 0;
        this.mLastSelectedColumn = 0;
        this.mSelectedRow = 0;
        this.mSelectedColumn = 0;
        this.mFocusCursor = 0;
        this.mRecyle = new RecycleBin();
        this.mFocusImage.setVisibility(visible);
    }

    public void setOnObserverListener(ObserverListener paramObserverListener) {
        if (paramObserverListener != null)
            this.mObserverListener = paramObserverListener;
    }
}