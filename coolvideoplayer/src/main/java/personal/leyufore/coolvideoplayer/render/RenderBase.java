package personal.leyufore.coolvideoplayer.render;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;
import android.view.MotionEvent;

abstract public class RenderBase implements Renderer {
    protected Context mContext;

    public RenderBase(Context context) {
        mContext = context;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent event) {
        return false;
    }
}
