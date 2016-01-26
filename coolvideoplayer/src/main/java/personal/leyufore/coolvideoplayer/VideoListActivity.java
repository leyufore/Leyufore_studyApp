package personal.leyufore.coolvideoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


import java.util.ArrayList;

import personal.leyufore.coolvideoplayer.adapter.VideoAdapter;
import personal.leyufore.coolvideoplayer.model.VideoInfo;
import personal.leyufore.coolvideoplayer.utility.Constant;
import personal.leyufore.coolvideoplayer.utility.Utility;

public class VideoListActivity extends ListActivity {

    private static final String TAG = "coolvideoplayer.VideoListActivity";

    private TextView tvTitle;

    private VideoAdapter videoAdapter = null;
    private ArrayList<VideoInfo> videolist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化窗口(启用窗口扩展功能,先启用后使用getWindow().setFeature()进行具体的设置)
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.layout_videolist);
        //自定义标题的样式
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.inc_activitys_title);

        tvTitle = (TextView) findViewById(R.id.tv_activitys_title);
        tvTitle.setText("视频播放列表");

        // 初始化界面控件并绑定数据
        initViews();
        // 设置控件事件监听
        bindViews();
    }

    private void initViews(){
        videolist = Utility.getVideosFromSD(VideoListActivity.this);
        videoAdapter = new VideoAdapter(this,videolist);
        setListAdapter(videoAdapter);
    }

    private void bindViews(){
        getListView().setOnItemClickListener(videoItemClickListener);
    }

    OnItemClickListener videoItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VideoInfo video = videolist.get(position);
            Intent playIntent = new Intent(VideoListActivity.this,MainActivity.class);

            playIntent.putExtra(Constant.PARAM_FLAG_VIDEOPATH, video.getPath());
            startActivity(playIntent);
        }
    };
    /**
     * 物理返回键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        // 物理返回键的点击值为常量4
        if (KeyEvent.KEYCODE_BACK == keyCode && 0 == event.getRepeatCount()) {

            new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.ic_dialog_alert).setTitle(
                    R.string.warning_title).setMessage(
                    R.string.warning_app_exit_yesno).setNegativeButton(
                    R.string.btn_cancle, null).setPositiveButton(
                    R.string.btn_submit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            finish();// 退出应用
                        }
                    }).show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
