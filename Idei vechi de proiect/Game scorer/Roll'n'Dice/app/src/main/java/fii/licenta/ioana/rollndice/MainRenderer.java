package fii.licenta.ioana.rollndice;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.util.concurrent.ThreadLocalRandom;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ioana on 09/03/2016.
 */
public class MainRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private OtherCube mCube;
    private float mCubeRotation;
    private long start;
    private long stop;
    private float randomX;
    private float randomY;
    private float randomZ;

    public void setStart(long start) {
        this.start = start;
    }

    public MainRenderer(Context context){
        this.context = context;
        this.mCube = new OtherCube();
        this.mCubeRotation = 0f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCube.loadGLTexture(gl, context);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);
        start = System.currentTimeMillis();
        randomX = (float)Math.random();
        randomY = (float)Math.random();
        randomZ = (float)Math.random();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -10.0f);

        //float randomRotation = (float)Math.random() * 6;

        stop = System.currentTimeMillis();

        if(stop - start < 1000){
            mCubeRotation -= 3.5f;
            gl.glRotatef(mCubeRotation, 1.f, 1.f, 1.f);
        }

        if(stop - start < 2000){
            mCubeRotation -= 3.0f;
            gl.glRotatef(mCubeRotation, 1.f, 1.f, 1.f);
        }

        if(stop - start < 2500){
            mCubeRotation -= 2.5f;
            gl.glRotatef(mCubeRotation, 1.f, 1.f, 1.f);
        }

        if(stop - start < 3500){
            mCubeRotation -= 2.0f;
            gl.glRotatef(mCubeRotation, 1.f, 1.f, 0.f);
        }

        //gl.glRotatef(mCubeRotation, 1.0f, 0.0f, 1.0f);
        //gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 0.0f);

        mCube.draw(gl);

        //mCubeRotation -= randomRotation;
    }
}
