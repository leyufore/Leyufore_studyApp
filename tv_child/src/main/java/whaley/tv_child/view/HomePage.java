package whaley.tv_child.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import whaley.tv_child.R;

/**
 * 由于该Demo只需要一个Activity,首页界面与闹钟界面相当于只是两个View,跳转时候,只是设置setContentView的值.
 * 不进行Activity跳转.该类就是为了吧首页的View剖离出来.
 * 对外提供以下接口:
 * 1.getContentView 返回已经初始化好的contentView
 * 2.dispatchKeyEvent 返回相应的按键控制
 * 3.rocketRaise 由于触发火箭是在Activity中,所以给出了该接口
 * 4.getCurrentController 由于enter闹钟界面时,activity需要知道当前是否选择的闹钟选项,以便重新设置contentView,所以给出了该接口
 * Created by wenrule on 16/3/29.
 */
public class HomePage {
    //设计成单例模式,由于里面提供的ContentView可以被重复使用
    private static HomePage homePage;

    public static HomePage getHomePage(Context context) {
        if (homePage == null) {
            synchronized (HomePage.class) {
                homePage = new HomePage(context);
            }
        } else {
            if (homePage.context == context) {
            } else {
                synchronized (HomePage.class){
                    homePage = new HomePage(context);
                }
            }
        }
        return homePage;
    }

    private Context context;
    private ViewGroup contentView;

    //方向
    /**
     * 此处不用final设置为常量的话,switch中是无法使用的
     * learner : leyufore
     */
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int OK = 4;
    //当前聚焦选项
    public static final int TUIJIAN = 0;
    public static final int SHOUCANG = 1;
    public static final int DONGHUA = 2;
    public static final int ERGE = 3;
    public static final int NIGHT = 4;
    public static final int ALARM = 5;

    //日间,夜间
    public static final int CONTROLL_DAY = 1;
    public static final int CONTROLL_NIGHT = 2;

    private int[] viewsImageId = {  //选项的图片资源id
            R.drawable.item_shoucang,
            R.drawable.item_shoucang,
            R.drawable.item_donghua,
            R.drawable.item_erge,
            R.drawable.night,
            R.drawable.alarm
    };
    private ImageView[] views = new ImageView[6];
    private AbsoluteLayout.LayoutParams[] focusLp = {
            new AbsoluteLayout.LayoutParams(91, 131, 75, 210),
            new AbsoluteLayout.LayoutParams(91, 131, 536, 210),
            new AbsoluteLayout.LayoutParams(91, 131, 1016, 210),
            new AbsoluteLayout.LayoutParams(91, 131, 1485, 210),
            new AbsoluteLayout.LayoutParams(91, 131, 22, 799),
            new AbsoluteLayout.LayoutParams(91, 131, 1608, 799)
    };
    private ImageView[] focusViews = new ImageView[6];
    private int controller;

    private AbsoluteLayout.LayoutParams[] viewsLp = {
            new AbsoluteLayout.LayoutParams(410, 717, 24, 249),
            new AbsoluteLayout.LayoutParams(410, 717, 494, 249),
            new AbsoluteLayout.LayoutParams(410, 717, 954, 249),
            new AbsoluteLayout.LayoutParams(410, 717, 1414, 249),
            new AbsoluteLayout.LayoutParams(168, 222, 83, 842),
            new AbsoluteLayout.LayoutParams(168, 222, 1668, 842)
    };
    //火箭(文案)
    private ViewGroup rocket;

    //上下聚焦切换时上方控件位置的记录
    private int upDownChangeRecord = 0;

    //初始化HomePage (e.g 进行界面的初始化)
    private HomePage(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        contentView = (ViewGroup) inflater.inflate(R.layout.activity_main, null);

        rocket = (ViewGroup) contentView.findViewById(R.id.rocket);
        init();

    }

    private void init() {    //初始化
        AnimationDrawable animDrawable;
        for (int i = 0; i < 6; i++) {
            views[i] = new ImageView(context);
            views[i].setImageResource(viewsImageId[i]);
            if (i == 0) { //第一个控件保持原图大小(处于聚焦状态)

            } else if (i == 4 || i == 5) {
                //设置下方两控件原始大小为原图的75%
                setTwoAlarmSmallAnim(views[i]);
            } else {  //设置上方其余三个控件大小为原图的66%(处于未聚焦状态)
                setItemSmallAnim(views[i]);
            }
            contentView.addView(views[i], viewsLp[i]);

            focusViews[i] = new ImageView(context);
            focusViews[i].setBackgroundResource(R.drawable.frame_anim_balloon);
            animDrawable = (AnimationDrawable) focusViews[i].getBackground();
            if (i == 0) { //显示热气球(处于聚焦状态)

            } else if (i == 4 || i == 5) { //下方两控件的热气球隐藏(处于未聚焦状态)
                setTwoFocusSmallAnim(focusViews[i]);
                focusViews[i].setVisibility(View.GONE);
            } else {  //上方其余三个控件的热气球隐藏(处于未聚焦状态)
                focusViews[i].setVisibility(View.GONE);
            }
            contentView.addView(focusViews[i], focusLp[i]);
            animDrawable.start();
        }


    }

    /**
     * 设置下方控件处于聚焦状态,大小为原图的100%,缩放中心为底边中心
     * @param view
     */
    private void setTwoLargeAnim(View view) {
        view.setPivotX(84f);
        view.setPivotY(222f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 设置下方控件处于未聚焦状态,大小为原图的75%,缩放中心为底边中心
     * @param view
     */
    private void setTwoAlarmSmallAnim(View view) {
        view.setPivotX(84f);
        view.setPivotY(222f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0.75f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0.75f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 设置上方控件处于未聚焦状态,大小为原图的100%,缩放中心为原图中心
     * @param view
     */
    private void setItemLargeAnim(View view) {
        view.setPivotX(205f);
        view.setPivotY(358.5f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 设置上方控件处于未聚焦状态,大小为原图的66%,缩放中心为原图中心
     * @param view
     */
    private void setItemSmallAnim(View view) {
        view.setPivotX(205f);
        view.setPivotY(358.5f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0.66f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0.66f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 设置下方两个控件的热气球起始状态大小为原图的80%
     * @param view
     */
    private void setTwoFocusSmallAnim(View view) {
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);
    }

    /**
     * 日间的控制逻辑
     */
    private boolean controllManagerInDay(int currentController, int operation) {
        switch (operation) {
            case UP:
                switch (currentController) {
                    case TUIJIAN:   //推荐
                        break;
                    case SHOUCANG:  //收藏
                        break;
                    case DONGHUA:   //动画
                        break;
                    case ERGE:  //儿歌
                        break;
                    case ALARM: //闹钟
                        setTwoAlarmSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = this.upDownChangeRecord;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        break;
                    default:
                        break;
                }
                break;
            case DOWN:
                switch (currentController) {
                    case TUIJIAN:   //推荐
                    case SHOUCANG:  //收藏
                    case DONGHUA:   //动画
                    case ERGE:  //儿歌
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        this.upDownChangeRecord = currentController;
                        currentController = ALARM;
                        setTwoLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case ALARM: //闹钟
                        return true;
                    default:
                        break;
                }
                break;
            case LEFT:
                switch (currentController) {
                    case TUIJIAN:   //推荐
                        return true;
                    case SHOUCANG:  //收藏
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = TUIJIAN;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case DONGHUA:   //动画
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = SHOUCANG;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case ERGE:  //儿歌
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = DONGHUA;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case ALARM: //闹钟
                        break;
                    default:
                        break;
                }
                break;
            case RIGHT:
                switch (currentController) {
                    case TUIJIAN:   //推荐
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = SHOUCANG;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case SHOUCANG:  //收藏
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = DONGHUA;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case DONGHUA:   //动画
                        setItemSmallAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.GONE);
                        currentController = ERGE;
                        setItemLargeAnim(views[currentController]);
                        focusViews[currentController].setVisibility(View.VISIBLE);
                        //重新设置当前聚焦项
                        this.controller = currentController;
                        return true;
                    case ERGE:  //儿歌
                        return true;
                    case ALARM: //闹钟
                        return true;
                    default:
                        break;
                }
                break;
            case OK:
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 夜间的控制逻辑
     */
    private boolean controllManagerInNight(int currentController, int operation) {
        switch (operation) {
            case UP:
                switch (currentController) {
                    case TUIJIAN:   //推荐
                        break;
                    case SHOUCANG:  //收藏
                        break;
                    case DONGHUA:   //动画
                        break;
                    case ERGE:  //儿歌
                        break;
                    case NIGHT: //晚间
                        break;
                    case ALARM: //闹钟
                        break;
                    default:
                        break;
                }
                break;
            case DOWN:
                break;
            case LEFT:
                break;
            case RIGHT:
                break;
            case OK:
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 根据白天还是黑夜调用日间或夜间的控制逻辑
     *
     * @param currentController 当前聚焦选项
     * @param direction         操作方向
     * @param day               白天还是黑夜
     */
    private boolean controllManager(int currentController, int direction, int day) {
        if (day == CONTROLL_DAY) {
            return controllManagerInDay(currentController, direction);
        } else {
            return controllManagerInNight(currentController, direction);
        }
    }
    //火箭文案弹出
    public void rocketRaise() {
        rocket.clearAnimation();

        AnimationDrawable animDrawable = (AnimationDrawable) rocket.findViewById(R.id.helicopter).getBackground();
        animDrawable.start();
        Animation animation = new TranslateAnimation(0, -2100, 0, 0);
        animation.setDuration(6000);
        rocket.setAnimation(animation);

        Log.e("tv_child", rocket.getMeasuredWidth() + " " + rocket.getMeasuredHeight());
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                return controllManager(this.controller, UP, CONTROLL_DAY);
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return controllManager(this.controller, DOWN, CONTROLL_DAY);
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return controllManager(this.controller, LEFT, CONTROLL_DAY);
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return controllManager(this.controller, RIGHT, CONTROLL_DAY);
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return controllManager(this.controller, OK, CONTROLL_DAY);
            default:
                break;
        }
        return false;
    }

    public View getContentView(){
        return contentView;
    }

    public int getCurrentController(){
        return controller;
    }


}
