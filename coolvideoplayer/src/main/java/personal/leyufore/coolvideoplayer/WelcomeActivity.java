package personal.leyufore.coolvideoplayer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import static android.view.animation.Animation.*;

public class WelcomeActivity extends Activity implements AnimationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_welcome);

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fade);
        anim.setAnimationListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.iv_welcome_img);
        imageView.startAnimation(anim);
        Log.v("haha","start");
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.v("animation", "end");
        startActivity(new Intent(this, OpenGLActivity.class));
        //AndroidSDK2.0后加入 实现Activity之间切换动画效果支持
        //overridePendingTransition(int enterAnim, int exitAnim)
        //enterAnim:下一个Activity的进入动画效果
        //exitAnim:当前Activity退出的动画效果
        overridePendingTransition(R.anim.fade,R.anim.hold);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
