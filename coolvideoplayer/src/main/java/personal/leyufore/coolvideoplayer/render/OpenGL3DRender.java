package personal.leyufore.coolvideoplayer.render;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;

import personal.leyufore.coolvideoplayer.R;
import personal.leyufore.coolvideoplayer.utility.Utility;

public class OpenGL3DRender extends RenderBase {

    private float rotx;
    private float roty;
    private float rotz;

    private int mTextureID[];


    private float box[] = {
            // FRONT
            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            // BACK
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            // LEFT
            -0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            // RIGHT
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            // TOP
            -0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            // BOTTOM
            -0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f, -0.5f, -0.5f,
    };

    private float texCoords[] = {
            // FRONT
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            // BACK
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            // LEFT
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            // RIGHT
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            // TOP
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            // BOTTOM
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };

    private FloatBuffer mCubeVetexBuffer;
    private FloatBuffer mCubeTexBuffer;

    public OpenGL3DRender(Context context) {
        super(context);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCubeVetexBuffer = Utility.createFloatBuffer(box);
        mCubeTexBuffer = Utility.createFloatBuffer(texCoords);

        loadBitmapTex(gl, R.raw.opengl3d);

        gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
        gl.glClearDepthf(1.0f); // Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // Really Nice Perspective Calculations
    }

    public void onDrawFrame(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // Clear Screen And Depth Buffer
        gl.glLoadIdentity();
        // Cube
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mCubeVetexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCubeTexBuffer);

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(rotx, 1, 0, 0);
        gl.glRotatef(roty, 0, 1, 0);
        gl.glRotatef(rotz, 0, 0, 1);

        // FRONT AND BACK
        //gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        //gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

        // LEFT AND RIGHT
        //gl.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        //gl.glNormal3f(1.0f, 0.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

        // TOP AND BOTTOM
        //gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        //gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        rotx += 0.2f;
        roty += 0.3f;
        rotz += 0.4f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0,0,width,height);                       // Reset The Current Viewport

        gl.glMatrixMode(GL10.GL_PROJECTION);                        // Select The Projection Matrix
        gl.glLoadIdentity();                                   // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f,(float)width/(float)height,0.1f,100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);                         // Select The Modelview Matrix
        gl.glLoadIdentity();
    }

    private void loadBitmapTex(GL10 gl, int res){
        Bitmap bmp = Utility.getTextureFromBitmapResource(mContext, res);

        mTextureID = new int[1];
        gl.glGenTextures(1, mTextureID, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID[0]);

        // Use Nearest for performance.
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);


        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
        return;
    }
}
