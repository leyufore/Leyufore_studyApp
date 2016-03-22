package com.leyufore.tv_menu.customLayout;

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

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.adapter.AdapterTemplate;
import com.leyufore.tv_menu.model.PositionTag;
import com.leyufore.tv_menu.params_generate.LayoutParamsGenerator;
import com.leyufore.tv_menu.util.LogU;

import java.util.ArrayList;
import java.util.List;

/**
 * whaley tv端 显示的行列固定,Item宽高固定的ViewGroup
 * 采用继承AbsoluteLayout方式实现
 * 问题:选择的View的动画紊乱,时有时无
 * 原因:View在进行动画时,可能进行了回收利用,导致出错
 * 方案: 1.获得的View开启动画 2.失去焦点的View结束动画 3.回收的View取消动画
 * 滑动选择:
 * 1.聚焦框移动 : 采用属性动画,改变了其实际位置,符合需求
 * 2.ViewGroup内容块移动 : 采用ScrollTo方式,只改了其中的内容位置,而没有改变View位置.动画效果只会影响VIew位置,不符合要求
 * 3.View获得失去焦点的动画 : 采用VIew动画,对View没有实际影响,感觉更适合需求.
 */
public class MultiColumnLayoutDemo extends AbsoluteLayout {
    //移动方向常量
    public static final int DOWN = 2;
    public static final int UP = 1;
    /**
     * 保存所有的View,包括加到ViewGroup中的子View + RecycleBIn中的View,由于java引用的机制,全局只有一份对象,
     * 只是有两处引用而已.
     */
    private List<View> allViews;
    private Context context;
    //mColumn,mRow,mItemHeight,mItemWidth显示的行列数,以及Item的宽高.在xml中提取
    private int mColumn;
    private int mRow;
    private int mItemHeight;
    private int mItemWidth;
    //selectedColumn,selectedRow : 当前聚焦框所在行列位置
    private int mSelectedColumn;
    private int mSelectedRow;
    /**
     * FocusCursor : 用于判断内容块是否需要上下移动
     * example: 触发内容块移动有两种情况
     * 1.当前选择框在第1行,显示行数为3,此时FocusCursor为0. 继续向上移动,FocusCursor变为 -1 ,<0.则内容块需要向上移动,
     * 并重置此时FocusCursor为0.重复
     * 2.当前选择框在第3行,显示行数为3,此时FocusCursor为2. 继续向下移动,FocusCursor变为 3 ,=3.则内容块需要向下移动,
     * 并重置此时FocusCursor为2.重复
     * 3.当前选择框在第2行,显示行数为3,此时FocusCursor为1.上下移动变为0,2.既不 <0,也不 = 3,则内容快不需要移动
     */
    private int mFocusCursor;
    //聚焦框.最好解耦出来
    private ImageView mFocusImage;
    //LastSelectedColumn LastSelectedRow : 记录上一次所选择的地方
    private int mLastSelectedColumn;
    private int mLastSelectedRow;
    //对View进行回收利用. 本质上就是一个数据容器.回收时候加进去,加载时候取出来.
    private RecycleBin mRecyle;
    //自定义监听器. 为了让外部在有需要的时候,可以进行功能上的实现.如:在进行下一个选择的时候,让选择的View产生效果.为了外部方便.解耦
    private ObserverListener mObserverListener;
    //自定义适配器,存储数据,让外部提供子View,让ViewGroup与子View关系解耦
    private AdapterTemplate mAdapter;
    //setAdapter阶段初始化时,自动生成布局所需的LayoutParams
    private LayoutParamsGenerator paramsGenerator;

    public MultiColumnLayoutDemo(Context paramContext) {
        super(paramContext);
        this.context = paramContext;
        init(paramContext, null);
        LogU.logE("1");
    }

    public MultiColumnLayoutDemo(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("2");
    }

    public MultiColumnLayoutDemo(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("4");
    }

    @TargetApi(21)
    public MultiColumnLayoutDemo(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        this.context = paramContext;
        init(paramContext, paramAttributeSet);
        LogU.logE("3");
    }

    /**
     * 根据行列号,查找adapte中数据的位置.本质就是讲二维位置转化为一维位置
     * @param row   行号
     * @param column    列号
     * @return  一维位置
     */
    private int findPostitionInAdapterByRowAndColumn(int row, int column) {
        return -1 + ((-1 + (row + 1)) * this.mColumn + (column + 1));
    }

    /**
     * 返回某一行应加载的View个数
     * @param row   行号
     * @return  某一行应加载的View个数
     */
    private int loadViewCount(int row) {
        if (row == -1 + getMaxRow())
            return this.mColumn - ((row + 1) * this.mColumn - this.mAdapter.getCount());
        return this.mColumn;
    }

    /**
     * 遍历一下自定义监听器中的方法,在需要时候进行调用,让外部可以实现功能上的需求
     */
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

    /**
     * 回收并加载View
     * @param selectedRow   当前选择的行号
     * @param selectedColumn    当前选择的列号
     * @param direction 当前移动的方向
     */
    private void recoveryAndLoad(int selectedRow, int selectedColumn, int direction) {
        switch (direction) {
            case DOWN:  //上移
                //由于RecycleBin中初始容量为0,所以需要先回收再加载
                if (selectedRow - (1 + this.mRow) >= 0) {
                    removeAndPushViews(selectedRow - (1 + this.mRow));
                    LogU.logE("after DOWN removeAndPushViews:");
                    LogU.logE("Recycle bin size : " + this.mRecyle.size());
                }
                if (selectedRow + 1 <= getMaxRow() - 1) {
                    loadAndPopViews(selectedRow + 1, selectedColumn);
                }
                break;
            case UP:    //下移
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

    //由于容器中初始时就有聚焦框这个子View,所以添加的child都放在其前面
    public void addView(View child) {
        AbsoluteLayout.LayoutParams lp = this.paramsGenerator.getParams();
        addView(child, getChildCount() - 1, lp);
    }

    public void addView(View child, AbsoluteLayout.LayoutParams lp) {
        addView(child, getChildCount() - 1, lp);
    }

    /**
     * 改变聚焦框显示隐藏状态
     */
    public void changeFocusState() {
        if (this.mFocusImage.getVisibility() == GONE)
            this.mFocusImage.setVisibility(VISIBLE);
        else if (this.mFocusImage.getVisibility() == VISIBLE) {
            this.mFocusImage.setVisibility(GONE);
        } else {

        }
    }

    /**
     * 移动逻辑:
     * 1.先判断是否到内容边缘,是的话,则不移动
     * 2.然后根据上下左右,改变mSelectedRow,mSelectedColumn的值,同时更新上一次选择的位置mLastSelectedRow,mLastSelectedColumn
     * 3.再根据FocusCursor判断内容块是否需要移动,需要的话同时触发View回收利用
     * 4.最后根据当前位置移动选择框
     * @param keyEvent
     * @return
     */
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

    /**
     * 此处就是引入allViews参数的原因.
     * 如果使用的是getChildAt()的方式去寻找,可能由于View回收利用和重绘的关系.会出现紊乱,获取出错甚至是null.
     * 于是引入一个稳定的参数
     * @return  获取上一个选择的View
     */
    public View getLastSelectedItem() {
        for (int i = 0; i < this.allViews.size(); i++) {
            View localView =  this.allViews.get(i);
            PositionTag localPositionTag = (PositionTag) localView.getTag();
            if ((localPositionTag.getRow() == this.mLastSelectedRow) && (localPositionTag.getColumn() == this.mLastSelectedColumn))
                return localView;
        }
        return null;
    }

    /**
     * @return  获取最大行数
     */
    public int getMaxRow() {
        int i = this.mAdapter.getCount() % this.mColumn;
        int j = this.mAdapter.getCount() / this.mColumn;
        if (i == 0) ;
        for (int k = 0; ; k = 1)
            return k + j;
    }

    /**
     * @return  获取当前选择的View
     */
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
        //mColumn,mRow,mItemHeight,mItemWidth从xml中初始化过来
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
        //聚焦框
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

    /**
     * 加载某一行的View
     * @param selectedRow   所需加载的行号
     * @param selectedColumn    未使用
     */
    public void loadAndPopViews(int selectedRow, int selectedColumn) {

        for (int i = getChildCount() - 2; i >= 0; i--) {
            if (((PositionTag) getChildAt(i).getTag()).getRow() == selectedRow) {
                return;
            }
        }
        int loadViewCount = loadViewCount(selectedRow);
        for (int i = 0; i < loadViewCount; i++) {
            View view = this.mAdapter.getView(findPostitionInAdapterByRowAndColumn(selectedRow, i % this.mColumn), this.mRecyle.pop());
            /**
             * 设Tag,是为了在回收时,可以根据View的Tag中的行号来确定要回收哪些View
             * View Tag - PostionTag - 记录了该View所在的行列号
             */
            view.setTag(new PositionTag(selectedRow, i % this.mColumn));
            addView(view, new AbsoluteLayout.LayoutParams(this.mItemWidth, this.mItemHeight, i * this.mItemWidth, selectedRow * this.mItemHeight));
        }
    }

    /**
     * 内容块移动
     * @param selectedRow   移动的行号
     * @param showRow   显示的行数
     * @param direction 移动的方向
     */
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
                MultiColumnLayoutDemo.this.scrollTo(0, startY + (int) (fraction * deltaY));
            }
        });
        valueAnimator.start();
    }

    /**
     * 聚焦框移动
     * @param selectedRow   行号
     * @param selectedColumn    列号
     */
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

    /**
     * 回收某一行View
     * @param selectedRow   行号
     */
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

    /**
     * 设置Adapter同时会对该ViewGroup进行所有参数的初始化.这相当是一个使用的入口.
     * 该VIewGroup的使用范例:
     * MultiColumnLayout layout = findViewById(..);
     * layout.setAdapter(adapter,list);
     * layout.setOnObserverListener(..);
     * @param adapter
     * @param visible   聚焦框是否可见
     */
    public void setAdapter(AdapterTemplate adapter, int visible) {
        if ((adapter == null) || (adapter.getCount() == 0)) {
            LogU.logE("adapter is null or list in adapter is null");
            return;
        }
        this.mAdapter = adapter;
        //初始化时应加载的VIew个数
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