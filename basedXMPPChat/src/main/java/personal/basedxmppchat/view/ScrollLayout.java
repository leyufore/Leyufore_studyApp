package personal.basedxmppchat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Vector;

public class ScrollLayout extends ViewGroup {

	/**
	 * 两个疑惑:
     * 1.为什么有些数据需要使用final.经测试,这些数据在每次处理事件时都会改变了自身的值
	 * 2.对于该ScrollLayout如何保证其不会影响Tab页面的点击和竖向滑动
     * learner : leyufore
	 */

	private Scroller mScroller;		//用于实现Tab页面的切换
	private VelocityTracker mVelocityTracker;	//速度跟踪度,用于除了让移动位置大于半屏时跳转,当滑动较快时,也会进行跳转
	private int mCurScreen;	//当前Tab页面
	private int mDefaultScreen = 0;	//默认最初进入Activity时显示的Tab页面
	private static final int TOUCH_STATE_REST = 0;	//触摸时页面处于不动的状态
	private static final int TOUCH_STATE_SCROLLING = 1;	//触摸时页面处于滑动的状态
	private static final int SNAP_VELOCITY = 500;	//单元速度
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;	//触摸滑动起效距离
	private float mLastMotionX;	//最后的手势位置
	private int sensitivity = 30;	//灵敏度
	private boolean spring;
	private Vector<LayoutChangeListener> listeners;

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;
        /**
         * getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
		 * learner : leyufore
         */
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		listeners = new Vector<LayoutChangeListener>();
	}

	public void addChangeListener(LayoutChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
            /**
             * View.GONE -- This view is invisible, and it doesn't take any space for layout purpose
             * 译:该VIew不可见,且不占据任何空间.
             */
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * onMeasure中的widthMeasureSpec,heightMeasureSpec为上级容器为其推荐(期望)的宽高,以及计算模式
         * 知识点:某个View,ViewGroup的大小由其上级容器期望的参数和自身的参数共同作用确定.
         */
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * width,widthMode 上级容器推荐的宽,以及计算模式
         */
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        /**
         * ViewGroup会为childView指定测量模式，下面简单介绍下三种测量模式：
         * EXACTLY：表示设置了精确的值，一般当childView设置其宽、高为精确值、match_parent时，ViewGroup会将其设置为EXACTLY；
         * AT_MOST：表示子布局被限制在一个最大值内，一般当childView设置其宽、高为wrap_content时，ViewGroup会将其设置为AT_MOST；
         * UNSPECIFIED：表示子布局想要多大就多大，一般出现在AadapterView的item的heightMode中、ScrollView的childView的heightMode中；此种模式比较少见。
         */
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
        /**
         * 现在需要childView和scrollLayout的宽高一致,加上ScollLayout的测量模式已经为EXCTLY(上级容器必须为精确值或match_parent),
         * 所以测量childView时,将该ViewGroup的上级容器的推荐宽高以及计算模式赋值给childView的measure即可.
         */
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
            /**
             * meausure() 计算View的大小.从源码中可以看见,其中会调用View的onMeasure()方法
             */
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

    /**
     * 用于手指松开时,判断视图是否过了屏幕的一半,以判断滑向哪个Tab页面
     */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		int lastIndex = mCurScreen;
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
            /**
             * invalidate,postInvalidate都用来刷新视图
             * learner : leyufore
             */
			invalidate(); // Redraw the layout
		}
		for (LayoutChangeListener listener : listeners)
			listener.doChange(lastIndex, whichScreen);
	}

    /**
     * 用于刚进入ContacterMainActivity时,利用View.scrollTo()使视图移动到第二个Tab页面.此处与Scroll无关.
     * learner : leyufore
     */
	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		final float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
            /**
             * 当画面正在进行自动滑动去特定屏幕时,手指触摸移动时,应暂停该自动移动 (设计思路:类似网站中轮播效果的实现)
             * learner : leyufore
             */
				mScroller.abortAnimation();
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			if (Math.abs(deltaX) > sensitivity) {
				// 左滑动为正数、右为负数
				if (spring) {
					scrollBy(deltaX, 0);
					mLastMotionX = x;
				} else {
                    /**
                     * 此处的spring没有设置过.所以不明白这里有什么作用
                     * learner : leyufore
                     */
                    /**
                     * 画面随着手指移动而移动
                     * learner : leyufore
                     */
					final int childCount = getChildCount();
					boolean max = mCurScreen < childCount - 1;
					boolean min = mCurScreen > 0;
					boolean canMove = deltaX > 0 ? (max ? true : false)
							: (min ? true : false);
					if (canMove) {
						scrollBy(deltaX, 0);
						mLastMotionX = x;
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
            /**
             * VelocityTracker -- 速度跟踪器
             * 此处该类用于当用户在屏幕上快速滑动时,跳转到前后界面.
             * 处于快速滑动时,判断速率与SNAP_VELOCITY,速率较快时则跳转.此时,不以滑动距离来判断.
             *
             * 当触摸过程结束时,回收VelocityTracker.触摸开始时,重新创建.
             *
             * learner : leyufore
             */
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
        Log.e("InterceptTouchEvent","xixihaha");
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST))
			return true;
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			Log.e("final int xDiff",String.valueOf(xDiff));
			if (xDiff > mTouchSlop)
				mTouchState = TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		/**
		 * 状态设置主要在ACTION_MOVE时候,才会设置TOUCH_STATE_SCROLLING.ACTION_DOWN时候不会设置.
         * 这样一个好处时,只有正在判断出处于水平触摸滑动时,才会进行垂直锁定(e.g. 这里会拦截事件,不传递给
         * 子View.因此ListView,ExpandableView中的上下移动不会进行)
         *
         * 疑惑 : 子VIew中的上下移动怎么进行水平方向上的锁定
         * learner : leyufore
		 */
		return mTouchState != TOUCH_STATE_REST;
	}

}