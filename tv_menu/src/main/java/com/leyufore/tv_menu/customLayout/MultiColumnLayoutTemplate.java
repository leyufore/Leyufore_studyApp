package com.leyufore.tv_menu.customLayout;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.leyufore.tv_menu.R;
import com.leyufore.tv_menu.adapter.AbsAdapterTemplate;
import com.leyufore.tv_menu.model.PositionTag;
import com.leyufore.tv_menu.observer.DataObserver;
import com.leyufore.tv_menu.params_generate.LayoutParamsGenerator;
import com.leyufore.tv_menu.util.LogU;

import java.util.ArrayList;
import java.util.List;

/**
 * 在版本2上进行了改进:
 * 给MultiColumnTemplate,自定义Adapter应用观察者模式,使得可以使用notifyDataSetChange();
 * 该VIewGroup的使用范例:
 * MultiColumnLayout layout = findViewById(..);
 * layout.setAdapter(adapter,list);
 * layout.setOnObserverListener(..);
 * adapter.notifyDataSetChange();
 * Creator : leyufore
 * Time : 2016/03/23
 * 版本 : 学习版本3
 */
public class MultiColumnLayoutTemplate extends AbsoluteLayout {
    //移动方向常量
    public static final int DOWN = 2;
    public static final int UP = 1;
    /**
     * 保存所有的View,包括加到ViewGroup中的子View + RecycleBIn中的View,由于java引用的机制,全局只有一份对象,
     * 只是有两处引用而已.
     */
    protected List<View> allViews;
    protected Context context;
    //mColumn,mRow,mItemHeight,mItemWidth显示的行列数,以及Item的宽高.在xml中提取
    protected int mColumn;
    protected int mRow;
    protected int mItemHeight;
    protected int mItemWidth;
    //selectedColumn,selectedRow : 当前聚焦框所在行列位置
    protected int mSelectedColumn;
    protected int mSelectedRow;
    /**
     * FocusCursor : 用于判断内容块是否需要上下移动
     * example: 触发内容块移动有两种情况
     * 1.当前选择框在第1行,显示行数为3,此时FocusCursor为0. 继续向上移动,FocusCursor变为 -1 ,<0.则内容块需要向上移动,
     * 并重置此时FocusCursor为0.重复
     * 2.当前选择框在第3行,显示行数为3,此时FocusCursor为2. 继续向下移动,FocusCursor变为 3 ,=3.则内容块需要向下移动,
     * 并重置此时FocusCursor为2.重复
     * 3.当前选择框在第2行,显示行数为3,此时FocusCursor为1.上下移动变为0,2.既不 <0,也不 = 3,则内容快不需要移动
     */
    protected int mFocusCursor;
    //LastSelectedColumn LastSelectedRow : 记录上一次所选择的地方
    protected int mLastSelectedColumn;
    protected int mLastSelectedRow;
    //对View进行回收利用. 本质上就是一个数据容器.回收时候加进去,加载时候取出来.
    protected RecycleBin mRecyle;
    //自定义监听器. 为了让外部在有需要的时候,可以进行功能上的实现.如:在进行下一个选择的时候,让选择的View产生效果.为了外部方便.解耦
    protected ObserverListener mObserverListener;
    //自定义适配器,存储数据,让外部提供子View,让ViewGroup与子View关系解耦
    protected AbsAdapterTemplate mAdapter;
    //类似android里面的DataSetObserver接口.用于被观察者持有观察者的对象.使得在自定义adapter进行notifyDataChange调用时候,通知该Viewgroup进行变化
    protected DataObserver mDataObserver;

    //setAdapter阶段初始化时,自动生成布局所需的LayoutParams
    protected LayoutParamsGenerator paramsGenerator;

    public MultiColumnLayoutTemplate(Context context) {
        super(context);
        this.context = context;
        init(context, null);
        LogU.logE("1");
    }

    public MultiColumnLayoutTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
        LogU.logE("2");
    }

    public MultiColumnLayoutTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
        LogU.logE("4");
    }

    @TargetApi(21)
    public MultiColumnLayoutTemplate(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(context, attrs);
        LogU.logE("3");
    }

    /**
     * 根据行列号,查找adapte中数据的位置.本质就是讲二维位置转化为一维位置
     * @param row   行号
     * @param column    列号
     * @return  一维位置
     */
    protected int findPostitionInAdapterByRowAndColumn(int row, int column) {
        return -1 + ((-1 + (row + 1)) * this.mColumn + (column + 1));
    }

    /**
     * 返回某一行应加载的View个数
     * @param row   行号
     * @return  某一行应加载的View个数
     */
    protected int loadViewCount(int row) {
        if (row < 0 || row > getMaxRow() -1)   //小于第一行或是大于最大行则不加载
            return 0;
        if (row == -1 + getMaxRow())
            return this.mColumn - ((row + 1) * this.mColumn - this.mAdapter.getCount());
        return this.mColumn;
    }

    /**
     * 遍历一下自定义监听器中的方法,在需要时候进行调用,让外部可以实现功能上的需求
     */
    protected void observerFocusChange() {
//        LogU.logE("animation start : ");
        LogU.logE("mLastSelectedRow: " + this.mLastSelectedRow + " mLastSelectedColumn : " + this.mLastSelectedColumn);
        LogU.logE("mSelectedRow : " + this.mSelectedRow + " mSelectedColumn : " + this.mSelectedColumn);
        if ((this.mObserverListener != null) && ((this.mLastSelectedRow != this.mSelectedRow) || (this.mLastSelectedColumn != this.mSelectedColumn))) {
//            LogU.logE("allViews status:");
            for (int i = 0; i < this.allViews.size(); i++) {
                PositionTag localPositionTag = (PositionTag) ((View) this.allViews.get(i)).getTag();
//                LogU.logE(i + " " + localPositionTag.getRow() + " " + localPositionTag.getColumn());
            }
            LogU.logE("observerFocusChange allViews size : " + this.allViews.size());
            LogU.logE("getChildCount size : " + getChildCount());
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
    protected void recoveryAndLoad(int selectedRow, int selectedColumn, int direction) {
        switch (direction) {
            case DOWN:  //上移
                //由于RecycleBin中初始容量为0,所以需要先回收再加载
                if (selectedRow - (1 + this.mRow) >= 0) {
                    removeAndPushViews(selectedRow - (1 + this.mRow));
//                    LogU.logE("after DOWN removeAndPushViews:");
//                    LogU.logE("Recycle bin size : " + this.mRecyle.size());
                }
                if (selectedRow + 1 <= getMaxRow() - 1) {
                    loadAndPopViews(selectedRow + 1, selectedColumn);
                }
                break;
            case UP:    //下移
                if (selectedRow + (1 + this.mRow) <= getMaxRow() - 1) {
                    removeAndPushViews(selectedRow + (1 + this.mRow));
//                    LogU.logE("after UP removeAndPushViews:");
//                    LogU.logE("Recycle bin size : " + this.mRecyle.size());
                }
                if (selectedRow - 1 >= 0) {
                    loadAndPopViews(selectedRow - 1, selectedColumn);
                }
                break;
            default:
                return;
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
        int remainder = this.mAdapter.getCount() % this.mColumn;
        int merchant = this.mAdapter.getCount() / this.mColumn;
        return remainder == 0 ? merchant : merchant + 1;
    }

    /**
     * @return  获取当前选择的View
     */
    public View getSelectedItem() {
        for (int i = 0; i < this.allViews.size(); i++) {
            View localView = this.allViews.get(i);
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

        this.mDataObserver = new DataObserver() {
            @Override
            public void onChange() {
                /**
                 * 清空原有内容
                 * 重新配置参数
                 * 重新布局
                 */
                removeAllViews();
                if (mRecyle != null){
                    mRecyle.clear();
                }else{
                    mRecyle = new RecycleBin();
                }
                allViews.clear();
                if(paramsGenerator != null){
                    //重置初始化时自动布局生成器里面的计数
                    paramsGenerator.reset();
                }
                mLastSelectedRow = 0;
                mLastSelectedColumn = 0;
                mSelectedRow = 0;
                mSelectedColumn = 0;
                mFocusCursor = 0;
                //初始化时应加载的VIew个数
                int loadViewCount = Math.min((2 + mRow) * mColumn, mAdapter.getCount());
                int row = -1;
                for (int i = 0; i < loadViewCount; i++) {
                    if (i % mColumn == 0)
                        row++;
                    View view = mAdapter.getView(i, null);
                    view.setTag(new PositionTag(row, i % mColumn));
                    addView(view,paramsGenerator.getParams());
                    allViews.add(view);
                }
                //刷新页面后,重定位内容块.
                //由于这里是加载了不同数据,所以内容块应与最初展现时一样,展现第一页,内容块滑到0,0处
                scrollTo(0,0);
            }
        };
    }

    /**
     * 加载某一行的View
     * @param selectedRow   所需加载的行号
     * @param selectedColumn    未使用
     */
    public void loadAndPopViews(int selectedRow, int selectedColumn) {

        for (int i = getChildCount() - 1; i >= 0; i--) {
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
                MultiColumnLayoutTemplate.this.scrollTo(0, startY + (int) (fraction * deltaY));
            }
        });
        valueAnimator.start();
    }

    /**
     * 回收某一行View
     * @param selectedRow   行号
     */
    public void removeAndPushViews(int selectedRow) {
        for (int i =  getChildCount() - 1; i >= 0; i--) {
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
     * @param adapter
     */
    public void setAdapter(AbsAdapterTemplate adapter) {
        if ((adapter == null) || (adapter.getCount() == 0)) {
            LogU.logE("adapter is null or list in adapter is null");
            return;
        }
        this.mAdapter = adapter;
        this.mAdapter.setDataObserver(this.mDataObserver);
    }

    public void setOnObserverListener(ObserverListener paramObserverListener) {
        if (paramObserverListener != null)
            this.mObserverListener = paramObserverListener;
    }
}