package personal.leyufore.coolvideoplayer;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

import personal.leyufore.coolvideoplayer.utility.Constant;


//这里可以使用VideoPlayer替代.这里的MediaPlayer+SurfaceView可以不采用.

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "MainActivity";
    //视频路径
    private String mVideoPath = null;
    //视频显示尺寸
    private int nHeight = 0;
    private int nWidth = 0;
    //声明控制按钮
    ImageButton ibtn_play;
    ImageButton ibtn_eject;
    ImageButton ibtn_openlist;
    //视频播放相关
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        parseParam();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseMediaPlayer();

        mSurfaceView = null;
        mSurfaceHolder = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initViews() {
        ibtn_eject = (ImageButton) findViewById(R.id.ibtn_main_eject);
        ibtn_openlist = (ImageButton) findViewById(R.id.ibtn_main_openlist);
        ibtn_play = (ImageButton) findViewById(R.id.ibtn_main_play);

        //设置窗口为未知像素模式,此处不知道有什么作用
        getWindow().setFormat(PixelFormat.UNKNOWN);

        mSurfaceView = (SurfaceView) findViewById(R.id.sv_main_screen);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        // Deprecated. this is ignored, this value is set automatically when needed.Android3.0以上会自动设置，但是为了兼容还需设置
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private void parseParam() {
        mVideoPath = getIntent().getStringExtra(Constant.PARAM_FLAG_VIDEOPATH);
    }

    //影片播放完的监听器
    MediaPlayer.OnCompletionListener videoPlayCompletedListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
            ibtn_play.setBackgroundResource(R.drawable.btn_mp_play);
        }
    };

    MediaPlayer.OnPreparedListener videoPlayPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //获取视频的宽高
            nHeight = mp.getVideoHeight();
            nWidth = mp.getVideoWidth();
            if(0 != nHeight && 0 != nWidth){
                //根据视频宽高设置surfaceView的宽高
                mSurfaceHolder.setFixedSize(nWidth,nHeight);
                mp.start();
            }
        }
    };

    //自定义影片播放路径获取函数
    private void getVideoPath(){
        Intent videoselectIntent = new Intent(MainActivity.this,VideoListActivity.class);
        videoselectIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(videoselectIntent);

        finish();
    }


    //自定义影片播放函数
    private void playVideo() {
        mMediaPlayer = new MediaPlayer();

        try {
            /*
            MediaPlayer在调用start(),pause()等前,需要先装在音频.有两种方法:
            1.create();
            2.setDataSource();设置数据源 prepare()准备音频,真正的装在音频
            mMediaPlayer.setDataSource(mVideoPath);
            */
            /*
            Sets the audio stream type for this MediaPlayer. See {@link AudioManager}
            for a list of stream types. Must call this method before prepare() or
            prepareAsync() in order for the target stream type to become effective
            thereafter.
            */
            // 设置音频类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 让SurfaceView进行画面显示
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnCompletionListener(videoPlayCompletedListener);
            mMediaPlayer.setOnPreparedListener(videoPlayPreparedListener);


        } catch (IOException e) {
            Log.w(TAG, "Unable to open video: " + mVideoPath, e);
            e.printStackTrace();
        }
    }

    //使用完MediaPlayer后,要进行资源的释放.不然过多的mediaPlayer实例会导致异常
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();

            mMediaPlayer = null;
        }
    }
    //控制按钮点击事件处理函数
    public void onControlButtonClickHandler(View target){
        if (null == mVideoPath || 0 == mVideoPath.length()) {
            getVideoPath();
            return;
        }

        switch(target.getId()){
            //播放
            case R.id.ibtn_main_play:
                if(null != mMediaPlayer){   //当前处于正在播放的状态
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        ibtn_play.setBackgroundResource(R.drawable.btn_mp_play);

                    }else{
                        mMediaPlayer.start();
                        ibtn_play.setBackgroundResource(R.drawable.btn_mp_pause);
                    }
                }else{
                    playVideo();
                }
                break;

            //停止播放
            case R.id.ibtn_main_eject:
                releaseMediaPlayer();
                ibtn_play.setBackgroundResource(R.drawable.btn_mp_play);
                break;

            //打开视频播放列表
            case R.id.ibtn_main_openlist:
                releaseMediaPlayer();
                getVideoPath();
                break;

        }

    }
}
