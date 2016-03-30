package whaley.tv_child.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.Image;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import whaley.tv_child.LogError;
import whaley.tv_child.R;

/**
 * Created by wenrule on 16/3/29.
 */
public class AlarmPage {
    //设计成单例模式,由于里面提供的ContentView可以被重复使用
    private static AlarmPage alarmPage;

    public static AlarmPage getAlarmPage(Context context) {
        if (alarmPage == null) {
            synchronized (AlarmPage.class) {
                alarmPage = new AlarmPage(context);
            }
        } else {
            if (alarmPage.context == context) {
            } else {
                synchronized (AlarmPage.class) {
                    alarmPage = new AlarmPage(context);
                }
            }
        }
        return alarmPage;
    }

    private Context context;
    private ViewGroup contentView;

    private  AlarmPageEventListener alarmPageEventListener;

    public interface AlarmPageEventListener {
        void onBack();
    }

    public void setAlarmPageEventListener(AlarmPageEventListener alarmPageEventListener){
        if(alarmPageEventListener != null){
            this.alarmPageEventListener = alarmPageEventListener;
        }
    }

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

    //选项
    public static final int CLOSE = 0;
    public static final int TIME_ONE = 1;
    public static final int TIME_TWO = 2;
    public static final int TIME_TRHEE = 3;
    public static final int TIME_CUSTOM = 4;
    public static final int BACK = 5;
    //当前聚焦选项
    private int controller;

    //上下聚焦切换时上方控件位置的记录
    private int upDownChangeRecord = 0;

    private ViewGroup[] viewGroups = new ViewGroup[5];
    private ImageView[] bgGroups = new ImageView[5];
    private ImageView[] focusGroups = new ImageView[5];
    private ImageView focus_stroke;
    private ImageView operation_prompt;
    private ImageView custom_up;
    private ImageView custom_down;

    private int customTime = -1;
    private TextView custom_time_text;

    public static final int EDIT = 0;
    public static final int NORMAL = 1;

    private int timeCustomState = NORMAL;

    private int lastSelected = CLOSE;

    private AlarmPage(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        contentView = (ViewGroup) inflater.inflate(R.layout.alarm_page, null);

        init();
    }

    public void init() {
        viewGroups[0] = (ViewGroup) contentView.findViewById(R.id.close);
        viewGroups[1] = (ViewGroup) contentView.findViewById(R.id.alarm_item_1);
        viewGroups[2] = (ViewGroup) contentView.findViewById(R.id.alarm_item_2);
        viewGroups[3] = (ViewGroup) contentView.findViewById(R.id.alarm_item_3);
        viewGroups[4] = (ViewGroup) contentView.findViewById(R.id.alarm_item_custom);

        bgGroups[0] = (ImageView) contentView.findViewById(R.id.alarm_item_bg_0);
        bgGroups[1] = (ImageView) contentView.findViewById(R.id.alarm_item_bg_1);
        bgGroups[2] = (ImageView) contentView.findViewById(R.id.alarm_item_bg_2);
        bgGroups[3] = (ImageView) contentView.findViewById(R.id.alarm_item_bg_3);
        bgGroups[4] = (ImageView) contentView.findViewById(R.id.alarm_item_bg_4);

        focusGroups[0] = (ImageView) contentView.findViewById(R.id.alarm_item_focus_0);
        focusGroups[1] = (ImageView) contentView.findViewById(R.id.alarm_item_focus_1);
        focusGroups[2] = (ImageView) contentView.findViewById(R.id.alarm_item_focus_2);
        focusGroups[3] = (ImageView) contentView.findViewById(R.id.alarm_item_focus_3);
        focusGroups[4] = (ImageView) contentView.findViewById(R.id.alarm_item_focus_4);

        operation_prompt = (ImageView) contentView.findViewById(R.id.operation_prompt);

        focus_stroke = (ImageView) contentView.findViewById(R.id.focus_stroke);
        focus_stroke.setVisibility(View.GONE);


        custom_up = (ImageView) contentView.findViewById(R.id.up);
        custom_down = (ImageView) contentView.findViewById(R.id.down);
        custom_up.setVisibility(View.GONE);
        custom_down.setVisibility(View.GONE);

        custom_time_text = (TextView) contentView.findViewById(R.id.custom_time_text);

        for (int i = 0; i < 5; i++) {
            if (i == 0) {

            } else if (i == 4) {
                setTimeCustomLostFocus(viewGroups[i], bgGroups[i], focusGroups[i], operation_prompt);
                setItemLostSelected(focusGroups[i]);
            } else {
                setItemLostFocus(viewGroups[i], bgGroups[i], focusGroups[i]);
                setItemLostSelected(focusGroups[i]);
            }
        }


    }

    /**
     * 默认bg的90%大小显示,bg为默认选项背景.(未聚焦状态)
     *
     * @param view
     */
    public void setItemSmall(View view) {
        view.setPivotX(0f);
        view.setPivotY(109f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0.9f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0.9f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 聚焦时默认大小放大至原图大小,替换聚焦bg
     *
     * @param view
     */
    public void setItemLarge(View view) {
        view.setPivotX(0f);
        view.setPivotY(109f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    /**
     * 设置上方控件聚焦状态 (适用于前四个)
     *
     * @param absoViewGroup Item的View
     * @param bgView        背景View
     * @param focusView     打钩的图标
     */
    public void setItemFocus(View absoViewGroup, View bgView, View focusView) {
        setItemLarge(absoViewGroup);
        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_focus_bg);
//        focusView.setVisibility(View.VISIBLE);

    }

    /**
     * 设置上方控件未聚焦状态 (适用于前四个)
     *
     * @param absoViewGroup Item的View
     * @param bgView        背景View
     * @param focusView     打钩的图标
     */
    public void setItemLostFocus(View absoViewGroup, View bgView, View focusView) {
        setItemSmall(absoViewGroup);
        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_default_bg);
//        focusView.setVisibility(View.GONE);
    }

    public void setItemSelected(View focusView) {
//        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_focus_bg);
        focusView.setVisibility(View.VISIBLE);
    }

    public void setItemLostSelected(View focusView) {
//        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_default_bg);
        focusView.setVisibility(View.GONE);
    }

    public void setTimeCustomFocus(View absoViewGroup, View bgView, View focusView, View prompt) {
        setItemLarge(absoViewGroup);
        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_focus_bg);
//        focusView.setVisibility(View.VISIBLE);
        prompt.setVisibility(View.VISIBLE);
    }

    public void setTimeCustomLostFocus(View absoViewGroup, View bgView, View focusView, View prompt) {
        setItemSmall(absoViewGroup);
        bgView.setBackgroundResource(R.drawable.page_alarm_alarm_item_default_bg);
//        focusView.setVisibility(View.GONE);
        prompt.setVisibility(View.GONE);
    }

    public void setBackItemFocus() {
        focus_stroke.setVisibility(View.VISIBLE);
    }

    public void setBackItemLostFocus() {
        focus_stroke.setVisibility(View.GONE);
    }

    public boolean controllManager(int controller, int operation) {
        switch (controller) {
            case CLOSE:
                switch (operation) {
                    case UP:
                    case LEFT:
                        return true;
                    case DOWN:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        upDownChangeRecord = controller;
                        controller = BACK;
                        this.controller = controller;
                        setBackItemFocus();
                        return true;
                    case RIGHT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_ONE;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case OK:
                        if(lastSelected != controller) {
                            setItemLostSelected(focusGroups[lastSelected]);
                            setItemSelected(focusGroups[controller]);
                            lastSelected = controller;
                        }
                    default:
                        break;
                }
                break;
            case TIME_ONE:
                switch (operation) {
                    case UP:
                        return true;
                    case DOWN:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        upDownChangeRecord = controller;
                        controller = BACK;
                        this.controller = controller;
                        setBackItemFocus();
                        return true;
                    case LEFT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = CLOSE;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case RIGHT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_TWO;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case OK:
                        if(lastSelected != controller) {
                            setItemLostSelected(focusGroups[lastSelected]);
                            setItemSelected(focusGroups[controller]);
                            lastSelected = controller;
                        }
                    default:
                        break;
                }
                break;
            case TIME_TWO:
                switch (operation) {
                    case UP:
                        return true;
                    case DOWN:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        upDownChangeRecord = controller;
                        controller = BACK;
                        this.controller = controller;
                        setBackItemFocus();
                        return true;
                    case LEFT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_ONE;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case RIGHT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_TRHEE;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case OK:
                        if(lastSelected != controller) {
                            setItemLostSelected(focusGroups[lastSelected]);
                            setItemSelected(focusGroups[controller]);
                            lastSelected = controller;
                        }
                    default:
                        break;
                }
                break;
            case TIME_TRHEE:
                switch (operation) {
                    case UP:
                        return true;
                    case DOWN:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        upDownChangeRecord = controller;
                        controller = BACK;
                        this.controller = controller;
                        setBackItemFocus();
                        return true;
                    case LEFT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_TWO;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case RIGHT:
                        setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        controller = TIME_CUSTOM;
                        this.controller = controller;
                        setTimeCustomFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller], operation_prompt);
                        return true;
                    case OK:
                        if(lastSelected != controller) {
                            setItemLostSelected(focusGroups[lastSelected]);
                            setItemSelected(focusGroups[controller]);
                            lastSelected = controller;
                        }
                    default:
                        break;
                }
                break;
            case TIME_CUSTOM:
                if(timeCustomState == NORMAL){  //自定义时间处于正常状态
                    switch (operation){
                        case UP:
                        case RIGHT:
                            return true;
                        case DOWN:
                            setItemLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                            upDownChangeRecord = controller;
                            controller = BACK;
                            this.controller = controller;
                            setBackItemFocus();
                            return true;
                        case LEFT:
                            setTimeCustomLostFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller],operation_prompt);
                            controller = TIME_TRHEE;
                            this.controller = controller;
                            setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                            return true;
                        case OK:
                            if(lastSelected == TIME_CUSTOM){
                                setItemLostSelected(focusGroups[TIME_CUSTOM]);
                            }
                            timeCustomState = EDIT;
                            if(customTime != 5) {
                                custom_down.setVisibility(View.VISIBLE);
                            }
                            if(customTime != 120) {
                                custom_up.setVisibility(View.VISIBLE);
                            }
                            if(customTime == -1){
                                customTime = 70;
                                custom_time_text.setText(customTime + "分钟");
                            }
                            return true;
                        default:
                            break;
                    }
                }else { //自定义时间处于编辑状态
                    switch (operation){
                        case UP:
                            if(customTime == 120)
                                return true;
                            customTime += 5;
                            if(customTime == 120){
                                custom_up.setVisibility(View.GONE);
                            }
                            custom_time_text.setText(customTime + "分钟");
                            return true;
                        case DOWN:
                            if(customTime == 5)
                                return true;
                            customTime -= 5;
                            if(customTime == 5){
                                custom_down.setVisibility(View.GONE);
                            }
                            custom_time_text.setText(customTime + "分钟");
                            return true;
                        case LEFT:
                        case RIGHT:
                            return true;
                        case OK:
                            setItemLostSelected(focusGroups[lastSelected]);
                            custom_up.setVisibility(View.GONE);
                            custom_down.setVisibility(View.GONE);
                            setItemSelected(focusGroups[TIME_CUSTOM]);
                            timeCustomState = NORMAL;
                            lastSelected = TIME_CUSTOM;
                            return true;
                        default:
                            break;
                    }
                }
                break;
            case BACK:
                switch (operation) {
                    case UP:
                        setBackItemLostFocus();
                        controller = upDownChangeRecord;
                        this.controller = controller;
                        setItemFocus(viewGroups[controller], bgGroups[controller], focusGroups[controller]);
                        return true;
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        return true;
                    case OK:
                        if(alarmPageEventListener != null){
                            alarmPageEventListener.onBack();
                            back();
                        }
                        return true;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                return controllManager(this.controller, UP);
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return controllManager(this.controller, DOWN);
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return controllManager(this.controller, LEFT);
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return controllManager(this.controller, RIGHT);
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return controllManager(this.controller, OK);
            default:
                break;
        }
        return false;
    }

    public void back() {
        setBackItemLostFocus();
        this.controller = lastSelected;
        if(this.controller == TIME_CUSTOM){
            setTimeCustomFocus(viewGroups[lastSelected], bgGroups[lastSelected], focusGroups[lastSelected],operation_prompt);
        }else {
            setItemFocus(viewGroups[lastSelected], bgGroups[lastSelected], focusGroups[lastSelected]);
        }
    }

    public int getCurrentController() {
        return controller;
    }

    public View getContentView() {
        return contentView;
    }
}
