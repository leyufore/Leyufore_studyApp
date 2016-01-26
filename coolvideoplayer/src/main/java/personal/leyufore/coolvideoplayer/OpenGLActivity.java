package personal.leyufore.coolvideoplayer;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;

import personal.leyufore.coolvideoplayer.render.OpenGL3DRender;

public class OpenGLActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    //private RenderBase mCurrentRender = null;
    private Renderer renderer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        renderer = new OpenGL3DRender(this);
        if(renderer!=null){
            mGLSurfaceView = new GLSurfaceView(this);
            mGLSurfaceView.setRenderer(renderer);

            setContentView(mGLSurfaceView);
        }
    }
    @Override
    protected void onResume(){
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();

        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();

        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent playIntent = new Intent(this,VideoListActivity.class);
        /*FLAG_ACTIVITY_CLEAR_TOP 查看doc文档可知:
        If set, and the activity being launched is already running in the
        current task, then instead of launching a new instance of that activity,
        all of the other activities on top of it will be closed and this Intent
        will be delivered to the (now on top) old activity as a new Intent.

        For example, consider a task consisting of the activities: A, B, C, D.If D calls
        startActivity() with an Intent that resolves to the component
        of activity B, then C and D will be finished and B receive the given Intent,
        resulting in the stack now being: A, B.
        */
        playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(playIntent);
        finish();
        return true;
    }
}
