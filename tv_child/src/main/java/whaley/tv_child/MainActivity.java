package whaley.tv_child;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import whaley.tv_child.view.AlarmPage;
import whaley.tv_child.view.HomePage;

public class MainActivity extends Activity {

    public static int ROCKET_RAISE = 0; //Handle中触发文案弹出的Message what
    private Handler handler;

    public static final int HOMEPAGE_VIEW = 0;      //  首页
    public static final int ALARMPAGE_VIEW = 1;     //闹钟
    //起始时处于HOMEPAGE_VIEW
    private int viewController = HOMEPAGE_VIEW;

    /**
     * homePage,alarmPage的使用
     */
    private HomePage homePage;
    private AlarmPage alarmPage;
    public View homePage_view;
    public View alarmPage_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogError.error("create" + viewController);



        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ROCKET_RAISE) {
                    HomePage.getHomePage(MainActivity.this).rocketRaise();
                    //定时弹出文案
                    handler.sendEmptyMessageDelayed(ROCKET_RAISE, 10000);
                }
            }
        };
        //初始时弹出文案
        handler.sendEmptyMessage(ROCKET_RAISE);


        homePage = HomePage.getHomePage(this);
        alarmPage = AlarmPage.getAlarmPage(this);
        homePage_view = homePage.getContentView();
        alarmPage_view = alarmPage.getContentView();

        //设置为首页
        setContentView(homePage_view);


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogError.error("attach to window");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogError.error("destroy");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogError.error("detach to window");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogError.error("pause");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        LogError.error("restart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogError.error("resume");}

    @Override
    protected void onStart() {
        super.onStart();
        LogError.error("start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogError.error("stop");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        if(this.viewController == HOMEPAGE_VIEW){
            if (event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
                this.finish();
                return true;
            }
            if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){ //确认闹钟时,填充闹钟界面的View
                if(homePage.getCurrentController() == HomePage.ALARM){
                    LogError.error("alarm selet");
                    setContentView(alarmPage_view);
                    this.viewController = ALARMPAGE_VIEW;
                    return true;
                }
            }
            return homePage.dispatchKeyEvent(event);
        }else if(this.viewController == ALARMPAGE_VIEW){
            if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) { //确认返回时,填充首页界面的View
                if (alarmPage.getCurrentController() == AlarmPage.BACK) {
                    setContentView(homePage_view);
                    this.viewController = HOMEPAGE_VIEW;
                    alarmPage.back();
                    return true;
                }
            }
            return alarmPage.dispatchKeyEvent(event);
        }else{
        }
        return false;
    }



}
