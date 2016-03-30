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
     * homePage,alarmPage的使用是为了减免每一次使用HomePage.getHomePage(MainActivity.this)的使用,由于该方法是同步方法,所以避免消耗
     */
    private HomePage homePage;
    private AlarmPage alarmPage;
    public View homePage_view;
    public View alarmPage_view;

    private HomePage.HomePageEventListener homePageEventListener= new HomePage.HomePageEventListener(){

        @Override
        public void onGoToAlarm() {
            setContentView(alarmPage_view);
            MainActivity.this.viewController = ALARMPAGE_VIEW;
        }
    };

    private AlarmPage.AlarmPageEventListener alarmPageEventListener = new AlarmPage.AlarmPageEventListener() {
        @Override
        public void onBack() {
            setContentView(homePage_view);
            MainActivity.this.viewController = HOMEPAGE_VIEW;
        }
    };

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

        homePage.setHomePageEventListener(homePageEventListener);
        alarmPage.setAlarmPageEventListener(alarmPageEventListener);

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
            return homePage.dispatchKeyEvent(event);
        }else if(this.viewController == ALARMPAGE_VIEW){
//            if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) { //确认返回时,填充首页界面的View
//                if (alarmPage.getCurrentController() == AlarmPage.BACK) {
//                    setContentView(homePage_view);
//                    this.viewController = HOMEPAGE_VIEW;
//                    alarmPage.back();
//                    return true;
//                }
//            }
            return alarmPage.dispatchKeyEvent(event);
        }else{
        }
        return false;
    }



}
